package com.odcloud.adapter.in.controller.update_file;

import com.odcloud.application.port.in.UpdateFileUseCase;
import com.odcloud.application.service.update_file.UpdateFileServiceResponse;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.response.ApiResponse;
import com.odcloud.resolver.LoginAccount;
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
