package com.odcloud.adapter.in.controller.file.update_file;

import com.odcloud.application.file.port.in.UpdateFileUseCase;
import com.odcloud.application.file.service.update_file.UpdateFileServiceResponse;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.resolver.LoginAccount;
import com.odcloud.infrastructure.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class UpdateFileController {

    private final UpdateFileUseCase useCase;

    @PatchMapping("/files/{fileId}")
    ApiResponse<UpdateFileResponse> update(
        @PathVariable Long fileId,
        @RequestBody UpdateFileRequest request,
        @LoginAccount Account account
    ) {
        UpdateFileServiceResponse response = useCase.update(request.toCommand(fileId, account));
        return ApiResponse.ok(UpdateFileResponse.of(response));
    }
}
