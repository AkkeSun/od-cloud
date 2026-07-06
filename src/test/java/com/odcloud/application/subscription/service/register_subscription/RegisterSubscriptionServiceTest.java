package com.odcloud.application.subscription.service.register_subscription;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.Group;
import com.odcloud.domain.model.Product;
import com.odcloud.domain.model.Subscription;
import com.odcloud.fakeClass.FakeAccountStoragePort;
import com.odcloud.fakeClass.FakePaymentStoragePort;
import com.odcloud.fakeClass.FakePgClientPort;
import com.odcloud.fakeClass.FakeProductStoragePort;
import com.odcloud.fakeClass.FakeSubscriptionStoragePort;
import com.odcloud.infrastructure.exception.CustomAuthenticationException;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RegisterSubscriptionServiceTest {

    private FakeAccountStoragePort fakeAccountStoragePort;
    private FakeSubscriptionStoragePort fakeSubscriptionStoragePort;
    private FakePaymentStoragePort fakePaymentStoragePort;
    private FakeProductStoragePort fakeProductStoragePort;
    private FakePgClientPort fakePgClientPort;
    private RegisterSubscriptionService service;

    @BeforeEach
    void setUp() {
        fakeAccountStoragePort = new FakeAccountStoragePort();
        fakeSubscriptionStoragePort = new FakeSubscriptionStoragePort();
        fakePaymentStoragePort = new FakePaymentStoragePort();
        fakeProductStoragePort = new FakeProductStoragePort();
        fakePgClientPort = new FakePgClientPort();
        service = new RegisterSubscriptionService(
            fakeAccountStoragePort,
            fakeSubscriptionStoragePort,
            fakePaymentStoragePort,
            fakeProductStoragePort,
            fakePgClientPort
        );
    }

    private Account setUpAccount() {
        Account account = Account.builder()
            .id(1L)
            .email("user@example.com")
            .groups(List.of(Group.builder().id(1L).name("개발팀").build()))
            .build();
        fakeAccountStoragePort.database.add(account);
        return account;
    }

    private void setUpProduct() {
        fakeProductStoragePort.database.add(Product.builder()
            .id(100L)
            .productName("CLOUD_100GB")
            .price(new BigDecimal("9900"))
            .build());
    }

    private RegisterSubscriptionCommand command(Account account) {
        return RegisterSubscriptionCommand.builder()
            .account(account)
            .groupId(1L)
            .productId(100L)
            .billingKey("billing-key-123")
            .build();
    }

    @Nested
    @DisplayName("[register] 구독 등록")
    class Describe_register {

        @Test
        @DisplayName("[success] Subscription 과 Payment 를 등록하고 결과를 반환한다")
        void success() {
            // given
            Account account = setUpAccount();
            setUpProduct();

            // when
            RegisterSubscriptionResponse response = service.register(command(account));

            // then
            assertThat(response.result()).isTrue();
            assertThat(response.subscriptionId()).isNotNull();
            assertThat(response.paymentId()).isNotNull();

            assertThat(fakeSubscriptionStoragePort.subscriptionDatabase).hasSize(1);
            Subscription savedSubscription = fakeSubscriptionStoragePort.subscriptionDatabase.get(0);
            assertThat(savedSubscription.getGroupId()).isEqualTo(1L);
            assertThat(savedSubscription.getProductId()).isEqualTo(100L);
            assertThat(savedSubscription.getBuyerId()).isEqualTo(1L);
            assertThat(savedSubscription.getStatus()).isEqualTo("ACTIVE");
            assertThat(savedSubscription.getBillingKey()).isEqualTo("billing-key-123");
            assertThat(savedSubscription.getNextBillingDate()).isNotNull();
            assertThat(savedSubscription.getExpiredDate())
                .isEqualTo(savedSubscription.getNextBillingDate());
            assertThat(savedSubscription.getExpiredDate())
                .isEqualTo(LocalDate.now().plusMonths(1));

            assertThat(fakePaymentStoragePort.database).hasSize(1);
            assertThat(fakePaymentStoragePort.database.get(0).getAmount())
                .isEqualByComparingTo(new BigDecimal("9900"));
            assertThat(fakePaymentStoragePort.database.get(0).getStatus()).isEqualTo("PAID");
            assertThat(fakePaymentStoragePort.database.get(0).getSubscriptionId())
                .isEqualTo(savedSubscription.getId());
        }

        @Test
        @DisplayName("[error] 등록되지 않은 사용자면 예외가 발생한다")
        void error_notFoundAccount() {
            // given
            Account account = Account.builder()
                .id(1L)
                .email("unknown@example.com")
                .groups(List.of(Group.builder().id(1L).build()))
                .build();
            setUpProduct();

            // when & then
            assertThatThrownBy(() -> service.register(command(account)))
                .isInstanceOf(CustomBusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.Business_NOT_FOUND_ACCOUNT);
        }

        @Test
        @DisplayName("[error] 가입하지 않은 그룹이면 접근 권한 예외가 발생한다")
        void error_groupAccessDenied() {
            // given
            Account account = setUpAccount();
            setUpProduct();
            RegisterSubscriptionCommand command = RegisterSubscriptionCommand.builder()
                .account(account)
                .groupId(999L)
                .productId(100L)
                .billingKey("billing-key-123")
                .build();

            // when & then
            assertThatThrownBy(() -> service.register(command))
                .isInstanceOf(CustomAuthenticationException.class);
        }

        @Test
        @DisplayName("[error] 등록되지 않은 상품이면 예외가 발생한다")
        void error_notFoundProduct() {
            // given
            Account account = setUpAccount();

            // when & then
            assertThatThrownBy(() -> service.register(command(account)))
                .isInstanceOf(CustomBusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.Business_NOT_FOUND_PRODUCT);
        }

        @Test
        @DisplayName("[error] 이미 활성화된 동일 구독이 존재하면 예외가 발생한다")
        void error_alreadyExistsSubscription() {
            // given
            Account account = setUpAccount();
            setUpProduct();
            fakeSubscriptionStoragePort.subscriptionDatabase.add(Subscription.builder()
                .groupId(1L)
                .productId(100L)
                .status("ACTIVE")
                .build());

            // when & then
            assertThatThrownBy(() -> service.register(command(account)))
                .isInstanceOf(CustomBusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.Business_ALREADY_EXISTS_SUBSCRIPTION);
        }

        @Test
        @DisplayName("[error] 빌링키가 유효하지 않으면 예외가 발생한다")
        void error_invalidBillingKey() {
            // given
            Account account = setUpAccount();
            setUpProduct();
            fakePgClientPort.valid = false;

            // when & then
            assertThatThrownBy(() -> service.register(command(account)))
                .isInstanceOf(CustomBusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.Business_INVALID_BILLING_KEY);
        }
    }
}
