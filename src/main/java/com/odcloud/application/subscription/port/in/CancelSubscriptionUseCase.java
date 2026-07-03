package com.odcloud.application.subscription.port.in;

import com.odcloud.application.subscription.service.cancel_subscription.CancelSubscriptionCommand;
import com.odcloud.application.subscription.service.cancel_subscription.CancelSubscriptionResponse;

public interface CancelSubscriptionUseCase {

    CancelSubscriptionResponse cancel(CancelSubscriptionCommand command);
}
