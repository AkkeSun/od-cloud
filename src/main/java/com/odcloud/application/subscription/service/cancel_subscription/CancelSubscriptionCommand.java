package com.odcloud.application.subscription.service.cancel_subscription;

import com.odcloud.domain.model.Account;
import lombok.Builder;

@Builder
public record CancelSubscriptionCommand(

    Long subscriptionId,

    Account account
) {

}
