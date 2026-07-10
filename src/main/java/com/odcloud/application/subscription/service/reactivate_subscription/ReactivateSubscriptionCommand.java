package com.odcloud.application.subscription.service.reactivate_subscription;

import com.odcloud.domain.model.Account;
import lombok.Builder;

@Builder
public record ReactivateSubscriptionCommand(

    Long subscriptionId,

    Account account
) {

}
