package com.odcloud.adapter.in.controller.subscription.modify_subscription_plan;

import com.odcloud.application.subscription.service.modify_subscription_plan.ModifySubscriptionPlanCommand;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.util.StringUtil;
import com.odcloud.infrastructure.validation.groups.ValidationGroups.NotBlankGroups;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
record ModifySubscriptionPlanRequest(

    @NotNull(message = "현재 구독 ID는 필수값입니다", groups = NotBlankGroups.class)
    Long currentSubscriptionId,

    @NotNull(message = "신규 상품 ID는 필수값입니다", groups = NotBlankGroups.class)
    Long newProductId
) {

    ModifySubscriptionPlanCommand toCommand(Account account) {
        return ModifySubscriptionPlanCommand.builder()
            .account(account)
            .currentSubscriptionId(currentSubscriptionId)
            .newProductId(newProductId)
            .build();
    }

    @Override
    public String toString() {
        return StringUtil.toString(this);
    }
}
