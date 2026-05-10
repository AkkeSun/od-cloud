package com.odcloud.application.file.port.out;

import java.io.InputStream;

public interface GoogleDrivePort {

    String ensureFolder(String folderName);

    String ensureSubFolder(String parentFolderId, String folderName);

    void uploadFile(String folderId, String driveFileName, InputStream content, long fileSize);

    void deleteFile(String folderId, String fileName);

    boolean fileExists(String folderId, String fileName);
}
