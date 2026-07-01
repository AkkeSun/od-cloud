package com.odcloud.fakeClass;

import com.odcloud.application.subscription.port.out.SubscriptionDetail;
import com.odcloud.application.subscription.port.out.SubscriptionStoragePort;
import java.util.ArrayList;
import java.util.List;

public class FakeSubscriptionStoragePort implements SubscriptionStoragePort {

    public List<SubscriptionDetail> database = new ArrayList<>();

    @Override
    public List<SubscriptionDetail> findActiveByGroupIds(List<Long> groupIds) {
        return new ArrayList<>(database);
    }
}
