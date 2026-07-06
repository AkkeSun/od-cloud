package com.odcloud.application.subscription.service.modify_subscription_plan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.Product;
import com.odcloud.domain.model.Subscription;
import com.odcloud.domain.service.SubscriptionPlanChangeCalculator;
import com.odcloud.fakeClass.FakePaymentStoragePort;
import com.odcloud.fakeClass.FakePgClientPort;
import com.odcloud.fakeClass.FakeProductStoragePort;
import com.odcloud.fakeClass.FakeSubscriptionStoragePort;
import com.odcloud.infrastructure.exception.CustomAuthorizationException;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ModifySubscriptionPlanServiceTest {

    private static final Long BUYER_ID = 10L;
    private static final Long GROUP_ID = 1L;
    private static final Long CURRENT_PRODUCT_ID = 100L;
    private static final Long NEW_PRODUCT_ID = 200L;

    private FakeSubscriptionStoragePort fakeSubscriptionStoragePort;
    private FakeProductStoragePort fakeProductStoragePort;
    private FakePaymentStoragePort fakePaymentStoragePort;
    private FakePgClientPort fakePgClientPort;
    private ModifySubscriptionPlanService service;

    private final SubscriptionPlanChangeCalculator calculator = new SubscriptionPlanChangeCalculator();

    @BeforeEach
    void setUp() {
        fakeSubscriptionStoragePort = new FakeSubscriptionStoragePort();
        fakeProductStoragePort = new FakeProductStoragePort();
        fakePaymentStoragePort = new FakePaymentStoragePort();
        fakePgClientPort = new FakePgClientPort();
        service = new ModifySubscriptionPlanService(
            fakeSubscriptionStoragePort, fakeProductStoragePort, fakePaymentStoragePort, fakePgClientPort);
    }

    private Subscription setUpActiveSubscription(BigDecimal currentPrice, LocalDate expiredDate) {
        fakeProductStoragePort.database.add(Product.builder()
            .id(CURRENT_PRODUCT_ID)
            .productName("현재 상품")
            .price(currentPrice)
            .build());

        return fakeSubscriptionStoragePort.save(Subscription.builder()
            .productId(CURRENT_PRODUCT_ID)
            .groupId(GROUP_ID)
            .buyerId(BUYER_ID)
            .status("ACTIVE")
            .billingKey("billing-key-123")
            .nextBillingDate(expiredDate)
            .expiredDate(expiredDate)
            .build());
    }

    private void setUpNewProduct(BigDecimal newPrice) {
        fakeProductStoragePort.database.add(Product.builder()
            .id(NEW_PRODUCT_ID)
            .productName("신규 상품")
            .price(newPrice)
            .build());
    }

    private ModifySubscriptionPlanCommand command(Long subscriptionId, Long newProductId, Account account) {
        return ModifySubscriptionPlanCommand.builder()
            .account(account)
            .currentSubscriptionId(subscriptionId)
            .newProductId(newProductId)
            .build();
    }

    @Nested
    @DisplayName("[modify] 구독 플랜을 변경하는 메소드")
    class Describe_modify {

        @Test
        @DisplayName("[success] 신규 상품가가 더 비싸면 업그레이드 처리로 즉시 전환하고 차액을 결제한다")
        void success_upgrade() {
            // given
            LocalDate expiredDate = LocalDate.now().plusDays(15);
            BigDecimal currentPrice = BigDecimal.valueOf(10000);
            BigDecimal newPrice = BigDecimal.valueOf(30000);
            Subscription subscription = setUpActiveSubscription(currentPrice, expiredDate);
            setUpNewProduct(newPrice);
            Account account = Account.builder().id(BUYER_ID).build();

            BigDecimal expectedRemainingValue =
                calculator.calculateRemainingValue(currentPrice, subscription, LocalDate.now());
            BigDecimal expectedChargeAmount =
                calculator.calculateUpgradeChargeAmount(newPrice, expectedRemainingValue);

            // when
            ModifySubscriptionPlanResponse response =
                service.modify(command(subscription.getId(), NEW_PRODUCT_ID, account));

            // then
            assertThat(response.result()).isTrue();
            assertThat(response.changeType()).isEqualTo("UPGRADE");
            assertThat(response.previousSubscriptionId()).isEqualTo(subscription.getId());
            assertThat(response.chargedAmount()).isEqualByComparingTo(expectedChargeAmount);
            assertThat(response.paymentId()).isNotNull();

            Subscription oldSubscription = fakeSubscriptionStoragePort.subscriptionDatabase.stream()
                .filter(s -> s.getId().equals(subscription.getId()))
                .findFirst().orElseThrow();
            assertThat(oldSubscription.getStatus()).isEqualTo("EXPIRED");
            assertThat(oldSubscription.getExpiredDate()).isEqualTo(LocalDate.now());

            Subscription newSubscription = fakeSubscriptionStoragePort.subscriptionDatabase.stream()
                .filter(s -> s.getId().equals(response.newSubscriptionId()))
                .findFirst().orElseThrow();
            assertThat(newSubscription.getStatus()).isEqualTo("ACTIVE");
            assertThat(newSubscription.getProductId()).isEqualTo(NEW_PRODUCT_ID);
            assertThat(newSubscription.getBillingKey()).isEqualTo(subscription.getBillingKey());
            assertThat(newSubscription.getExpiredDate()).isEqualTo(LocalDate.now().plusMonths(1));

            assertThat(fakePaymentStoragePort.database).hasSize(1);
            assertThat(fakePaymentStoragePort.database.get(0).getAmount())
                .isEqualByComparingTo(expectedChargeAmount);
        }

        @Test
        @DisplayName("[success] 신규 상품가가 더 저렴하면 다운그레이드 처리로 해지 예약과 신규 구독 예약이 이루어진다")
        void success_downgrade() {
            // given
            LocalDate expiredDate = LocalDate.now().plusDays(15);
            Subscription subscription = setUpActiveSubscription(BigDecimal.valueOf(30000), expiredDate);
            setUpNewProduct(BigDecimal.valueOf(10000));
            Account account = Account.builder().id(BUYER_ID).build();

            // when
            ModifySubscriptionPlanResponse response =
                service.modify(command(subscription.getId(), NEW_PRODUCT_ID, account));

            // then
            assertThat(response.result()).isTrue();
            assertThat(response.changeType()).isEqualTo("DOWNGRADE");
            assertThat(response.paymentId()).isNull();
            assertThat(response.chargedAmount()).isEqualByComparingTo(BigDecimal.ZERO);

            Subscription oldSubscription = fakeSubscriptionStoragePort.subscriptionDatabase.stream()
                .filter(s -> s.getId().equals(subscription.getId()))
                .findFirst().orElseThrow();
            assertThat(oldSubscription.getStatus()).isEqualTo("DOWN_PENDING");
            assertThat(oldSubscription.getExpiredDate()).isEqualTo(expiredDate);

            Subscription newSubscription = fakeSubscriptionStoragePort.subscriptionDatabase.stream()
                .filter(s -> s.getId().equals(response.newSubscriptionId()))
                .findFirst().orElseThrow();
            assertThat(newSubscription.getStatus()).isEqualTo("PENDING");
            assertThat(newSubscription.getProductId()).isEqualTo(NEW_PRODUCT_ID);
            assertThat(newSubscription.getBillingKey()).isEqualTo(subscription.getBillingKey());
            assertThat(newSubscription.getExpiredDate()).isEqualTo(expiredDate);
            assertThat(newSubscription.getNextBillingDate()).isEqualTo(expiredDate);

            assertThat(fakePaymentStoragePort.database).isEmpty();
        }

        @Test
        @DisplayName("[error] 존재하지 않는 구독이면 예외가 발생한다")
        void error_notFoundSubscription() {
            // given
            Account account = Account.builder().id(BUYER_ID).build();

            // when & then
            assertThatThrownBy(() -> service.modify(command(999L, NEW_PRODUCT_ID, account)))
                .isInstanceOf(CustomBusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.Business_NOT_FOUND_SUBSCRIPTION);
        }

        @Test
        @DisplayName("[error] 구독의 buyerId와 로그인 사용자가 다르면 접근 권한 예외가 발생한다")
        void error_accessDenied() {
            // given
            Subscription subscription =
                setUpActiveSubscription(BigDecimal.valueOf(10000), LocalDate.now().plusDays(15));
            setUpNewProduct(BigDecimal.valueOf(30000));
            Account other = Account.builder().id(99L).build();

            // when & then
            assertThatThrownBy(() -> service.modify(command(subscription.getId(), NEW_PRODUCT_ID, other)))
                .isInstanceOf(CustomAuthorizationException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.ACCESS_DENIED);
        }

        @Test
        @DisplayName("[error] 구독 상태가 ACTIVE가 아니면 예외가 발생한다")
        void error_invalidStatus() {
            // given
            fakeProductStoragePort.database.add(Product.builder()
                .id(CURRENT_PRODUCT_ID)
                .price(BigDecimal.valueOf(10000))
                .build());
            Subscription subscription = Subscription.builder()
                .id(1L)
                .productId(CURRENT_PRODUCT_ID)
                .groupId(GROUP_ID)
                .buyerId(BUYER_ID)
                .status("EXP_PENDING")
                .expiredDate(LocalDate.now().plusDays(15))
                .build();
            fakeSubscriptionStoragePort.subscriptionDatabase.add(subscription);
            setUpNewProduct(BigDecimal.valueOf(30000));
            Account account = Account.builder().id(BUYER_ID).build();

            // when & then
            assertThatThrownBy(() -> service.modify(command(subscription.getId(), NEW_PRODUCT_ID, account)))
                .isInstanceOf(CustomBusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.Business_INVALID_SUBSCRIPTION_STATUS_FOR_MODIFY);
        }

        @Test
        @DisplayName("[error] 신규 상품이 존재하지 않으면 예외가 발생한다")
        void error_notFoundProduct() {
            // given
            Subscription subscription =
                setUpActiveSubscription(BigDecimal.valueOf(10000), LocalDate.now().plusDays(15));
            Account account = Account.builder().id(BUYER_ID).build();

            // when & then
            assertThatThrownBy(() -> service.modify(command(subscription.getId(), 999L, account)))
                .isInstanceOf(CustomBusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.Business_NOT_FOUND_PRODUCT);
        }

        @Test
        @DisplayName("[error] 신규 상품가가 기존 상품가와 동일하면 예외가 발생한다")
        void error_samePrice() {
            // given
            Subscription subscription =
                setUpActiveSubscription(BigDecimal.valueOf(10000), LocalDate.now().plusDays(15));
            setUpNewProduct(BigDecimal.valueOf(10000));
            Account account = Account.builder().id(BUYER_ID).build();

            // when & then
            assertThatThrownBy(() -> service.modify(command(subscription.getId(), NEW_PRODUCT_ID, account)))
                .isInstanceOf(CustomBusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.Business_INVALID_PLAN_CHANGE);
        }

        @Test
        @DisplayName("[error] 업그레이드 차액 결제에 실패하면 예외가 발생하고 구독 상태는 변경되지 않는다")
        void error_paymentFailed() {
            // given
            LocalDate expiredDate = LocalDate.now().plusDays(15);
            Subscription subscription =
                setUpActiveSubscription(BigDecimal.valueOf(10000), expiredDate);
            setUpNewProduct(BigDecimal.valueOf(30000));
            Account account = Account.builder().id(BUYER_ID).build();
            fakePgClientPort.paid = false;

            // when & then
            assertThatThrownBy(() -> service.modify(command(subscription.getId(), NEW_PRODUCT_ID, account)))
                .isInstanceOf(CustomBusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.Business_PLAN_CHANGE_PAYMENT_FAILED);

            assertThat(fakeSubscriptionStoragePort.subscriptionDatabase).hasSize(1);
            Subscription unchanged = fakeSubscriptionStoragePort.subscriptionDatabase.get(0);
            assertThat(unchanged.getStatus()).isEqualTo("ACTIVE");
            assertThat(unchanged.getExpiredDate()).isEqualTo(expiredDate);
            assertThat(fakePaymentStoragePort.database).isEmpty();
        }

        @Test
        @DisplayName("[error] 신규 상품에 대해 그룹 내 이미 활성화된 구독이 있으면 예외가 발생한다")
        void error_alreadyExistsSubscriptionForNewProduct() {
            // given: 그룹이 이미 신규 상품(productId=NEW_PRODUCT_ID)에 대한 ACTIVE 구독을 보유
            Subscription subscription =
                setUpActiveSubscription(BigDecimal.valueOf(10000), LocalDate.now().plusDays(15));
            setUpNewProduct(BigDecimal.valueOf(30000));
            fakeSubscriptionStoragePort.save(Subscription.builder()
                .productId(NEW_PRODUCT_ID)
                .groupId(GROUP_ID)
                .buyerId(BUYER_ID)
                .status("ACTIVE")
                .billingKey("other-billing-key")
                .nextBillingDate(LocalDate.now().plusDays(20))
                .expiredDate(LocalDate.now().plusDays(20))
                .build());
            Account account = Account.builder().id(BUYER_ID).build();

            // when & then
            assertThatThrownBy(() -> service.modify(command(subscription.getId(), NEW_PRODUCT_ID, account)))
                .isInstanceOf(CustomBusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.Business_ALREADY_EXISTS_SUBSCRIPTION);
        }

        // 동시성 방어(비관적 락)는 FakeClass로는 실제 DB 락 동작을 검증할 수 없으므로
        // SubscriptionRepositoryTest(H2 통합 테스트)에서 findByIdForUpdate의 실제 락 동작을 검증한다.
    }
}
