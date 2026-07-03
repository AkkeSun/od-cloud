package com.odcloud.fakeClass;

import com.odcloud.application.subscription.port.out.SubscriptionDetail;
import com.odcloud.application.subscription.port.out.SubscriptionStoragePort;
import com.odcloud.domain.model.Subscription;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class FakeSubscriptionStoragePort implements SubscriptionStoragePort {

    public List<SubscriptionDetail> database = new ArrayList<>();
    public List<Subscription> subscriptionDatabase = new ArrayList<>();

    private final AtomicLong sequence = new AtomicLong(0);

    @Override
    public List<SubscriptionDetail> findActiveByGroupIds(List<Long> groupIds) {
        return new ArrayList<>(database);
    }

    @Override
    public boolean existsActiveByGroupIdAndProductId(Long groupId, Long productId) {
        return subscriptionDatabase.stream()
            .anyMatch(subscription -> subscription.getGroupId().equals(groupId)
                && subscription.getProductId().equals(productId)
                && "ACTIVE".equals(subscription.getStatus()));
    }

    @Override
    public Subscription save(Subscription subscription) {
        Subscription saved = Subscription.builder()
            .id(subscription.getId() == null ? sequence.incrementAndGet() : subscription.getId())
            .productId(subscription.getProductId())
            .groupId(subscription.getGroupId())
            .buyerId(subscription.getBuyerId())
            .status(subscription.getStatus())
            .billingKey(subscription.getBillingKey())
            .nextBillingDate(subscription.getNextBillingDate())
            .expiredDate(subscription.getExpiredDate())
            .modDt(subscription.getModDt())
            .regDt(subscription.getRegDt())
            .build();
        subscriptionDatabase.add(saved);
        return saved;
    }
}
