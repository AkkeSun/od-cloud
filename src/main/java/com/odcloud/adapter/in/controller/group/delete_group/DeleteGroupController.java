package com.odcloud.adapter.in.controller.group.delete_group;

import com.odcloud.application.group.port.in.DeleteGroupUseCase;
import com.odcloud.application.group.service.delete_group.DeleteGroupResponse;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.resolver.LoginAccount;
import com.odcloud.infrastructure.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class DeleteGroupController {

    private final DeleteGroupUseCase deleteGroupUseCase;

    @DeleteMapping("/groups/{groupId}")
    ApiResponse<DeleteGroupResponse> delete(
        @PathVariable Long groupId,
        @LoginAccount Account account
    ) {
        return ApiResponse.ok(
            deleteGroupUseCase.delete(groupId, account));
    }
}
