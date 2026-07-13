package com.odcloud.application.subscription.port.in;

import com.odcloud.application.subscription.service.cancel_downgrade_reservation.CancelDowngradeReservationCommand;
import com.odcloud.application.subscription.service.cancel_downgrade_reservation.CancelDowngradeReservationResponse;

public interface CancelDowngradeReservationUseCase {

    CancelDowngradeReservationResponse cancel(CancelDowngradeReservationCommand command);
}