package com.odcloud.application.subscription.service.reactivate_subscription;

import lombok.Builder;

@Builder
public record ReactivateSubscriptionResponse(
    Boolean result
) {

    public static ReactivateSubscriptionResponse ofSuccess() {
        return ReactivateSubscriptionResponse.builder()
            .result(Boolean.TRUE)
            .build();
    }
}
