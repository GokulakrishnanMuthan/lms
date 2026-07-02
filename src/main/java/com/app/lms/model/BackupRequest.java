package com.app.lms.model;

import lombok.Data;

/**
 * Request body for {@code POST /lms/backup}.
 *
 * <p>Matches the frontend contract exactly:
 * <pre>{ "folderPath": "D:\\lms-backups" }</pre>
 */
@Data
public class BackupRequest {

	/** Directory on the server where the dump file must be written. */
	private String folderPath;
}
