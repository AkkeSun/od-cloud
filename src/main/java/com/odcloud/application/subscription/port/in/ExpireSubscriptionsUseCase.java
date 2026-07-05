package com.odcloud.application.subscription.port.in;

import com.odcloud.application.subscription.service.expire_subscriptions.ExpireSubscriptionsResponse;

public interface ExpireSubscriptionsUseCase {

    ExpireSubscriptionsResponse expire();
}
