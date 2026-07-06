package com.odcloud.application.subscription.service.cancel_subscription;

import static com.odcloud.infrastructure.exception.ErrorCode.ACCESS_DENIED;
import static com.odcloud.infrastructure.exception.ErrorCode.Business_INVALID_SUBSCRIPTION_STATUS;

import com.odcloud.application.subscription.port.in.CancelSubscriptionUseCase;
import com.odcloud.application.subscription.port.out.SubscriptionStoragePort;
import com.odcloud.domain.model.Subscription;
import com.odcloud.infrastructure.exception.CustomAuthenticationException;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class CancelSubscriptionService implements CancelSubscriptionUseCase {

    private final SubscriptionStoragePort subscriptionStoragePort;

    @Override
    @Transactional
    public CancelSubscriptionResponse cancel(CancelSubscriptionCommand command) {
        Subscription subscription = subscriptionStoragePort.findById(command.subscriptionId());

        if (!subscription.getBuyerId().equals(command.account().getId())) {
            throw new CustomAuthenticationException(ACCESS_DENIED);
        }

        if (!subscription.isCancelable()) {
            throw new CustomBusinessException(Business_INVALID_SUBSCRIPTION_STATUS);
        }

        if (subscription.isDownPending()) {
            subscriptionStoragePort.findByGroupIdAndStatus(subscription.getGroupId(), "PENDING")
                .ifPresent(pending -> subscriptionStoragePort.deleteById(pending.getId()));
        }

        subscription.cancel();
        subscriptionStoragePort.save(subscription);

        return CancelSubscriptionResponse.ofSuccess();
    }
}
