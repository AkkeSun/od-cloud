package com.odcloud.adapter.in.controller.group.register_group;

import com.odcloud.application.group.port.in.RegisterGroupUseCase;
import com.odcloud.application.group.service.register_group.RegisterGroupResponse;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.resolver.LoginAccount;
import com.odcloud.infrastructure.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class RegisterGroupController {

    private final RegisterGroupUseCase useCase;

    @PostMapping("/groups")
    ApiResponse<RegisterGroupResponse> register(
        @RequestBody @Valid RegisterGroupRequest request,
        @LoginAccount Account account
    ) {
        return ApiResponse.ok(useCase.register(request.toCommand(account)));
    }
}
