package com.odcloud.adapter.in.controller.file.delete_file;

import com.odcloud.application.file.port.in.DeleteFileUseCase;
import com.odcloud.application.file.service.delete_file.DeleteFileServiceResponse;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.resolver.LoginAccount;
import com.odcloud.infrastructure.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
class DeleteFileController {

    private final DeleteFileUseCase useCase;

    @DeleteMapping("/files")
    ApiResponse<DeleteFileResponse> delete(
        @LoginAccount Account account,
        @Valid @RequestBody DeleteFileRequest request
    ) {
        DeleteFileServiceResponse serviceResponse = useCase.deleteFile(request.toCommand(account));
        return ApiResponse.ok(DeleteFileResponse.of(serviceResponse));
    }
}
