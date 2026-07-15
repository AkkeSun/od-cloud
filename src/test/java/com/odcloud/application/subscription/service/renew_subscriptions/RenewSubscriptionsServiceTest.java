package com.odcloud.application.subscription.service.renew_subscriptions;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.domain.model.Group;
import com.odcloud.domain.model.Product;
import com.odcloud.domain.model.Subscription;
import com.odcloud.fakeClass.FakeGroupStoragePort;
import com.odcloud.fakeClass.FakePaymentStoragePort;
import com.odcloud.fakeClass.FakePgClientPort;
import com.odcloud.fakeClass.FakeProductStoragePort;
import com.odcloud.fakeClass.FakeRedisStoragePort;
import com.odcloud.fakeClass.FakeSubscriptionStoragePort;
import com.odcloud.infrastructure.constant.CommonConstant;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RenewSubscriptionsServiceTest {

    private FakeSubscriptionStoragePort fakeSubscriptionStoragePort;
    private FakePaymentStoragePort fakePaymentStoragePort;
    private FakeProductStoragePort fakeProductStoragePort;
    private FakePgClientPort fakePgClientPort;
    private FakeGroupStoragePort fakeGroupStoragePort;
    private FakeRedisStoragePort fakeRedisStoragePort;
    private RenewSubscriptionsService service;

    @BeforeEach
    void setUp() {
        fakeSubscriptionStoragePort = new FakeSubscriptionStoragePort();
        fakePaymentStoragePort = new FakePaymentStoragePort();
        fakeProductStoragePort = new FakeProductStoragePort();
        fakePgClientPort = new FakePgClientPort();
        fakeGroupStoragePort = new FakeGroupStoragePort();
        fakeRedisStoragePort = new FakeRedisStoragePort();
        service = new RenewSubscriptionsService(
            fakeSubscriptionStoragePort,
            fakePaymentStoragePort,
            fakeProductStoragePort,
            fakePgClientPort,
            fakeGroupStoragePort,
            fakeRedisStoragePort
        );
        fakeProductStoragePort.database.add(Product.builder()
            .id(100L)
            .productName("CLOUD_100GB")
            .price(new BigDecimal("9900"))
            .build());
        fakeGroupStoragePort.groupDatabase.add(Group.builder()
            .id(1L)
            .name("그룹")
            .ownerEmail("owner@example.com")
            .storageUsed(0L)
            .storageTotal(CommonConstant.DEFAULT_STORAGE_TOTAL)
            .backupYn("N")
            .build());
    }

    private Subscription activeSubscription(Long id, LocalDate nextBillingDate) {
        return subscription(id, "ACTIVE", nextBillingDate);
    }

    private Subscription subscription(Long id, String status, LocalDate nextBillingDate) {
        return Subscription.builder()
            .id(id)
            .productId(100L)
            .groupId(1L)
            .buyerId(1L)
            .status(status)
            .billingKey("billing-key-" + id)
            .nextBillingDate(nextBillingDate)
            .expiredDate(nextBillingDate)
            .build();
    }

    @Nested
    @DisplayName("[renew] 구독 갱신 결제")
    class Describe_renew {

        @Test
        @DisplayName("[success] 결제 후 Payment 저장 및 Subscription 의 next_billing_date 를 갱신한다")
        void success() {
            // given
            LocalDate dueDate = LocalDate.now();
            fakeSubscriptionStoragePort.subscriptionDatabase.add(activeSubscription(1L, dueDate));

            // when
            RenewSubscriptionsResponse response = service.renew();

            // then
            assertThat(response.totalCount()).isEqualTo(1);
            assertThat(response.successCount()).isEqualTo(1);
            assertThat(response.failCount()).isZero();

            assertThat(fakePaymentStoragePort.database).hasSize(1);
            assertThat(fakePaymentStoragePort.database.get(0).getAmount())
                .isEqualByComparingTo(new BigDecimal("9900"));
            assertThat(fakePaymentStoragePort.database.get(0).getStatus()).isEqualTo("PAID");
            assertThat(fakePaymentStoragePort.database.get(0).getSubscriptionId()).isEqualTo(1L);

            Subscription updated = fakeSubscriptionStoragePort.findById(1L);
            assertThat(updated.getStatus()).isEqualTo("ACTIVE");
            assertThat(updated.getNextBillingDate()).isEqualTo(dueDate.plusMonths(1));
            assertThat(updated.getExpiredDate()).isEqualTo(dueDate.plusMonths(1));
        }

        @Test
        @DisplayName("[skip] next_billing_date 가 미래이거나 ACTIVE/PENDING 이 아니면 갱신 대상이 아니다")
        void skip_notDue() {
            // given
            fakeSubscriptionStoragePort.subscriptionDatabase.add(
                activeSubscription(1L, LocalDate.now().plusDays(3)));
            Subscription expired = activeSubscription(2L, LocalDate.now());
            expired.expire();
            fakeSubscriptionStoragePort.subscriptionDatabase.add(expired);

            // when
            RenewSubscriptionsResponse response = service.renew();

            // then
            assertThat(response.totalCount()).isZero();
            assertThat(fakePaymentStoragePort.database).isEmpty();
        }

        @Test
        @DisplayName("[success] PENDING 상태의 구독은 결제 성공 시 ACTIVE 로 활성화된다")
        void success_activatesPendingStatus() {
            // given
            LocalDate dueDate = LocalDate.now();
            fakeSubscriptionStoragePort.subscriptionDatabase.add(
                subscription(1L, "PENDING", dueDate));

            // when
            RenewSubscriptionsResponse response = service.renew();

            // then
            assertThat(response.totalCount()).isEqualTo(1);
            assertThat(response.successCount()).isEqualTo(1);
            assertThat(fakePaymentStoragePort.database).hasSize(1);

            Subscription updated = fakeSubscriptionStoragePort.findById(1L);
            assertThat(updated.getStatus()).isEqualTo("ACTIVE");
            assertThat(updated.getNextBillingDate()).isEqualTo(dueDate.plusMonths(1));
            assertThat(updated.getExpiredDate()).isEqualTo(dueDate.plusMonths(1));
        }

        @Test
        @DisplayName("[success] PENDING(다운그레이드 예약) 구독 활성화 시 그룹의 storageTotal 을 새 상품 기준으로 재계산한다")
        void success_appliesGroupBenefitOnPendingActivation() {
            // given - 100GB 스토리지 상품(productId=3) 의 PENDING 구독이 활성화되는 상황
            fakeProductStoragePort.database.add(Product.builder()
                .id(CommonConstant.STORAGE_100GB_PRODUCT_ID)
                .productName("STORAGE_100GB")
                .price(new BigDecimal("9900"))
                .build());
            LocalDate dueDate = LocalDate.now();
            fakeSubscriptionStoragePort.subscriptionDatabase.add(Subscription.builder()
                .id(1L)
                .productId(CommonConstant.STORAGE_100GB_PRODUCT_ID)
                .groupId(1L)
                .buyerId(1L)
                .status("PENDING")
                .billingKey("billing-key-1")
                .nextBillingDate(dueDate)
                .expiredDate(dueDate)
                .build());

            // when
            RenewSubscriptionsResponse response = service.renew();

            // then
            assertThat(response.successCount()).isEqualTo(1);
            Group group = fakeGroupStoragePort.findById(1L);
            assertThat(group.getStorageTotal()).isEqualTo(CommonConstant.STORAGE_100GB);
        }

        @Test
        @DisplayName("[success] ACTIVE 구독의 정기 갱신은 그룹 혜택을 변경하지 않는다")
        void success_activeRenewalDoesNotTouchGroup() {
            // given
            LocalDate dueDate = LocalDate.now();
            fakeSubscriptionStoragePort.subscriptionDatabase.add(activeSubscription(1L, dueDate));

            // when
            service.renew();

            // then
            Group group = fakeGroupStoragePort.findById(1L);
            assertThat(group.getStorageTotal()).isEqualTo(CommonConstant.DEFAULT_STORAGE_TOTAL);
            assertThat(group.getBackupYn()).isEqualTo("N");
        }

        @Test
        @DisplayName("[fail] 결제에 실패하면 Payment 저장 및 갱신을 하지 않는다")
        void fail_payment() {
            // given
            LocalDate dueDate = LocalDate.now();
            fakeSubscriptionStoragePort.subscriptionDatabase.add(activeSubscription(1L, dueDate));
            fakePgClientPort.paid = false;

            // when
            RenewSubscriptionsResponse response = service.renew();

            // then
            assertThat(response.totalCount()).isEqualTo(1);
            assertThat(response.successCount()).isZero();
            assertThat(response.failCount()).isEqualTo(1);

            assertThat(fakePaymentStoragePort.database).isEmpty();
            assertThat(fakeSubscriptionStoragePort.findById(1L).getNextBillingDate())
                .isEqualTo(dueDate);
        }
    }
}
