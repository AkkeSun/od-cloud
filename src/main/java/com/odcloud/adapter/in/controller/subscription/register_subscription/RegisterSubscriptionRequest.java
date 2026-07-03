package com.odcloud.adapter.in.controller.subscription.register_subscription;

import com.odcloud.application.subscription.service.register_subscription.RegisterSubscriptionCommand;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.util.StringUtil;
import com.odcloud.infrastructure.validation.groups.ValidationGroups.NotBlankGroups;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
record RegisterSubscriptionRequest(

    @NotNull(message = "그룹 ID는 필수값입니다", groups = NotBlankGroups.class)
    Long groupId,

    @NotNull(message = "상품 ID는 필수값입니다", groups = NotBlankGroups.class)
    Long productId,

    @NotBlank(message = "빌링키는 필수값입니다", groups = NotBlankGroups.class)
    String billingKey
) {

    RegisterSubscriptionCommand toCommand(Account account) {
        return RegisterSubscriptionCommand.builder()
            .account(account)
            .groupId(groupId)
            .productId(productId)
            .billingKey(billingKey)
            .build();
    }

    @Override
    public String toString() {
        return StringUtil.toString(this);
    }
}
