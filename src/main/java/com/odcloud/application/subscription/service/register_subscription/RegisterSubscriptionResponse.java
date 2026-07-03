package com.odcloud.application.subscription.service.register_subscription;

import lombok.Builder;

@Builder
public record RegisterSubscriptionResponse(
    Boolean result,
    Long subscriptionId,
    Long paymentId
) {

    public static RegisterSubscriptionResponse of(Long subscriptionId, Long paymentId) {
        return RegisterSubscriptionResponse.builder()
            .result(Boolean.TRUE)
            .subscriptionId(subscriptionId)
            .paymentId(paymentId)
            .build();
    }
}
