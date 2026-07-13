package com.odcloud.application.subscription.service.cancel_downgrade_reservation;

import lombok.Builder;

@Builder
public record CancelDowngradeReservationResponse(
    Boolean result
) {

    public static CancelDowngradeReservationResponse ofSuccess() {
        return CancelDowngradeReservationResponse.builder()
            .result(Boolean.TRUE)
            .build();
    }
}