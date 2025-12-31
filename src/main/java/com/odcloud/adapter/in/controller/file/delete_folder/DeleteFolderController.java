package com.odcloud.adapter.in.controller.file.delete_folder;

import com.odcloud.application.file.port.in.DeleteFolderUseCase;
import com.odcloud.application.file.service.delete_folder.DeleteFolderServiceResponse;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.resolver.LoginAccount;
import com.odcloud.infrastructure.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
class DeleteFolderController {

    private final DeleteFolderUseCase useCase;

    @DeleteMapping("/folders/{folderId}")
    ApiResponse<DeleteFolderResponse> delete(
        @LoginAccount Account account,
        @PathVariable Long folderId
    ) {
        DeleteFolderServiceResponse serviceResponse = useCase.deleteFolder(account, folderId);
        return ApiResponse.ok(DeleteFolderResponse.of(serviceResponse));
    }
}
