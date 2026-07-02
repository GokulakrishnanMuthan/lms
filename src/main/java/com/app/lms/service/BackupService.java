package com.app.lms.service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Creates a timestamped database dump inside a caller-supplied folder.
 *
 * <p>The dump tool ({@code mysqldump} / {@code pg_dump}) is selected from the JDBC
 * driver in {@code spring.datasource.url} and invoked via {@link ProcessBuilder}
 * with an argument <em>list</em> (never a shell string). Credentials are read from
 * the existing {@code spring.datasource.*} config and are kept off the process
 * command line: for MySQL they go into a short-lived {@code --defaults-extra-file},
 * for PostgreSQL into the {@code PGPASSWORD} environment variable.
 */
@Service
public class BackupService {

	private static final Logger log = LoggerFactory.getLogger(BackupService.class);

	private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
	private static final Pattern JDBC_URL =
			Pattern.compile("^jdbc:(mysql|postgresql)://([^:/?]+)(?::(\\d+))?/([^?;]+).*$");
	private static final long DUMP_TIMEOUT_MINUTES = 10;

	@Value("${spring.datasource.url}")
	private String datasourceUrl;

	@Value("${spring.datasource.username}")
	private String datasourceUsername;

	@Value("${spring.datasource.password}")
	private String datasourcePassword;

	/** Base directory that every backup must be confined to (path-traversal guard). */
	@Value("${app.backup.root:}")
	private String backupRoot;

	@Value("${app.backup.mysqldump-path:mysqldump}")
	private String mysqldumpPath;

	@Value("${app.backup.pg-dump-path:pg_dump}")
	private String pgDumpPath;

	/**
	 * Validates the target folder, runs the dump, and returns the absolute path of
	 * the created {@code .sql} file.
	 *
	 * @throws BadRequestException if the folder is blank, outside the allowed root,
	 *         or resolves to a non-directory (maps to HTTP 400)
	 * @throws BackupException if the dump process fails (maps to HTTP 500)
	 */
	public String createBackup(String folderPath) {
		File dir = resolveAndValidateFolder(folderPath);

		String fileName = "lms-backup-" + LocalDateTime.now().format(STAMP) + ".sql";
		File dumpFile = new File(dir, fileName);

		DbInfo db = parseDatasourceUrl();
		log.info("Starting DB backup: db='{}' engine='{}' folder='{}' file='{}'",
				db.database, db.engine, dir.getAbsolutePath(), fileName);

		int exitCode = db.engine.equals("mysql")
				? runMysqlDump(db, dumpFile)
				: runPgDump(db, dumpFile);

		if (exitCode != 0) {
			// Don't leave a half-written / empty dump lying around.
			dumpFile.delete();
			log.error("Backup FAILED: db='{}' file='{}' exitCode={}", db.database, fileName, exitCode);
			throw new BackupException("Database dump failed (exit code " + exitCode
					+ "). Check that the dump tool is installed and the datasource credentials are correct.");
		}

		log.info("Backup SUCCESS: db='{}' file='{}' exitCode=0 path='{}'",
				db.database, fileName, dumpFile.getAbsolutePath());
		return dumpFile.getAbsolutePath();
	}

	// --- folder validation -------------------------------------------------

	private File resolveAndValidateFolder(String folderPath) {
		if (folderPath == null || folderPath.isBlank()) {
			throw new BadRequestException("folderPath must not be blank.");
		}

		Path resolved;
		try {
			resolved = Paths.get(folderPath.trim()).toAbsolutePath().normalize();
		} catch (Exception e) {
			throw new BadRequestException("folderPath is not a valid path: " + folderPath);
		}

		// Path-traversal / arbitrary-write guard: confine to app.backup.root when set.
		if (backupRoot != null && !backupRoot.isBlank()) {
			Path root = Paths.get(backupRoot.trim()).toAbsolutePath().normalize();
			if (!resolved.startsWith(root)) {
				throw new BadRequestException("folderPath must be inside the configured backup root ("
						+ root + ").");
			}
		}

		File dir = resolved.toFile();
		if (!dir.exists()) {
			if (!dir.mkdirs()) {
				throw new BackupException("Could not create backup folder: " + resolved);
			}
			log.info("Created backup folder: {}", resolved);
		} else if (!dir.isDirectory()) {
			throw new BadRequestException("folderPath exists but is not a directory: " + resolved);
		}
		return dir;
	}

	// --- datasource parsing ------------------------------------------------

