package com.odcloud.adapter.in.controller.subscription.reactivate_subscription;

import com.odcloud.application.subscription.port.in.ReactivateSubscriptionUseCase;
import com.odcloud.application.subscription.service.reactivate_subscription.ReactivateSubscriptionCommand;
import com.odcloud.application.subscription.service.reactivate_subscription.ReactivateSubscriptionResponse;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.resolver.LoginAccount;
import com.odcloud.infrastructure.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class ReactivateSubscriptionController {

    private final ReactivateSubscriptionUseCase useCase;

    @PatchMapping("/subscriptions/{subscriptionId}/reactivation")
    ApiResponse<ReactivateSubscriptionResponse> reactivate(
        @PathVariable Long subscriptionId,
        @LoginAccount Account account
    ) {
        return ApiResponse.ok(useCase.reactivate(ReactivateSubscriptionCommand.builder()
            .subscriptionId(subscriptionId)
            .account(account)
            .build()));
    }
}
