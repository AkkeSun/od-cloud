package com.odcloud.application.subscription.service.modify_subscription_plan;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record ModifySubscriptionPlanResponse(
    Boolean result,
    String changeType,
    Long previousSubscriptionId,
    Long newSubscriptionId,
    Long paymentId,
    BigDecimal chargedAmount
) {

    public static ModifySubscriptionPlanResponse ofUpgrade(
        Long previousSubscriptionId,
        Long newSubscriptionId,
        Long paymentId,
        BigDecimal chargedAmount
    ) {
        return ModifySubscriptionPlanResponse.builder()
            .result(Boolean.TRUE)
            .changeType("UPGRADE")
            .previousSubscriptionId(previousSubscriptionId)
            .newSubscriptionId(newSubscriptionId)
            .paymentId(paymentId)
            .chargedAmount(chargedAmount)
            .build();
    }

    public static ModifySubscriptionPlanResponse ofDowngrade(
        Long previousSubscriptionId,
        Long newSubscriptionId
    ) {
        return ModifySubscriptionPlanResponse.builder()
            .result(Boolean.TRUE)
            .changeType("DOWNGRADE")
            .previousSubscriptionId(previousSubscriptionId)
            .newSubscriptionId(newSubscriptionId)
            .paymentId(null)
            .chargedAmount(BigDecimal.ZERO)
            .build();
    }
}
