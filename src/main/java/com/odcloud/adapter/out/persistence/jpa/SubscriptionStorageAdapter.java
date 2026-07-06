package com.odcloud.adapter.out.persistence.jpa;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_NOT_FOUND_SUBSCRIPTION;

import com.odcloud.application.subscription.port.out.SubscriptionDetail;
import com.odcloud.application.subscription.port.out.SubscriptionStoragePort;
import com.odcloud.domain.model.Subscription;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class SubscriptionStorageAdapter implements SubscriptionStoragePort {

    private final SubscriptionRepository repository;

    @Override
    public List<SubscriptionDetail> findActiveByGroupIds(List<Long> groupIds) {
        return repository.findActiveByGroupIds(groupIds);
    }

    @Override
    public boolean existsActiveByGroupIdAndProductId(Long groupId, Long productId) {
        return repository.existsActiveByGroupIdAndProductId(groupId, productId);
    }

    @Override
    public Subscription findById(Long subscriptionId) {
        SubscriptionEntity entity = repository.findById(subscriptionId);
        if (entity == null) {
            throw new CustomBusinessException(Business_NOT_FOUND_SUBSCRIPTION);
        }
        return entity.toDomain();
    }

    @Override
    public Subscription findByIdForUpdate(Long subscriptionId) {
        SubscriptionEntity entity = repository.findByIdForUpdate(subscriptionId);
        if (entity == null) {
            throw new CustomBusinessException(Business_NOT_FOUND_SUBSCRIPTION);
        }
        return entity.toDomain();
    }

    @Override
    public Optional<Subscription> findByGroupIdAndStatus(Long groupId, String status) {
        return repository.findByGroupIdAndStatus(groupId, status)
            .map(SubscriptionEntity::toDomain);
    }

    @Override
    public List<Subscription> findByRenewTargets(LocalDate nextBillingDate) {
        return repository.findByRenewTargets(List.of("ACTIVE", "PENDING"), nextBillingDate);
    }

    @Override
    public List<Subscription> findExpiredTargets(LocalDate expiredDate) {
        return repository.findExpiredTargets(List.of("EXP_PENDING", "DOWN_PENDING"), expiredDate);
    }

    @Override
    public Subscription save(Subscription subscription) {
        return repository.save(subscription);
    }

    @Override
    public void deleteById(Long subscriptionId) {
        repository.deleteById(subscriptionId);
    }
}
