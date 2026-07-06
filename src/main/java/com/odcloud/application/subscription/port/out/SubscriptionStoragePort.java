package com.odcloud.application.subscription.port.out;

import com.odcloud.domain.model.Subscription;
import java.time.LocalDate;
import java.util.List;

public interface SubscriptionStoragePort {

    List<SubscriptionDetail> findActiveByGroupIds(List<Long> groupIds);

    boolean existsActiveByGroupIdAndProductId(Long groupId, Long productId);

    Subscription findById(Long subscriptionId);

    List<Subscription> findByStatusAndNextBillingDateLoe(String status, LocalDate nextBillingDate);

    List<Subscription> findByStatusAndExpiredDateLoe(String status, LocalDate expiredDate);

    Subscription save(Subscription subscription);
}
