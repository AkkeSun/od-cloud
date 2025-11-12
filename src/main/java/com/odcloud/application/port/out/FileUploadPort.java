package com.odcloud.application.port.out;

import com.odcloud.domain.model.File;

public interface FileUploadPort {

    void createFolder(String folderPath);

    void uploadFile(File file);
}
