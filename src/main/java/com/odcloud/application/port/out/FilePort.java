package com.odcloud.application.port.out;

import com.odcloud.adapter.out.file.FileResponse;
import com.odcloud.domain.model.FileInfo;
import java.util.List;

public interface FilePort {

    void createFolder(String folderPath);

    void uploadFile(FileInfo file);

    void deleteFiles(List<String> filePaths);

    void deleteFile(String filePath);

    FileResponse readFile(FileInfo fileInfo);

    FileResponse readFiles(List<FileInfo> files);

    void moveFolder(String oldPath, String newPath);

    void deleteFolder(String path);
}
