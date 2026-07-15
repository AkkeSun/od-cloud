package com.odcloud.fakeClass;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_NOT_FOUND_SUBSCRIPTION;

import com.odcloud.application.subscription.port.out.SubscriptionDetail;
import com.odcloud.application.subscription.port.out.SubscriptionStoragePort;
import com.odcloud.domain.model.Subscription;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class FakeSubscriptionStoragePort implements SubscriptionStoragePort {

    private static final List<String> RENEW_TARGET_STATUSES = List.of("ACTIVE", "PENDING");
    private static final List<String> EXPIRE_TARGET_STATUSES = List.of("EXP_PENDING", "DOWN_PENDING");
    private static final List<String> ACTIVE_LIKE_STATUSES =
        List.of("ACTIVE", "EXP_PENDING", "DOWN_PENDING");

    /**
     * find_group_subscriptions 처럼 상품명/그룹명 등 subscriptionDatabase(Subscription)에는
     * 없는 정보 및 "구독이 아예 없는 상품" 행(subscriptionId=null)까지 임의로 구성해야 하는
     * 테스트를 위한 수동 시딩용 저장소. findActiveByGroupIds 는 이 목록과 subscriptionDatabase
     * 파생 결과를 합쳐서 반환한다.
     */
    public List<SubscriptionDetail> database = new ArrayList<>();
    public List<Subscription> subscriptionDatabase = new ArrayList<>();

    private final AtomicLong sequence = new AtomicLong(0);

    /**
     * 실제 SubscriptionRepository.findActiveByGroupIds 의 의미(groupId 일치 + ACTIVE/EXP_PENDING/
     * DOWN_PENDING 상태만) 를 subscriptionDatabase 의 현재 상태로부터 그대로 반영한다. 배치 처리
     * 중 이미 EXPIRED 로 변경되어 저장된 구독은 자연히 결과에서 제외된다.
     */
    @Override
    public List<SubscriptionDetail> findActiveByGroupIds(List<Long> groupIds) {
        List<SubscriptionDetail> derived = subscriptionDatabase.stream()
            .filter(subscription -> groupIds.contains(subscription.getGroupId()))
            .filter(subscription -> ACTIVE_LIKE_STATUSES.contains(subscription.getStatus()))
            .map(subscription -> new SubscriptionDetail(
                subscription.getProductId(),
                null,
                subscription.getId(),
                subscription.getGroupId(),
                null,
                null,
                subscription.getStatus(),
                subscription.getExpiredDate()))
            .toList();

        List<SubscriptionDetail> result = new ArrayList<>(database);
        result.addAll(derived);
        return result;
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
    public Optional<Subscription> findByGroupIdAndStatus(Long groupId, String status) {
        return subscriptionDatabase.stream()
            .filter(subscription -> subscription.getGroupId().equals(groupId)
                && status.equals(subscription.getStatus()))
            .findFirst();
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

    @Override
    public void deleteById(Long subscriptionId) {
        subscriptionDatabase.removeIf(s -> s.getId() != null && s.getId().equals(subscriptionId));
    }
}
