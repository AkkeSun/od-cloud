package com.odcloud.adapter.in.create_folder;

import com.odcloud.application.port.in.CreateFolderUseCase;
import com.odcloud.application.service.create_folder.CreateFolderServiceResponse;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.response.ApiResponse;
import com.odcloud.resolver.LoginAccount;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class CreateFolderController {

    private final CreateFolderUseCase useCase;

    @PostMapping("/folders")
    ApiResponse<CreateFolderResponse> createFolder(
        @RequestBody @Valid CreateFolderRequest request,
        @LoginAccount Account account
    ) {
        CreateFolderServiceResponse serviceResponse = useCase.createFolder(
            request.toCommand(account));
        return ApiResponse.ok(CreateFolderResponse.of(serviceResponse));
    }
}
