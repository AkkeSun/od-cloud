package com.odcloud.application.service.create_file;

import com.odcloud.application.port.in.CreateFileUseCase;
import com.odcloud.application.port.in.command.CreateFileCommand;
import com.odcloud.application.port.out.FileStoragePort;
import com.odcloud.application.port.out.FileUploadPort;
import com.odcloud.application.port.out.FolderStoragePort;
import com.odcloud.domain.model.File;
import com.odcloud.domain.model.Folder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
class CreateFileService implements CreateFileUseCase {

    private final FolderStoragePort folderStoragePort;
    private final FileStoragePort fileStoragePort;
    private final FileUploadPort fileUploadPort;

    @Override
    @Transactional
    public CreateFileServiceResponse createFile(CreateFileCommand command) {
        // 폴더 조회
        Folder folder = folderStoragePort.findById(command.folderId());

        // 여러 파일 처리
        for (MultipartFile multipartFile : command.files()) {
            // 원본 파일명 및 확장자 추출
            String originalFileName = multipartFile.getOriginalFilename();
            String extension = getFileExtension(originalFileName);

            // 서버 파일명 생성 (UUID + 날짜 + 확장자)
            String uuid = UUID.randomUUID().toString().substring(0, 8);
            String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String serverFileName = uuid + "_" + date + extension;

            // 파일 경로 생성
            String fileLoc = folder.getPath() + "/" + serverFileName;

            // File 도메인 모델 생성
            File file = File.create(command.folderId(), originalFileName, fileLoc);

            // DB 저장
            fileStoragePort.save(file);

            // 파일 업로드
            fileUploadPort.uploadFile(multipartFile, fileLoc);

            log.info("[createFile] 파일 생성 완료 - 원본: {}, 저장경로: {}", originalFileName, fileLoc);
        }

        return CreateFileServiceResponse.ofSuccess();
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
}
