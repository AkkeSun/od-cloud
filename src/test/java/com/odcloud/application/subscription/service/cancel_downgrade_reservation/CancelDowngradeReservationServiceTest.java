package com.odcloud.application.subscription.service.cancel_downgrade_reservation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.Subscription;
import com.odcloud.fakeClass.FakeSubscriptionStoragePort;
import com.odcloud.infrastructure.exception.CustomAuthorizationException;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CancelDowngradeReservationServiceTest {

    private FakeSubscriptionStoragePort fakeSubscriptionStoragePort;
    private CancelDowngradeReservationService service;

    @BeforeEach
    void setUp() {
        fakeSubscriptionStoragePort = new FakeSubscriptionStoragePort();
        service = new CancelDowngradeReservationService(fakeSubscriptionStoragePort);
    }

    private Subscription setUpSubscription(Long buyerId, String status) {
        Subscription subscription = Subscription.builder()
            .id(1L)
            .productId(3L)
            .groupId(1L)
            .buyerId(buyerId)
            .status(status)
            .build();
        fakeSubscriptionStoragePort.subscriptionDatabase.add(subscription);
        return subscription;
    }

    private CancelDowngradeReservationCommand command(Long subscriptionId, Account account) {
        return CancelDowngradeReservationCommand.builder()
            .subscriptionId(subscriptionId)
            .account(account)
            .build();
    }

    @Nested
    @DisplayName("[cancel] 다운그레이드 예약을 취소하는 메소드")
    class Describe_cancel {

        @Test
        @DisplayName("[success] DOWN_PENDING 구독을 ACTIVE로 되돌린다")
        void success() {
            // given
            Subscription subscription = setUpSubscription(10L, "DOWN_PENDING");
            Account account = Account.builder().id(10L).build();

            // when
            CancelDowngradeReservationResponse response =
                service.cancel(command(subscription.getId(), account));

            // then
            assertThat(response.result()).isTrue();
            assertThat(fakeSubscriptionStoragePort.subscriptionDatabase.get(0).getStatus())
                .isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("[success] 같은 그룹의 다운그레이드 예약(PENDING) 구독을 삭제한다")
        void success_deletesPendingDowngradeTarget() {
            // given
            Subscription subscription = setUpSubscription(10L, "DOWN_PENDING");
            Subscription pendingDowngrade = Subscription.builder()
                .id(2L)
                .productId(2L)
                .groupId(subscription.getGroupId())
                .buyerId(10L)
                .status("PENDING")
                .build();
            fakeSubscriptionStoragePort.subscriptionDatabase.add(pendingDowngrade);
            Account account = Account.builder().id(10L).build();

            // when
            CancelDowngradeReservationResponse response =
                service.cancel(command(subscription.getId(), account));

            // then
            assertThat(response.result()).isTrue();
            assertThat(fakeSubscriptionStoragePort.subscriptionDatabase)
                .extracting(Subscription::getId)
                .containsExactly(subscription.getId());
        }

        @Test
        @DisplayName("[error] 존재하지 않는 구독이면 예외가 발생한다")
        void error_notFoundSubscription() {
            // given
            Account account = Account.builder().id(10L).build();

            // when & then
            assertThatThrownBy(() -> service.cancel(command(999L, account)))
                .isInstanceOf(CustomBusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.Business_NOT_FOUND_SUBSCRIPTION);
        }

        @Test
        @DisplayName("[error] 구독의 buyerId와 로그인 사용자가 다르면 접근 권한 예외가 발생한다")
        void error_accessDenied() {
            // given
            Subscription subscription = setUpSubscription(10L, "DOWN_PENDING");
            Account other = Account.builder().id(99L).build();

            // when & then
            assertThatThrownBy(() -> service.cancel(command(subscription.getId(), other)))
                .isInstanceOf(CustomAuthorizationException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.ACCESS_DENIED);
        }

        @Test
        @DisplayName("[error] 구독 상태가 DOWN_PENDING이 아니면 예외가 발생한다")
        void error_invalidStatus() {
            // given
            Subscription subscription = setUpSubscription(10L, "ACTIVE");
            Account account = Account.builder().id(10L).build();

            // when & then
            assertThatThrownBy(() -> service.cancel(command(subscription.getId(), account)))
                .isInstanceOf(CustomBusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.Business_INVALID_SUBSCRIPTION_STATUS_FOR_DOWNGRADE_CANCEL);
        }
    }
}
