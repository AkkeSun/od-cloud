package com.odcloud.application.subscription.service.cancel_subscription;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.Subscription;
import com.odcloud.fakeClass.FakeSubscriptionStoragePort;
import com.odcloud.infrastructure.exception.CustomAuthenticationException;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CancelSubscriptionServiceTest {

    private FakeSubscriptionStoragePort fakeSubscriptionStoragePort;
    private CancelSubscriptionService service;

    @BeforeEach
    void setUp() {
        fakeSubscriptionStoragePort = new FakeSubscriptionStoragePort();
        service = new CancelSubscriptionService(fakeSubscriptionStoragePort);
    }

    private Subscription setUpActiveSubscription(Long buyerId) {
        Subscription subscription = Subscription.builder()
            .id(1L)
            .productId(100L)
            .groupId(1L)
            .buyerId(buyerId)
            .status("ACTIVE")
            .build();
        fakeSubscriptionStoragePort.subscriptionDatabase.add(subscription);
        return subscription;
    }

    private CancelSubscriptionCommand command(Long subscriptionId, Account account) {
        return CancelSubscriptionCommand.builder()
            .subscriptionId(subscriptionId)
            .account(account)
            .build();
    }

    @Nested
    @DisplayName("[cancel] 구독을 취소하는 메소드")
    class Describe_cancel {

        @Test
        @DisplayName("[success] ACTIVE 구독을 취소하면 EXP_PENDING 으로 전환된다")
        void success() {
            // given
            Subscription subscription = setUpActiveSubscription(10L);
            Account account = Account.builder().id(10L).build();

            // when
            CancelSubscriptionResponse response = service.cancel(command(subscription.getId(), account));

            // then
            assertThat(response.result()).isTrue();
            assertThat(fakeSubscriptionStoragePort.subscriptionDatabase.get(0).getStatus())
                .isEqualTo("EXP_PENDING");
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
            Subscription subscription = setUpActiveSubscription(10L);
            Account other = Account.builder().id(99L).build();

            // when & then
            assertThatThrownBy(() -> service.cancel(command(subscription.getId(), other)))
                .isInstanceOf(CustomAuthenticationException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.ACCESS_DENIED);
        }

        @Test
        @DisplayName("[error] 구독 상태가 ACTIVE가 아니면 예외가 발생한다")
        void error_invalidStatus() {
            // given
            Subscription subscription = Subscription.builder()
                .id(1L)
                .buyerId(10L)
                .status("EXP_PENDING")
                .build();
            fakeSubscriptionStoragePort.subscriptionDatabase.add(subscription);
            Account account = Account.builder().id(10L).build();

            // when & then
            assertThatThrownBy(() -> service.cancel(command(subscription.getId(), account)))
                .isInstanceOf(CustomBusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.Business_INVALID_SUBSCRIPTION_STATUS);
        }
    }
}
