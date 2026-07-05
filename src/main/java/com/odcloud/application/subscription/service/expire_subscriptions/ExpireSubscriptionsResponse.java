package com.odcloud.application.subscription.service.expire_subscriptions;

import lombok.Builder;

@Builder
public record ExpireSubscriptionsResponse(
    int totalCount,
    int successCount,
    int failCount
) {

}
