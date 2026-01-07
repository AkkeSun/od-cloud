package com.odcloud.adapter.in.controller.group.register_notice;

import com.odcloud.application.group.port.in.RegisterNoticeUseCase;
import com.odcloud.application.group.service.register_notice.RegisterNoticeServiceResponse;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.resolver.LoginAccount;
import com.odcloud.infrastructure.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class RegisterNoticeController {

    private final RegisterNoticeUseCase useCase;

    @PostMapping("/groups/{groupId}/notices")
    ApiResponse<RegisterNoticeResponse> register(
        @PathVariable Long groupId,
        @LoginAccount Account account,
        @RequestBody @Valid RegisterNoticeRequest request
    ) {
        RegisterNoticeServiceResponse serviceResponse = useCase.register(
            request.toCommand(groupId, account));
        return ApiResponse.ok(RegisterNoticeResponse.of(serviceResponse));
    }
}
