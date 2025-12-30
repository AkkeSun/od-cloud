package com.odcloud.adapter.in.controller.file.register_folder;

import com.odcloud.application.port.in.RegisterFolderUseCase;
import com.odcloud.application.service.register_folder.RegisterFolderServiceResponse;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.response.ApiResponse;
import com.odcloud.infrastructure.validation.groups.ValidationSequence;
import com.odcloud.resolver.LoginAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class RegisterFolderController {

    private final RegisterFolderUseCase useCase;

    @PostMapping("/folders")
    ApiResponse<RegisterFolderResponse> createFolder(
        @RequestBody @Validated(ValidationSequence.class) RegisterFolderRequest request,
        @LoginAccount Account account
    ) {
        RegisterFolderServiceResponse response = useCase.createFolder(request.toCommand(account));
        return ApiResponse.ok(RegisterFolderResponse.of(response));
    }
}
