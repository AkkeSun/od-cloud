package com.odcloud.adapter.in.controller.file.update_folder;

import com.odcloud.application.file.port.in.UpdateFolderUseCase;
import com.odcloud.application.file.service.update_folder.UpdateFolderServiceResponse;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.resolver.LoginAccount;
import com.odcloud.infrastructure.response.ApiResponse;
import com.odcloud.infrastructure.validation.groups.ValidationSequence;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class UpdateFolderController {

    private final UpdateFolderUseCase useCase;

    @PatchMapping("/folders/{folderId}")
    ApiResponse<UpdateFolderResponse> updateFolder(
        @PathVariable Long folderId,
        @Validated(ValidationSequence.class) @RequestBody UpdateFolderRequest request,
        @LoginAccount Account account
    ) {
        UpdateFolderServiceResponse response = useCase.updateFolder(
            request.toCommand(folderId, account));
        return ApiResponse.ok(UpdateFolderResponse.of(response));
    }
}
