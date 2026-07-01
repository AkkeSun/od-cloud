package com.odcloud.application.subscription.port.in;

import com.odcloud.application.subscription.service.find_group_subscriptions.FindGroupSubscriptionsResponse;
import com.odcloud.domain.model.Account;

public interface FindGroupSubscriptionsUseCase {

    FindGroupSubscriptionsResponse find(Account account);
}
