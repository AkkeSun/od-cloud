package com.odcloud.application.subscription.service.reactivate_subscription;

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

class ReactivateSubscriptionServiceTest {

    private FakeSubscriptionStoragePort fakeSubscriptionStoragePort;
    private ReactivateSubscriptionService service;

    @BeforeEach
    void setUp() {
        fakeSubscriptionStoragePort = new FakeSubscriptionStoragePort();
        service = new ReactivateSubscriptionService(fakeSubscriptionStoragePort);
    }

    private Subscription setUpSubscription(Long buyerId, String status) {
        Subscription subscription = Subscription.builder()
            .id(1L)
            .productId(100L)
            .groupId(1L)
            .buyerId(buyerId)
            .status(status)
            .build();
        fakeSubscriptionStoragePort.subscriptionDatabase.add(subscription);
        return subscription;
    }

    private ReactivateSubscriptionCommand command(Long subscriptionId, Account account) {
        return ReactivateSubscriptionCommand.builder()
            .subscriptionId(subscriptionId)
            .account(account)
            .build();
    }

    @Nested
    @DisplayName("[reactivate] 구독을 재활성화하는 메소드")
    class Describe_reactivate {

        @Test
        @DisplayName("[success] EXP_PENDING 구독을 재활성화하면 ACTIVE로 전환된다")
        void success() {
            // given
            Subscription subscription = setUpSubscription(10L, "EXP_PENDING");
            Account account = Account.builder().id(10L).build();

            // when
            ReactivateSubscriptionResponse response =
                service.reactivate(command(subscription.getId(), account));

            // then
            assertThat(response.result()).isTrue();
            assertThat(fakeSubscriptionStoragePort.subscriptionDatabase.get(0).getStatus())
                .isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("[error] 존재하지 않는 구독이면 예외가 발생한다")
        void error_notFoundSubscription() {
            // given
            Account account = Account.builder().id(10L).build();

            // when & then
            assertThatThrownBy(() -> service.reactivate(command(999L, account)))
                .isInstanceOf(CustomBusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.Business_NOT_FOUND_SUBSCRIPTION);
        }

        @Test
        @DisplayName("[error] 구독의 buyerId와 로그인 사용자가 다르면 접근 권한 예외가 발생한다")
        void error_accessDenied() {
            // given
            Subscription subscription = setUpSubscription(10L, "EXP_PENDING");
            Account other = Account.builder().id(99L).build();

            // when & then
            assertThatThrownBy(() -> service.reactivate(command(subscription.getId(), other)))
                .isInstanceOf(CustomAuthorizationException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.ACCESS_DENIED);
        }

        @Test
        @DisplayName("[error] 구독 상태가 ACTIVE면 예외가 발생한다")
        void error_invalidStatus_active() {
            // given
            Subscription subscription = setUpSubscription(10L, "ACTIVE");
            Account account = Account.builder().id(10L).build();

            // when & then
            assertThatThrownBy(() -> service.reactivate(command(subscription.getId(), account)))
                .isInstanceOf(CustomBusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.Business_INVALID_SUBSCRIPTION_STATUS_FOR_REACTIVATE);
        }

        @Test
        @DisplayName("[error] 구독 상태가 EXPIRED면 예외가 발생한다")
        void error_invalidStatus_expired() {
            // given
            Subscription subscription = setUpSubscription(10L, "EXPIRED");
            Account account = Account.builder().id(10L).build();

            // when & then
            assertThatThrownBy(() -> service.reactivate(command(subscription.getId(), account)))
                .isInstanceOf(CustomBusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.Business_INVALID_SUBSCRIPTION_STATUS_FOR_REACTIVATE);
        }

        @Test
        @DisplayName("[error] 구독 상태가 DOWN_PENDING이면 예외가 발생한다")
        void error_invalidStatus_downPending() {
            // given
            Subscription subscription = setUpSubscription(10L, "DOWN_PENDING");
            Account account = Account.builder().id(10L).build();

            // when & then
            assertThatThrownBy(() -> service.reactivate(command(subscription.getId(), account)))
                .isInstanceOf(CustomBusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.Business_INVALID_SUBSCRIPTION_STATUS_FOR_REACTIVATE);
        }

        @Test
        @DisplayName("[error] 구독의 buyerId가 null이면 접근 권한 예외가 발생한다")
        void error_accessDenied_buyerIdIsNull() {
            // given
            Subscription subscription = setUpSubscription(null, "EXP_PENDING");
            Account account = Account.builder().id(10L).build();

            // when & then
            assertThatThrownBy(() -> service.reactivate(command(subscription.getId(), account)))
                .isInstanceOf(CustomAuthorizationException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.ACCESS_DENIED);
        }

        // 동시성 방어(비관적 락)는 FakeClass로는 실제 DB 락 동작을 검증할 수 없으므로
        // SubscriptionRepositoryTest(H2 통합 테스트)에서 findByIdForUpdate의 실제 락 동작을 검증한다.
    }
}
