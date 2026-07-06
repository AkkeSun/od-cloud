package com.odcloud.application.subscription.port.out;

import com.odcloud.domain.model.Subscription;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SubscriptionStoragePort {

    List<SubscriptionDetail> findActiveByGroupIds(List<Long> groupIds);

    boolean existsActiveByGroupIdAndProductId(Long groupId, Long productId);

    Subscription findById(Long subscriptionId);

    Subscription findByIdForUpdate(Long subscriptionId);

    Optional<Subscription> findByGroupIdAndStatus(Long groupId, String status);

    List<Subscription> findByRenewTargets(LocalDate nextBillingDate);

    List<Subscription> findExpiredTargets(LocalDate expiredDate);

    Subscription save(Subscription subscription);

    void deleteById(Long subscriptionId);
}
