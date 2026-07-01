package com.odcloud.application.subscription.port.out;

import java.util.List;

public interface SubscriptionStoragePort {

    List<SubscriptionDetail> findActiveByGroupIds(List<Long> groupIds);
}
