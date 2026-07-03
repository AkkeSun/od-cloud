package com.odcloud.application.subscription.port.out;

import com.odcloud.domain.model.Subscription;
import java.util.List;

public interface SubscriptionStoragePort {

    List<SubscriptionDetail> findActiveByGroupIds(List<Long> groupIds);

    boolean existsActiveByGroupIdAndProductId(Long groupId, Long productId);

    Subscription save(Subscription subscription);
}
