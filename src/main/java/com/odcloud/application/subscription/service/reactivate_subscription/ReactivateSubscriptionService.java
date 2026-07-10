package com.odcloud.application.subscription.service.reactivate_subscription;

import static com.odcloud.infrastructure.exception.ErrorCode.ACCESS_DENIED;
import static com.odcloud.infrastructure.exception.ErrorCode.Business_INVALID_SUBSCRIPTION_STATUS_FOR_REACTIVATE;

import com.odcloud.application.subscription.port.in.ReactivateSubscriptionUseCase;
import com.odcloud.application.subscription.port.out.SubscriptionStoragePort;
import com.odcloud.domain.model.Subscription;
import com.odcloud.infrastructure.exception.CustomAuthorizationException;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class ReactivateSubscriptionService implements ReactivateSubscriptionUseCase {

    private final SubscriptionStoragePort subscriptionStoragePort;

    @Override
    @Transactional
    public ReactivateSubscriptionResponse reactivate(ReactivateSubscriptionCommand command) {
        Subscription subscription =
            subscriptionStoragePort.findByIdForUpdate(command.subscriptionId());

        if (!Objects.equals(subscription.getBuyerId(), command.account().getId())) {
            throw new CustomAuthorizationException(ACCESS_DENIED);
        }

        if (!subscription.isReactivatable()) {
            throw new CustomBusinessException(Business_INVALID_SUBSCRIPTION_STATUS_FOR_REACTIVATE);
        }

        subscription.reactivate();
        subscriptionStoragePort.save(subscription);

        return ReactivateSubscriptionResponse.ofSuccess();
    }
}
