package com.odcloud.adapter.in.controller.subscription.modify_subscription_plan;

import com.odcloud.application.subscription.port.in.ModifySubscriptionPlanUseCase;
import com.odcloud.application.subscription.service.modify_subscription_plan.ModifySubscriptionPlanResponse;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.resolver.LoginAccount;
import com.odcloud.infrastructure.response.ApiResponse;
import com.odcloud.infrastructure.validation.groups.ValidationSequence;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class ModifySubscriptionPlanController {

    private final ModifySubscriptionPlanUseCase useCase;

    @PatchMapping("/subscriptions/plans")
    ApiResponse<ModifySubscriptionPlanResponse> modify(
        @Validated(ValidationSequence.class) @RequestBody ModifySubscriptionPlanRequest request,
        @LoginAccount Account account
    ) {
        return ApiResponse.ok(useCase.modify(request.toCommand(account)));
    }
}
