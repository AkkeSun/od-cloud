package com.odcloud.application.subscription.service.cancel_downgrade_reservation;

import com.odcloud.domain.model.Account;
import lombok.Builder;

@Builder
public record CancelDowngradeReservationCommand(

    Long subscriptionId,

    Account account
) {

}