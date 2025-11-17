package com.odcloud.application.port.out;

import com.odcloud.adapter.out.file.FileResponse;
import com.odcloud.domain.model.File;
import java.util.List;

public interface FilePort {

    void createFolder(String folderPath);

    void uploadFile(File file);

    void deleteFiles(List<String> filePaths);

    FileResponse readFile(File fileInfo);

    FileResponse readFiles(List<File> files);
}
