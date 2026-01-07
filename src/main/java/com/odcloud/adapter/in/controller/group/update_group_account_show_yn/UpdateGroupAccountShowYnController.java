package com.odcloud.adapter.in.controller.group.update_group_account_show_yn;

import com.odcloud.application.group.port.in.UpdateGroupAccountUseYnUseCase;
import com.odcloud.application.group.service.update_group_account_use_yn.UpdateGroupAccountUseYnServiceResponse;
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
class UpdateGroupAccountShowYnController {

    private final UpdateGroupAccountUseYnUseCase useCase;

    @PatchMapping("/groups/{groupId}/useYn")
    ApiResponse<UpdateGroupAccountUseYnResponse> update(
        @PathVariable Long groupId,
        @LoginAccount Account account,
        @RequestBody @Validated(ValidationSequence.class) UpdateGroupAccountShowYnRequest request) {

        UpdateGroupAccountUseYnServiceResponse serviceResponse = useCase.updateShowYn(
            request.toCommand(groupId, account));
        return ApiResponse.ok(UpdateGroupAccountUseYnResponse.of(serviceResponse));
    }
}
