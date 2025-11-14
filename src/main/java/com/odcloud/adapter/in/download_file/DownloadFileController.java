package com.odcloud.adapter.in.download_file;

import com.odcloud.application.port.in.DownloadFileUseCase;
import com.odcloud.application.port.in.command.DownloadFileCommand;
import com.odcloud.application.port.in.command.DownloadFolderCommand;
import com.odcloud.application.service.download_file.DownloadFileServiceResponse;
import jakarta.validation.Valid;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
class DownloadFileController {

    private final DownloadFileUseCase useCase;

    /**
     * 단건 파일 다운로드
     */
    @GetMapping("/files/{fileId}/download")
    ResponseEntity<byte[]> downloadFile(@PathVariable Long fileId) {
        DownloadFileCommand command = DownloadFileCommand.builder()
            .fileId(fileId)
            .build();

        DownloadFileServiceResponse response = useCase.downloadFile(command);

        return createDownloadResponse(response);
    }

    /**
     * 복수 파일 다운로드 (압축)
     */
    @PostMapping("/files/download")
    ResponseEntity<byte[]> downloadFiles(@Valid @RequestBody DownloadFilesRequest request) {
        DownloadFileServiceResponse response = useCase.downloadFiles(request.toCommand());

        return createDownloadResponse(response);
    }

    /**
     * 폴더 다운로드 (압축)
     */
    @GetMapping("/folders/{folderId}/download")
    ResponseEntity<byte[]> downloadFolder(@PathVariable Long folderId) {
        DownloadFolderCommand command = DownloadFolderCommand.builder()
            .folderId(folderId)
            .build();

        DownloadFileServiceResponse response = useCase.downloadFolder(command);

        return createDownloadResponse(response);
    }

    private ResponseEntity<byte[]> createDownloadResponse(DownloadFileServiceResponse response) {
        ContentDisposition contentDisposition = ContentDisposition.attachment()
            .filename(response.fileName(), StandardCharsets.UTF_8)
            .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(contentDisposition);
        headers.setContentType(MediaType.parseMediaType(response.contentType()));
        headers.setContentLength(response.content().length);

        return ResponseEntity.ok()
            .headers(headers)
            .body(response.content());
    }
}
