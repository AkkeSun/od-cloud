package com.odcloud.application.subscription.port.in;

import com.odcloud.application.subscription.service.reactivate_subscription.ReactivateSubscriptionCommand;
import com.odcloud.application.subscription.service.reactivate_subscription.ReactivateSubscriptionResponse;

public interface ReactivateSubscriptionUseCase {

    ReactivateSubscriptionResponse reactivate(ReactivateSubscriptionCommand command);
}
