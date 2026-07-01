package com.odcloud.application.subscription.port.in;

import com.odcloud.application.subscription.service.find_group_subscriptions.FindGroupSubscriptionsResponse;
import com.odcloud.domain.model.Account;
import java.util.List;

public interface FindGroupSubscriptionsUseCase {

    List<FindGroupSubscriptionsResponse> find(Account account);
}
