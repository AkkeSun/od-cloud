package com.odcloud.adapter.in.controller.group.join_group;

import com.odcloud.application.group.port.in.JoinGroupUseCase;
import com.odcloud.application.group.service.join_group.JoinGroupServiceResponse;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.resolver.LoginAccount;
import com.odcloud.infrastructure.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class JoinGroupController {

    private final JoinGroupUseCase useCase;

    @PostMapping("/groups/{groupId}/join")
    ApiResponse<JoinGroupResponse> join(
        @PathVariable Long groupId,
        @LoginAccount Account account
    ) {
        JoinGroupServiceResponse serviceResponse = useCase.join(groupId, account);
        return ApiResponse.ok(JoinGroupResponse.of(serviceResponse));
    }
}
