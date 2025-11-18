package com.odcloud.application.port.out;

import com.odcloud.adapter.out.file.FileResponse;
import com.odcloud.domain.model.File;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface FilePort {

    void createFolder(String folderPath);

    void uploadFile(File file);

    void deleteFiles(List<String> filePaths);

    void deleteFile(String filePath);

    String uploadProfilePicture(MultipartFile file);

    FileResponse readFile(File fileInfo);

    FileResponse readFiles(List<File> files);
}
