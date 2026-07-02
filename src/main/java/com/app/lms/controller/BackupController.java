package com.app.lms.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.app.lms.entity.User;
import com.app.lms.model.BackupRequest;
import com.app.lms.model.BackupResponse;
import com.app.lms.service.BackupService;
import com.app.lms.service.BackupService.BadRequestException;
import com.app.lms.service.BackupService.BackupException;
import com.app.lms.service.UserService;

/**
 * {@code POST /lms/backup} — creates a timestamped database dump on the server.
 *
 * <p>Admin-only. Because this application has no session/token security layer, the
 * caller is identified by the {@value #ADMIN_HEADER} header (the logged-in
 * username). That username is verified against the {@code users} table and the
 * request is rejected unless the user's {@code role} is {@code admin}. This is the
 * strongest check available without introducing Spring Security; see BACKEND_CHANGES.
 */
@RestController
public class BackupController {

	private static final Logger log = LoggerFactory.getLogger(BackupController.class);

	/** Header carrying the logged-in username; must also be allowed by the CORS filter. */
	public static final String ADMIN_HEADER = "X-User-Name";

	@Autowired
	private BackupService backupService;

	@Autowired
	private UserService userService;

	@PostMapping("/backup")
	public ResponseEntity<BackupResponse> backup(
			@RequestHeader(value = ADMIN_HEADER, required = false) String userName,
			@RequestBody BackupRequest request) {

		if (!isAdmin(userName)) {
			log.warn("Rejected /backup: user '{}' is not an authorised admin", userName);
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(BackupResponse.failure("Not authorised. This action requires an admin account."));
		}

		try {
			String filePath = backupService.createBackup(request == null ? null : request.getFolderPath());
			return ResponseEntity.ok(BackupResponse.success("Backup created", filePath));
		} catch (BadRequestException e) {
			log.warn("Backup bad request: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(BackupResponse.failure(e.getMessage()));
		} catch (BackupException e) {
			log.error("Backup failed: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(BackupResponse.failure(e.getMessage()));
		} catch (Exception e) {
			log.error("Unexpected error creating backup", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(BackupResponse.failure("An unexpected error occurred while creating the backup."));
		}
	}

	private boolean isAdmin(String userName) {
		if (userName == null || userName.isBlank()) {
			return false;
		}
		Optional<User> user = userService.getUserByName(userName.trim());
		return user.isPresent()
				&& "admin".equalsIgnoreCase(user.get().getRole())
				&& !"true".equalsIgnoreCase(String.valueOf(user.get().getIsDeleted()));
	}
}
