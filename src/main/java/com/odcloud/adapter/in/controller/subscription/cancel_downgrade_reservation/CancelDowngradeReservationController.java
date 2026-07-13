package com.odcloud.adapter.in.controller.subscription.cancel_downgrade_reservation;

import com.odcloud.application.subscription.port.in.CancelDowngradeReservationUseCase;
import com.odcloud.application.subscription.service.cancel_downgrade_reservation.CancelDowngradeReservationCommand;
import com.odcloud.application.subscription.service.cancel_downgrade_reservation.CancelDowngradeReservationResponse;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.resolver.LoginAccount;
import com.odcloud.infrastructure.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class CancelDowngradeReservationController {

    private final CancelDowngradeReservationUseCase useCase;

    @PatchMapping("/subscriptions/{subscriptionId}/downgrade-cancellation")
    ApiResponse<CancelDowngradeReservationResponse> cancel(
        @PathVariable Long subscriptionId,
        @LoginAccount Account account
    ) {
        return ApiResponse.ok(useCase.cancel(CancelDowngradeReservationCommand.builder()
            .subscriptionId(subscriptionId)
            .account(account)
            .build()));
    }
}
