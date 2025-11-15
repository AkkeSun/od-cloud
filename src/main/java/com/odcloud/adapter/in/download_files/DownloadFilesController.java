package com.odcloud.adapter.in.download_files;

import com.odcloud.application.port.in.DownloadFileUseCase;
import com.odcloud.application.port.in.command.DownloadFilesCommand;
import com.odcloud.application.service.download_file.DownloadFileServiceResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class DownloadFilesController {

    private final DownloadFileUseCase useCase;

    /**
     * 복수 파일 다운로드 (압축)
     */
    @GetMapping("/files/download")
    ResponseEntity<byte[]> downloadFiles(@RequestParam List<Long> fileIds) {
        DownloadFilesCommand command = DownloadFilesCommand.builder()
            .fileIds(fileIds)
            .build();

        DownloadFileServiceResponse response = useCase.downloadFiles(command);

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
