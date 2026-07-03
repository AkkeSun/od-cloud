package com.odcloud.adapter.out.persistence.jpa;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_NOT_FOUND_SUBSCRIPTION;

import com.odcloud.application.subscription.port.out.SubscriptionDetail;
import com.odcloud.application.subscription.port.out.SubscriptionStoragePort;
import com.odcloud.domain.model.Subscription;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import java.util.List;
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
    public Subscription save(Subscription subscription) {
        return repository.save(subscription);
    }
}
