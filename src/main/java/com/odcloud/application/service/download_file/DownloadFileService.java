package com.odcloud.application.service.download_file;

import com.odcloud.application.port.in.DownloadFileUseCase;
import com.odcloud.application.port.in.command.DownloadFileCommand;
import com.odcloud.application.port.out.FileStoragePort;
import com.odcloud.application.port.out.FileUploadPort;
import com.odcloud.domain.model.File;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
class DownloadFileService implements DownloadFileUseCase {

    private final FileStoragePort fileStoragePort;
    private final FileUploadPort fileUploadPort;

    @Override
    @Transactional(readOnly = true)
    public DownloadFileServiceResponse downloadFile(DownloadFileCommand command) {
        List<File> files = fileStoragePort.findByIds(command.fileIds());

        if (files.isEmpty()) {
            throw new IllegalArgumentException("다운로드할 파일이 없습니다");
        }

        // 단일 파일인 경우
        if (files.size() == 1) {
            File file = files.get(0);
            byte[] fileData = fileUploadPort.readFile(file.getFileLoc());
            String contentType = getContentType(file.getFileName());

            log.info("[downloadFile] 단일 파일 다운로드: {}", file.getFileName());
            return DownloadFileServiceResponse.builder()
                .fileData(fileData)
                .fileName(file.getFileName())
                .contentType(contentType)
                .build();
        }

        // 복수 파일인 경우 - ZIP으로 압축
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(baos);

            for (File file : files) {
                byte[] fileData = fileUploadPort.readFile(file.getFileLoc());
                ZipEntry zipEntry = new ZipEntry(file.getFileName());
                zos.putNextEntry(zipEntry);
                zos.write(fileData);
                zos.closeEntry();
            }

            zos.close();
            byte[] zipData = baos.toByteArray();

            log.info("[downloadFile] 복수 파일 ZIP 다운로드: {} 개 파일", files.size());
            return DownloadFileServiceResponse.builder()
                .fileData(zipData)
                .fileName("files.zip")
                .contentType("application/zip")
                .build();
        } catch (IOException e) {
            log.error("[downloadFile] ZIP 생성 실패: {}", e.getMessage());
            throw new RuntimeException("ZIP 파일 생성에 실패했습니다", e);
        }
    }

    private String getContentType(String fileName) {
        if (fileName == null) {
            return "application/octet-stream";
        }
        String lowerFileName = fileName.toLowerCase();
        if (lowerFileName.endsWith(".pdf")) {
            return "application/pdf";
        } else if (lowerFileName.endsWith(".jpg") || lowerFileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (lowerFileName.endsWith(".png")) {
            return "image/png";
        } else if (lowerFileName.endsWith(".txt")) {
            return "text/plain";
        } else if (lowerFileName.endsWith(".zip")) {
            return "application/zip";
        }
        return "application/octet-stream";
    }
}
