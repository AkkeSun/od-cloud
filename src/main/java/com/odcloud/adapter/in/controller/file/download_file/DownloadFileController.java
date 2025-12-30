package com.odcloud.adapter.in.controller.file.download_file;

import com.odcloud.application.port.in.DownloadFileUseCase;
import com.odcloud.application.service.download_file.DownloadFileServiceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class DownloadFileController {

    private final DownloadFileUseCase useCase;

    @GetMapping("/files/{fileId}/download")
    ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) {
        DownloadFileServiceResponse serviceResponse = useCase.downloadFile(fileId);
        return ResponseEntity.ok()
            .headers(serviceResponse.headers())
            .body(serviceResponse.resource());
    }
}
