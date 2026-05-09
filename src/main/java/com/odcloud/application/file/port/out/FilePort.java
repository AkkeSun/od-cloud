package com.odcloud.application.file.port.out;

import com.odcloud.application.file.port.out.dto.FileResponse;
import com.odcloud.domain.model.FileInfo;
import java.util.List;

public interface FilePort {

    void uploadFile(FileInfo file);

    void deleteFile(String filePath);

    FileResponse readFile(FileInfo fileInfo);

    FileResponse readFiles(List<FileInfo> files);
}
