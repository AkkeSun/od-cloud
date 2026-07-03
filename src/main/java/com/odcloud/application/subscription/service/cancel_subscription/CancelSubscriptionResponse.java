package com.odcloud.application.subscription.service.cancel_subscription;

import lombok.Builder;

@Builder
public record CancelSubscriptionResponse(
    Boolean result
) {

    public static CancelSubscriptionResponse ofSuccess() {
        return CancelSubscriptionResponse.builder()
            .result(Boolean.TRUE)
            .build();
    }
}
