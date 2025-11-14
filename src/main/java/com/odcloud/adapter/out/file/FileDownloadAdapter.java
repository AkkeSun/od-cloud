package com.odcloud.adapter.out.file;

import com.odcloud.application.port.out.FileDownloadPort;
import com.odcloud.infrastructure.constant.ProfileConstant;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class FileDownloadAdapter implements FileDownloadPort {

    private final ProfileConstant profileConstant;

    @Override
    public InputStream readFile(String fileLoc) {
        try {
            String basePath = profileConstant.fileUpload().basePath();
            Path fullPath = Paths.get(basePath, fileLoc);

            if (!Files.exists(fullPath)) {
                log.error("[readFile] 파일이 존재하지 않습니다: {}", fullPath);
                throw new IllegalArgumentException("파일을 찾을 수 없습니다: " + fileLoc);
            }

            return new FileInputStream(fullPath.toFile());
        } catch (IOException e) {
            log.error("[readFile] 파일 읽기 실패: {}, error: {}", fileLoc, e.getMessage());
            throw new RuntimeException("파일 읽기 실패: " + fileLoc, e);
        }
    }

    @Override
    public boolean fileExists(String fileLoc) {
        String basePath = profileConstant.fileUpload().basePath();
        Path fullPath = Paths.get(basePath, fileLoc);
        return Files.exists(fullPath);
    }
}
