package com.odcloud.adapter.in.controller.file.download_files;

import com.odcloud.application.file.port.in.DownloadFilesUseCase;
import com.odcloud.application.file.service.download_files.DownloadFilesResponse;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.resolver.LoginAccount;
import com.odcloud.infrastructure.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
class DownloadFilesController {

    private final DownloadFilesUseCase useCase;

    @GetMapping("/files/download")
    ApiResponse<DownloadFilesResponse> downloadFiles(
        @Valid DownloadFilesRequest request,
        @LoginAccount Account account
    ) {
        return ApiResponse.ok(useCase.download(request.toCommand(account)));
    }
}
