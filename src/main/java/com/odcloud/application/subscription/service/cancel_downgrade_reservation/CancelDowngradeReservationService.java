package com.odcloud.application.subscription.service.cancel_downgrade_reservation;

import static com.odcloud.infrastructure.exception.ErrorCode.ACCESS_DENIED;
import static com.odcloud.infrastructure.exception.ErrorCode.Business_INVALID_SUBSCRIPTION_STATUS_FOR_DOWNGRADE_CANCEL;

import com.odcloud.application.subscription.port.in.CancelDowngradeReservationUseCase;
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
class CancelDowngradeReservationService implements CancelDowngradeReservationUseCase {

    private final SubscriptionStoragePort subscriptionStoragePort;

    @Override
    @Transactional
    public CancelDowngradeReservationResponse cancel(CancelDowngradeReservationCommand command) {
        Subscription subscription =
            subscriptionStoragePort.findByIdForUpdate(command.subscriptionId());

        if (!Objects.equals(subscription.getBuyerId(), command.account().getId())) {
            throw new CustomAuthorizationException(ACCESS_DENIED);
        }

        if (!subscription.isDownPending()) {
            throw new CustomBusinessException(
                Business_INVALID_SUBSCRIPTION_STATUS_FOR_DOWNGRADE_CANCEL);
        }

        subscriptionStoragePort.findByGroupIdAndStatus(subscription.getGroupId(), "PENDING")
            .ifPresent(pending -> subscriptionStoragePort.deleteById(pending.getId()));

        subscription.cancelDowngradeReservation();
        subscriptionStoragePort.save(subscription);

        return CancelDowngradeReservationResponse.ofSuccess();
    }
}
