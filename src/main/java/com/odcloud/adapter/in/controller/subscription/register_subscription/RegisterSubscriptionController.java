package com.odcloud.adapter.in.controller.subscription.register_subscription;

import com.odcloud.application.subscription.port.in.RegisterSubscriptionUseCase;
import com.odcloud.application.subscription.service.register_subscription.RegisterSubscriptionResponse;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.resolver.LoginAccount;
import com.odcloud.infrastructure.response.ApiResponse;
import com.odcloud.infrastructure.validation.groups.ValidationSequence;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class RegisterSubscriptionController {

    private final RegisterSubscriptionUseCase useCase;

    @PostMapping("/subscriptions")
    ApiResponse<RegisterSubscriptionResponse> register(
        @Validated(ValidationSequence.class) @RequestBody RegisterSubscriptionRequest request,
        @LoginAccount Account account
    ) {
        return ApiResponse.ok(useCase.register(request.toCommand(account)));
    }
}
