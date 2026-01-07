package com.odcloud.adapter.in.controller.group.update_group_account_status;

import com.odcloud.application.group.port.in.UpdateGroupAccountStatusUseCase;
import com.odcloud.application.group.service.update_group_account_status.UpdateGroupAccountStatusServiceResponse;
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
class UpdateGroupAccountStatusController {

    private final UpdateGroupAccountStatusUseCase useCase;

    @PatchMapping("/groups/{groupId}/accounts/{accountId}/status")
    ApiResponse<UpdateGroupAccountStatusResponse> updateStatus(
        @PathVariable Long groupId,
        @PathVariable Long accountId,
        @LoginAccount Account account,
        @RequestBody @Validated(ValidationSequence.class) UpdateGroupAccountStatusRequest request
    ) {
        UpdateGroupAccountStatusServiceResponse serviceResponse = useCase.updateStatus(
            request.toCommand(groupId, accountId, account));
        return ApiResponse.ok(UpdateGroupAccountStatusResponse.of(serviceResponse));
    }
}
