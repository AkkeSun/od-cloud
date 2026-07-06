package com.odcloud.fakeClass;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_NOT_FOUND_SUBSCRIPTION;

import com.odcloud.application.subscription.port.out.SubscriptionDetail;
import com.odcloud.application.subscription.port.out.SubscriptionStoragePort;
import com.odcloud.domain.model.Subscription;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class FakeSubscriptionStoragePort implements SubscriptionStoragePort {

    private static final List<String> RENEW_TARGET_STATUSES = List.of("ACTIVE", "PENDING");
    private static final List<String> EXPIRE_TARGET_STATUSES = List.of("EXP_PENDING", "DOWN_PENDING");

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

    /**
     * FakeClass는 단일 스레드 인메모리 리스트일 뿐이며 실제 DB 락을 모델링하지 않는다.
     * 인터페이스 구현을 위해 findById와 동일하게 동작한다 — 실제 비관적 락 검증은
     * SubscriptionRepository/SubscriptionStorageAdapter의 H2 통합 테스트에서 수행한다.
     */
    @Override
    public Subscription findByIdForUpdate(Long subscriptionId) {
        return findById(subscriptionId);
    }

    @Override
    public List<Subscription> findByRenewTargets(LocalDate nextBillingDate) {
        return subscriptionDatabase.stream()
            .filter(subscription -> RENEW_TARGET_STATUSES.contains(subscription.getStatus())
                && subscription.getNextBillingDate() != null
                && !subscription.getNextBillingDate().isAfter(nextBillingDate))
            .toList();
    }

    @Override
    public List<Subscription> findExpiredTargets(LocalDate expiredDate) {
        return subscriptionDatabase.stream()
            .filter(subscription -> EXPIRE_TARGET_STATUSES.contains(subscription.getStatus())
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
