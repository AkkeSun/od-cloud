package com.odcloud.application.subscription.service.find_group_subscriptions;

import com.odcloud.application.subscription.port.in.FindGroupSubscriptionsUseCase;
import com.odcloud.application.subscription.port.out.SubscriptionDetail;
import com.odcloud.application.subscription.port.out.SubscriptionStoragePort;
import com.odcloud.domain.model.Account;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class FindGroupSubscriptionsService implements FindGroupSubscriptionsUseCase {

    private final SubscriptionStoragePort subscriptionStoragePort;

    @Override
    public List<FindGroupSubscriptionsResponse> find(Account account) {
        List<SubscriptionDetail> details = subscriptionStoragePort.findActiveByGroupIds(account.getGroupIds());
        return FindGroupSubscriptionsResponse.of(details);
    }
}