	private DbInfo parseDatasourceUrl() {
		Matcher m = JDBC_URL.matcher(datasourceUrl == null ? "" : datasourceUrl.trim());
		if (!m.matches()) {
			throw new BackupException("Unsupported spring.datasource.url; expected a jdbc:mysql or "
					+ "jdbc:postgresql URL but was: " + datasourceUrl);
		}
		DbInfo db = new DbInfo();
		db.engine = m.group(1);
		db.host = m.group(2);
		db.port = m.group(3) != null ? m.group(3) : (db.engine.equals("mysql") ? "3306" : "5432");
		db.database = m.group(4);
		db.username = datasourceUsername;
		db.password = datasourcePassword;
		return db;
	}

	// --- process execution -------------------------------------------------

	private int runMysqlDump(DbInfo db, File dumpFile) {
		Path defaultsFile = null;
		try {
			// Keep the password off argv/env: mysqldump reads it from this options file.
			defaultsFile = writeMysqlDefaultsFile(db);

			List<String> command = new ArrayList<>();
			command.add(mysqldumpPath);
			command.add("--defaults-extra-file=" + defaultsFile.toString());
			command.add("--single-transaction"); // consistent snapshot for InnoDB, no table locks
			command.add(db.database);

			return runProcess(command, dumpFile, null);
		} catch (IOException e) {
			throw new BackupException("Could not prepare mysqldump: " + e.getMessage());
		} finally {
			if (defaultsFile != null) {
				try {
					Files.deleteIfExists(defaultsFile);
				} catch (IOException e) {
					log.warn("Could not delete temporary mysqldump defaults file {}", defaultsFile, e);
				}
			}
		}
	}

	private int runPgDump(DbInfo db, File dumpFile) {
		List<String> command = new ArrayList<>();
		command.add(pgDumpPath);
		command.add("-h");
		command.add(db.host);
		command.add("-p");
		command.add(db.port);
		command.add("-U");
		command.add(db.username);
		command.add("-w"); // never prompt for a password; read it from PGPASSWORD
		command.add(db.database);

		return runProcess(command, dumpFile, db.password);
	}

	/**
	 * Runs the given command, redirecting stdout to {@code dumpFile}. When
	 * {@code pgPassword} is non-null it is passed via the {@code PGPASSWORD} env var.
	 * Returns the process exit code.
	 */
	private int runProcess(List<String> command, File dumpFile, String pgPassword) {
		try {
			ProcessBuilder pb = new ProcessBuilder(command);
			pb.redirectOutput(ProcessBuilder.Redirect.to(dumpFile));
			pb.redirectErrorStream(false);
			if (pgPassword != null) {
				pb.environment().put("PGPASSWORD", pgPassword);
			}

			Process process = pb.start();
			// Drain stderr so the process can't block on a full pipe, and surface it on failure.
			byte[] errBytes = process.getErrorStream().readAllBytes();

			if (!process.waitFor(DUMP_TIMEOUT_MINUTES, TimeUnit.MINUTES)) {
				process.destroyForcibly();
				throw new BackupException("Database dump timed out after " + DUMP_TIMEOUT_MINUTES + " minutes.");
			}

			int exit = process.exitValue();
			if (exit != 0) {
				String err = new String(errBytes, StandardCharsets.UTF_8).trim();
				log.error("Dump tool stderr (exit {}): {}", exit, err);
			}
			return exit;
		} catch (IOException e) {
			// Most commonly: the dump binary is not on the server PATH.
			throw new BackupException("Could not start the dump tool '" + command.get(0)
					+ "'. Is it installed and on the server PATH? (" + e.getMessage() + ")");
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new BackupException("Backup was interrupted.");
		}
	}

	private Path writeMysqlDefaultsFile(DbInfo db) throws IOException {
		Path file = Files.createTempFile("lms-mysqldump-", ".cnf");
		// Best-effort: restrict to the owner before writing the credentials.
		file.toFile().setReadable(false, false);
		file.toFile().setReadable(true, true);
		file.toFile().setWritable(false, false);
		file.toFile().setWritable(true, true);

		String contents = "[client]\n"
				+ "host=" + db.host + "\n"
				+ "port=" + db.port + "\n"
				+ "user=" + db.username + "\n"
				+ "password=" + (db.password == null ? "" : db.password) + "\n";
		Files.write(file, contents.getBytes(StandardCharsets.UTF_8));
		return file;
	}

	// --- helper types ------------------------------------------------------

	private static class DbInfo {
		String engine;
		String host;
		String port;
		String database;
		String username;
		String password;
	}

	/** Maps to HTTP 400. */
	public static class BadRequestException extends RuntimeException {
		public BadRequestException(String message) {
			super(message);
		}
	}

	/** Maps to HTTP 500. */
	public static class BackupException extends RuntimeException {
		public BackupException(String message) {
			super(message);
		}
	}
}
