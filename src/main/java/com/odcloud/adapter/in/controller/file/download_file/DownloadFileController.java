package com.odcloud.adapter.in.controller.file.download_file;

import com.odcloud.application.file.port.in.DownloadFileUseCase;
import com.odcloud.application.file.service.download_file.DownloadFileCommand;
import com.odcloud.application.file.service.download_file.DownloadFileResponse;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.resolver.LoginAccount;
import com.odcloud.infrastructure.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class DownloadFileController {

    private final DownloadFileUseCase useCase;

    @GetMapping("/files/{fileId}/download")
    ApiResponse<DownloadFileResponse> downloadFile(
        @PathVariable Long fileId,
        @LoginAccount Account account
    ) {
        DownloadFileCommand command = DownloadFileCommand.builder()
            .account(account)
            .fileId(fileId)
            .build();
        return ApiResponse.ok(useCase.downloadFile(command));
    }
}
