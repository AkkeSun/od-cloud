package com.odcloud.application.service.download_file;

import com.odcloud.application.port.in.DownloadFileUseCase;
import com.odcloud.application.port.in.command.DownloadFileCommand;
import com.odcloud.application.port.in.command.DownloadFilesCommand;
import com.odcloud.application.port.in.command.DownloadFolderCommand;
import com.odcloud.application.port.out.FileDownloadPort;
import com.odcloud.application.port.out.FileStoragePort;
import com.odcloud.application.port.out.FolderStoragePort;
import com.odcloud.domain.model.File;
import com.odcloud.domain.model.Folder;
import com.odcloud.infrastructure.util.ZipUtil;
import com.odcloud.infrastructure.util.ZipUtil.FileInfo;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
class DownloadFileService implements DownloadFileUseCase {

    private final FileStoragePort fileStoragePort;
    private final FolderStoragePort folderStoragePort;
    private final FileDownloadPort fileDownloadPort;

    @Override
    @Transactional(readOnly = true)
    public DownloadFileServiceResponse downloadFile(DownloadFileCommand command) {
        File file = fileStoragePort.findById(command.fileId());

        try (InputStream inputStream = fileDownloadPort.readFile(file.getFileLoc())) {
            byte[] content = readAllBytes(inputStream);
            String contentType = determineContentType(file.getFileName());

            return DownloadFileServiceResponse.of(file.getFileName(), content, contentType);
        } catch (Exception e) {
            log.error("[downloadFile] 파일 다운로드 실패: fileId={}, error={}", command.fileId(),
                e.getMessage());
            throw new RuntimeException("파일 다운로드 실패", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public DownloadFileServiceResponse downloadFiles(DownloadFilesCommand command) {
        List<File> files = fileStoragePort.findByIds(command.fileIds());

        if (files.isEmpty()) {
            throw new IllegalArgumentException("다운로드할 파일이 없습니다.");
        }

        List<FileInfo> fileInfos = new ArrayList<>();
        for (File file : files) {
            InputStream inputStream = fileDownloadPort.readFile(file.getFileLoc());
            fileInfos.add(new FileInfo(file.getFileName(), inputStream));
        }

        byte[] zipContent = ZipUtil.createZip(fileInfos);
        String zipFileName = "files.zip";

        return DownloadFileServiceResponse.of(zipFileName, zipContent, "application/zip");
    }

    @Override
    @Transactional(readOnly = true)
    public DownloadFileServiceResponse downloadFolder(DownloadFolderCommand command) {
        Folder folder = folderStoragePort.findById(command.folderId());

        // 폴더 내 모든 파일 수집 (서브폴더 포함)
        List<FileInfo> fileInfos = collectAllFilesInFolder(folder);

        if (fileInfos.isEmpty()) {
            throw new IllegalArgumentException("폴더에 다운로드할 파일이 없습니다.");
        }

        byte[] zipContent = ZipUtil.createZip(fileInfos);
        String zipFileName = folder.getName() + ".zip";

        return DownloadFileServiceResponse.of(zipFileName, zipContent, "application/zip");
    }

    private List<FileInfo> collectAllFilesInFolder(Folder folder) {
        List<FileInfo> fileInfos = new ArrayList<>();

        // 현재 폴더의 파일들
        List<File> files = fileStoragePort.findByFolderId(folder.getId());
        for (File file : files) {
            InputStream inputStream = fileDownloadPort.readFile(file.getFileLoc());
            String relativePath = folder.getName() + "/" + file.getFileName();
            fileInfos.add(new FileInfo(relativePath, inputStream));
        }

        // 하위 폴더들의 파일들
        List<Folder> subFolders = folderStoragePort.findAllSubFolders(folder.getId());
        for (Folder subFolder : subFolders) {
            List<File> subFiles = fileStoragePort.findByFolderId(subFolder.getId());
            for (File file : subFiles) {
                InputStream inputStream = fileDownloadPort.readFile(file.getFileLoc());
                // 상대 경로 계산 (폴더 구조 유지)
                String relativePath = buildRelativePath(folder, subFolder) + "/" + file.getFileName();
                fileInfos.add(new FileInfo(relativePath, inputStream));
            }
        }

        return fileInfos;
    }

    private String buildRelativePath(Folder rootFolder, Folder subFolder) {
        // 간단한 구현: 서브폴더 이름 사용
        // 실제로는 폴더 계층 구조를 따라 전체 경로를 구성해야 할 수 있음
        return rootFolder.getName() + "/" + subFolder.getName();
    }

    private byte[] readAllBytes(InputStream inputStream) throws Exception {
        try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            byte[] data = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(data)) != -1) {
                buffer.write(data, 0, bytesRead);
            }
            return buffer.toByteArray();
        }
    }

    private String determineContentType(String fileName) {
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
        } else if (lowerFileName.endsWith(".json")) {
            return "application/json";
        } else if (lowerFileName.endsWith(".xml")) {
            return "application/xml";
        } else if (lowerFileName.endsWith(".zip")) {
            return "application/zip";
        } else {
            return "application/octet-stream";
        }
    }
}
