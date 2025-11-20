package com.odcloud.adapter.in.delete_folder;

import com.odcloud.application.port.in.DeleteFolderUseCase;
import com.odcloud.application.service.delete_folder.DeleteFolderServiceResponse;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.response.ApiResponse;
import com.odcloud.resolver.LoginAccount;
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
