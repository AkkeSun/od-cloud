package com.odcloud.adapter.out.file;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_FILE_DOWNLOAD_ERROR;
import static com.odcloud.infrastructure.exception.ErrorCode.Business_FILE_UPLOAD_ERROR;

import com.odcloud.application.port.out.FilePort;
import com.odcloud.domain.model.FileInfo;
import com.odcloud.infrastructure.constant.ProfileConstant;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Slf4j
@Component
class FileAdapter implements FilePort {

    private final Tika tika;
    private String basePath;

    FileAdapter(ProfileConstant profileConstant) {
        this.tika = new Tika();
        this.basePath = profileConstant.fileUpload().basePath();
    }

    @Override
    public void createFolder(String folderPath) {
        try {
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
    public void uploadFile(FileInfo file) {
        try {
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

    @Override
    public void deleteFiles(List<String> filePaths) {
        for (String filePath : filePaths) {
            deleteFile(filePath);
        }
    }

    @Override
    public void deleteFile(String filePath) {
        java.io.File file = new java.io.File(basePath + filePath);
        if (file.exists()) {
            boolean deleted = file.delete();
            if (!deleted) {
                log.warn("[deleteFile] 파일 삭제 실패: {}", filePath);
            }
        }
    }

    @Override
    public FileResponse readFile(FileInfo fileInfo) {
        try {
            java.io.File file = new java.io.File(basePath + fileInfo.getFileLoc());
            FileSystemResource resource = new FileSystemResource(file);
            if (!resource.exists()) {
                log.error("[readFile] 서버 내 존재하는 파일이 없음");
                throw new RuntimeException();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(ContentDisposition.attachment()
                .filename(fileInfo.getFileName(), StandardCharsets.UTF_8)
                .build());
            headers.setContentType(MediaType.parseMediaType(tika.detect(file)));
            headers.setContentLength(resource.contentLength());

            return FileResponse.builder()
                .resource(resource)
                .headers(headers)
                .build();

        } catch (IOException e) {
            throw new CustomBusinessException(Business_FILE_DOWNLOAD_ERROR);
        }
    }

    @Override
    public FileResponse readFiles(List<FileInfo> files) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(baos);
            for (FileInfo file : files) {
                FileResponse fileResponse = readFile(file);
                addFileToZip(zos, fileResponse, file.getFileName());
            }

            zos.close();

            Resource resource = new ByteArrayResource(baos.toByteArray());
            HttpHeaders headers = createZipHeaders();

            return FileResponse.builder()
                .resource(resource)
                .headers(headers)
                .build();
        } catch (IOException e) {
            log.error("[readFiles] 파일 압축 중 오류 발생", e);
            throw new CustomBusinessException(Business_FILE_DOWNLOAD_ERROR);
        }
    }

    private void addFileToZip(ZipOutputStream zos, FileResponse fileResponse, String fileName)
        throws IOException {
        ZipEntry zipEntry = new ZipEntry(fileName);
        zos.putNextEntry(zipEntry);

        try (InputStream inputStream = fileResponse.resource().getInputStream()) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                zos.write(buffer, 0, length);
            }
        }

        zos.closeEntry();
    }

    private HttpHeaders createZipHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String timestamp = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = "files_" + timestamp + ".zip";

        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.attachment()
            .filename(filename, StandardCharsets.UTF_8)
            .build());

        return headers;
    }
}
