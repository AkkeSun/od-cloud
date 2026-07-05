package com.odcloud.fakeClass;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_NOT_FOUND_SUBSCRIPTION;

import com.odcloud.application.subscription.port.out.SubscriptionDetail;
import com.odcloud.application.subscription.port.out.SubscriptionStoragePort;
import com.odcloud.domain.model.Subscription;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    public Subscription findById(Long subscriptionId) {
        return subscriptionDatabase.stream()
            .filter(subscription -> subscription.getId().equals(subscriptionId))
            .findFirst()
            .orElseThrow(() -> new CustomBusinessException(Business_NOT_FOUND_SUBSCRIPTION));
    }

    @Override
    public List<Subscription> findByStatusAndNextBillingDateLoe(String status,
        LocalDate nextBillingDate) {
        return subscriptionDatabase.stream()
            .filter(subscription -> status.equals(subscription.getStatus())
                && subscription.getNextBillingDate() != null
                && !subscription.getNextBillingDate().isAfter(nextBillingDate))
            .toList();
    }

    @Override
    public List<Subscription> findByStatusAndExpiredDateLoe(String status,
        LocalDateTime expiredDate) {
        return subscriptionDatabase.stream()
            .filter(subscription -> status.equals(subscription.getStatus())
                && subscription.getExpiredDate() != null
                && !subscription.getExpiredDate().isAfter(expiredDate))
            .toList();
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
        subscriptionDatabase.removeIf(s -> s.getId() != null && s.getId().equals(saved.getId()));
        subscriptionDatabase.add(saved);
        return saved;
    }
}
