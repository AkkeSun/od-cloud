package com.odcloud.application.subscription.service.register_subscription;

import com.odcloud.domain.model.Account;
import lombok.Builder;

@Builder
public record RegisterSubscriptionCommand(

    Account account,

    Long groupId,

    Long productId,

    String billingKey
) {

    public boolean groupAccessDenied() {
        return groupId == null || !account().getGroupIds().contains(groupId);
    }
}
