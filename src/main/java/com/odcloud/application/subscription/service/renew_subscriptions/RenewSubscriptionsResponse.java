package com.odcloud.application.subscription.service.renew_subscriptions;

import lombok.Builder;

@Builder
public record RenewSubscriptionsResponse(
    int totalCount,
    int successCount,
    int failCount
) {

}
