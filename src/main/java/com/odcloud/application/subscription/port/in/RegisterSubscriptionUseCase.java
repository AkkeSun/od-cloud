package com.odcloud.application.subscription.port.in;

import com.odcloud.application.subscription.service.register_subscription.RegisterSubscriptionCommand;
import com.odcloud.application.subscription.service.register_subscription.RegisterSubscriptionResponse;

public interface RegisterSubscriptionUseCase {

    RegisterSubscriptionResponse register(RegisterSubscriptionCommand command);
}
