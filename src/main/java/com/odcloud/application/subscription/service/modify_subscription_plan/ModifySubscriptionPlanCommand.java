package com.odcloud.application.subscription.service.modify_subscription_plan;

import com.odcloud.domain.model.Account;
import lombok.Builder;

@Builder
public record ModifySubscriptionPlanCommand(

    Account account,

    Long currentSubscriptionId,

    Long newProductId
) {

}
