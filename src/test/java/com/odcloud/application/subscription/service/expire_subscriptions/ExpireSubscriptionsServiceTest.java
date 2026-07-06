package com.odcloud.application.subscription.service.expire_subscriptions;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.domain.model.Subscription;
import com.odcloud.fakeClass.FakeSubscriptionStoragePort;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ExpireSubscriptionsServiceTest {

    private FakeSubscriptionStoragePort fakeSubscriptionStoragePort;
    private ExpireSubscriptionsService service;

    @BeforeEach
    void setUp() {
        fakeSubscriptionStoragePort = new FakeSubscriptionStoragePort();
        service = new ExpireSubscriptionsService(fakeSubscriptionStoragePort);
    }

    private Subscription subscription(Long id, String status, LocalDate expiredDate) {
        return Subscription.builder()
            .id(id)
            .productId(100L)
            .groupId(1L)
            .buyerId(1L)
            .status(status)
            .expiredDate(expiredDate)
            .build();
    }

    @Nested
    @DisplayName("[expire] 구독 만료 처리")
    class Describe_expire {

        @Test
        @DisplayName("[success] EXP_PENDING 이면서 만료일이 지난 구독의 status 를 EXPIRED 로 변경한다")
        void success() {
            // given
            fakeSubscriptionStoragePort.subscriptionDatabase.add(
                subscription(1L, "EXP_PENDING", LocalDate.now().minusDays(1)));

            // when
            ExpireSubscriptionsResponse response = service.expire();

            // then
            assertThat(response.totalCount()).isEqualTo(1);
            assertThat(response.successCount()).isEqualTo(1);
            assertThat(response.failCount()).isZero();
            assertThat(fakeSubscriptionStoragePort.findById(1L).getStatus()).isEqualTo("EXPIRED");
        }

        @Test
        @DisplayName("[skip] 만료일이 지나지 않았거나 EXP_PENDING/DOWN_PENDING 이 아니면 대상이 아니다")
        void skip_notTarget() {
            // given
            fakeSubscriptionStoragePort.subscriptionDatabase.add(
                subscription(1L, "EXP_PENDING", LocalDate.now().plusDays(1)));
            fakeSubscriptionStoragePort.subscriptionDatabase.add(
                subscription(2L, "ACTIVE", LocalDate.now().minusDays(1)));

            // when
            ExpireSubscriptionsResponse response = service.expire();

            // then
            assertThat(response.totalCount()).isZero();
            assertThat(fakeSubscriptionStoragePort.findById(1L).getStatus()).isEqualTo("EXP_PENDING");
            assertThat(fakeSubscriptionStoragePort.findById(2L).getStatus()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("[success] DOWN_PENDING 이면서 만료일이 지난 구독의 status 를 EXPIRED 로 변경한다")
        void success_downPending() {
            // given
            fakeSubscriptionStoragePort.subscriptionDatabase.add(
                subscription(1L, "DOWN_PENDING", LocalDate.now().minusDays(1)));

            // when
            ExpireSubscriptionsResponse response = service.expire();

            // then
            assertThat(response.totalCount()).isEqualTo(1);
            assertThat(response.successCount()).isEqualTo(1);
            assertThat(fakeSubscriptionStoragePort.findById(1L).getStatus()).isEqualTo("EXPIRED");
        }
    }
}
