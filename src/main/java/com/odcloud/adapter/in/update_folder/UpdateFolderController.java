package com.odcloud.adapter.in.update_folder;

import com.odcloud.application.port.in.UpdateFolderUseCase;
import com.odcloud.application.service.update_folder.UpdateFolderServiceResponse;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.response.ApiResponse;
import com.odcloud.infrastructure.validation.groups.ValidationSequence;
import com.odcloud.resolver.LoginAccount;
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

    @PatchMapping("/folders/{fileId}")
    ApiResponse<UpdateFolderResponse> updateFolder(
        @PathVariable Long fileId,
        @Validated(ValidationSequence.class) @RequestBody UpdateFolderRequest request,
        @LoginAccount Account account
    ) {
        UpdateFolderServiceResponse response = useCase.updateFolder(
            request.toCommand(fileId, account));
        return ApiResponse.ok(UpdateFolderResponse.of(response));
    }
}
