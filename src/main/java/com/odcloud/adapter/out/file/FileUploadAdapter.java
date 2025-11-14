package com.odcloud.adapter.out.file;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_FILE_UPLOAD_ERROR;

import com.odcloud.application.port.out.FileUploadPort;
import com.odcloud.domain.model.File;
import com.odcloud.infrastructure.constant.ProfileConstant;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class FileUploadAdapter implements FileUploadPort {

    private final ProfileConstant profileConstant;

    @Override
    public void createFolder(String folderPath) {
        try {
            String basePath = profileConstant.fileUpload().basePath();
            Path fullPath = Paths.get(basePath, folderPath);

            if (Files.exists(fullPath)) {
                log.info("[createFolder] 폴더가 이미 존재합니다: {}", fullPath);
                return;
            }

            Files.createDirectories(fullPath);
        } catch (IOException e) {
            log.error("[createFolder] 폴더 생성 실패: {}, error: {}", folderPath, e.getMessage());
            throw new CustomBusinessException(Business_FILE_UPLOAD_ERROR);
        }
    }

    @Override
    public void uploadFile(File file) {
        try {
            String basePath = profileConstant.fileUpload().basePath();
            Path fullPath = Paths.get(basePath, file.getFileLoc());
            Path parentDir = fullPath.getParent();
            if (parentDir != null && !Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
            }

            file.getMultipartFile().transferTo(fullPath.toFile());
        } catch (IOException e) {
            log.error("[uploadFile] 파일 업로드 실패: {}, error: {}", file.getFileLoc(), e.getMessage());
            throw new CustomBusinessException(Business_FILE_UPLOAD_ERROR);
        }
    }
}
