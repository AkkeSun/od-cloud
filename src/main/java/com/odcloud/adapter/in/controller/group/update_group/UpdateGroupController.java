package com.odcloud.adapter.in.controller.group.update_group;

import com.odcloud.application.group.port.in.UpdateGroupUseCase;
import com.odcloud.application.group.service.update_group.UpdateGroupServiceResponse;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.resolver.LoginAccount;
import com.odcloud.infrastructure.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class UpdateGroupController {

    private final UpdateGroupUseCase useCase;

    @PatchMapping("/groups/{groupId}")
    ApiResponse<UpdateGroupResponse> update(
        @PathVariable Long groupId,
        @RequestBody @Valid UpdateGroupRequest request,
        @LoginAccount Account account
    ) {
        UpdateGroupServiceResponse serviceResponse = useCase.update(
            request.toCommand(groupId, account));
        return ApiResponse.ok(UpdateGroupResponse.of(serviceResponse));
    }
}
