package com.odcloud.adapter.in.controller.subscription.cancel_subscription;

import com.odcloud.application.subscription.port.in.CancelSubscriptionUseCase;
import com.odcloud.application.subscription.service.cancel_subscription.CancelSubscriptionCommand;
import com.odcloud.application.subscription.service.cancel_subscription.CancelSubscriptionResponse;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.resolver.LoginAccount;
import com.odcloud.infrastructure.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class CancelSubscriptionController {

    private final CancelSubscriptionUseCase useCase;

    @PatchMapping("/subscriptions/{subscriptionId}")
    ApiResponse<CancelSubscriptionResponse> cancel(
        @PathVariable Long subscriptionId,
        @LoginAccount Account account
    ) {
        return ApiResponse.ok(useCase.cancel(CancelSubscriptionCommand.builder()
            .subscriptionId(subscriptionId)
            .account(account)
            .build()));
    }
}
