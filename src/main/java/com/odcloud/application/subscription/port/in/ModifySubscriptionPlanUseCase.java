package com.odcloud.application.subscription.port.in;

import com.odcloud.application.subscription.service.modify_subscription_plan.ModifySubscriptionPlanCommand;
import com.odcloud.application.subscription.service.modify_subscription_plan.ModifySubscriptionPlanResponse;

public interface ModifySubscriptionPlanUseCase {

    ModifySubscriptionPlanResponse modify(ModifySubscriptionPlanCommand command);
}
