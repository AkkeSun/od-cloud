package com.odcloud.application.subscription.port.in;

import com.odcloud.application.subscription.service.renew_subscriptions.RenewSubscriptionsResponse;

public interface RenewSubscriptionsUseCase {

    RenewSubscriptionsResponse renew();
}
