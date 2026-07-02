package com.app.lms.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

/**
 * Response body for {@code POST /lms/backup}.
 *
 * <p>Success: {@code { "success": true, "message": "Backup created", "filePath": "..." }}<br>
 * Failure: {@code { "success": false, "message": "<reason>" }} ({@code filePath} omitted).
 *
 * <p>{@code filePath} is only serialised when non-null, so failure responses stay
 * to the {@code success}/{@code message} shape the frontend toast expects.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BackupResponse {

	private final boolean success;
	private final String message;
	private final String filePath;

	public static BackupResponse success(String message, String filePath) {
		return new BackupResponse(true, message, filePath);
	}

	public static BackupResponse failure(String message) {
		return new BackupResponse(false, message, null);
	}
}
