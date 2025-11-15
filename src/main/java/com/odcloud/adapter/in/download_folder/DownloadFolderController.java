package com.odcloud.adapter.in.download_folder;

import com.odcloud.application.port.in.DownloadFileUseCase;
import com.odcloud.application.port.in.command.DownloadFolderCommand;
import com.odcloud.application.service.download_file.DownloadFileServiceResponse;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class DownloadFolderController {

    private final DownloadFileUseCase useCase;

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
