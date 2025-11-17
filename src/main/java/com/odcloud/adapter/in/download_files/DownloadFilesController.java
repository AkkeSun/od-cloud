package com.odcloud.adapter.in.download_files;

import com.odcloud.application.port.in.DownloadFilesUseCase;
import com.odcloud.application.service.download_files.DownloadFilesServiceResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
class DownloadFilesController {

    private final DownloadFilesUseCase useCase;

    @GetMapping("/files/download")
    ResponseEntity<Resource> downloadFiles(@Valid DownloadFilesRequest request) {
        DownloadFilesServiceResponse serviceResponse = useCase.download(request.getFileIds());
        return ResponseEntity.ok()
            .headers(serviceResponse.headers())
            .body(serviceResponse.resource());
    }
}
