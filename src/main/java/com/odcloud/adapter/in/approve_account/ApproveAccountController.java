package com.odcloud.adapter.in.approve_account;

import com.odcloud.application.port.in.ApproveAccountUseCase;
import com.odcloud.application.service.approve_account.ApproveAccountServiceResponse;
import com.odcloud.infrastructure.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class ApproveAccountController {

    private final ApproveAccountUseCase useCase;

    @PutMapping("/accounts/{userId}/approval")
    ApiResponse<ApproveAccountResponse> confirm(@PathVariable Long userId) {
        ApproveAccountServiceResponse serviceResponse = useCase.approve(userId);

        return ApiResponse.ok(ApproveAccountResponse.of(serviceResponse));
    }
}
