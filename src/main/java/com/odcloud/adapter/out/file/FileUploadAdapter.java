package com.odcloud.adapter.out.file;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_FILE_UPLOAD_ERROR;

import com.odcloud.application.port.out.FileUploadPort;
import com.odcloud.infrastructure.constant.ProfileConstant;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

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
            log.info("[createFolder] 폴더 생성 완료: {}", fullPath);
        } catch (IOException e) {
            log.error("[createFolder] 폴더 생성 실패: {}, error: {}", folderPath, e.getMessage());
            throw new CustomBusinessException(Business_FILE_UPLOAD_ERROR);
        }
    }

    @Override
    public void uploadFile(MultipartFile file, String fileLoc) {
        try {
            String basePath = profileConstant.fileUpload().basePath();
            Path fullPath = Paths.get(basePath, fileLoc);

            // 부모 디렉토리가 없으면 생성
            Path parentDir = fullPath.getParent();
            if (parentDir != null && !Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
            }

            // 파일 저장
            file.transferTo(fullPath.toFile());
            log.info("[uploadFile] 파일 업로드 완료: {}", fullPath);
        } catch (IOException e) {
            log.error("[uploadFile] 파일 업로드 실패: {}, error: {}", fileLoc, e.getMessage());
            throw new CustomBusinessException(Business_FILE_UPLOAD_ERROR);
        }
    }

    @Override
    public byte[] readFile(String fileLoc) {
        try {
            String basePath = profileConstant.fileUpload().basePath();
            Path fullPath = Paths.get(basePath, fileLoc);

            if (!Files.exists(fullPath)) {
                log.error("[readFile] 파일이 존재하지 않습니다: {}", fullPath);
                throw new CustomBusinessException(Business_FILE_UPLOAD_ERROR);
            }

            byte[] fileData = Files.readAllBytes(fullPath);
            log.info("[readFile] 파일 읽기 완료: {}", fullPath);
            return fileData;
        } catch (IOException e) {
            log.error("[readFile] 파일 읽기 실패: {}, error: {}", fileLoc, e.getMessage());
            throw new CustomBusinessException(Business_FILE_UPLOAD_ERROR);
        }
    }
}
