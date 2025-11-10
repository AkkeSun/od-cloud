package com.odcloud.application.port.out;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadPort {

    void createFolder(String folderPath);

    void uploadFile(MultipartFile file, String fileLoc);

    byte[] readFile(String fileLoc);
}
