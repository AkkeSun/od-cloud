package com.odcloud.application.webhook.service.googleplay;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.odcloud.application.webhook.port.in.command.GooglePlayNotificationCommand;
import com.odcloud.domain.model.GooglePlayNotificationType;
import com.odcloud.domain.model.Group;
import com.odcloud.domain.model.Payment;
import com.odcloud.domain.model.PaymentStatus;
import com.odcloud.domain.model.StoreType;
import com.odcloud.domain.model.Voucher;
import com.odcloud.domain.model.VoucherStatus;
import com.odcloud.domain.model.VoucherType;
import com.odcloud.fakeClass.FakeGroupStoragePort;
import com.odcloud.fakeClass.FakePaymentStoragePort;
import com.odcloud.fakeClass.FakeVoucherStoragePort;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class HandleGooglePlayWebhookServiceTest {

    private HandleGooglePlayWebhookService service;
    private FakePaymentStoragePort paymentStoragePort;
    private FakeVoucherStoragePort voucherStoragePort;
    private FakeGroupStoragePort groupStoragePort;

    @BeforeEach
    void setUp() {
        paymentStoragePort = new FakePaymentStoragePort();
        voucherStoragePort = new FakeVoucherStoragePort();
        groupStoragePort = new FakeGroupStoragePort();
        service = new HandleGooglePlayWebhookService(
            paymentStoragePort,
            voucherStoragePort,
            groupStoragePort
        );
    }

    @Nested
    @DisplayName("[handle] Google Play 웹훅을 처리하는 메소드")
    class Describe_handle {

        @Nested
        @DisplayName("테스트 알림인 경우")
        class Context_testNotification {

            @Test
            @DisplayName("[success] 아무 작업 없이 정상 종료한다")
            void success() {
                // given
                GooglePlayNotificationCommand command = new GooglePlayNotificationCommand(
                    "com.odcloud.app",
                    System.currentTimeMillis(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    true
                );

                // when
                service.handle(command);

                // then
                assertThat(paymentStoragePort.database).isEmpty();
                assertThat(voucherStoragePort.database).isEmpty();
            }
        }

        @Nested
        @DisplayName("구독 갱신 알림인 경우")
        class Context_renewalNotification {

            @Test
            @DisplayName("[success] 구독을 갱신하고 새 결제를 생성한다")
            void success_renewed() {
                // given
                String purchaseToken = "test_purchase_token";
                Payment existingPayment = createPayment(1L, purchaseToken, "original_order");
                paymentStoragePort.save(existingPayment);

                Voucher voucher = createVoucher(1L, 1L, VoucherType.STORAGE_BASIC, 100L);
                voucherStoragePort.save(voucher);

                GooglePlayNotificationCommand command = new GooglePlayNotificationCommand(
                    "com.odcloud.app",
                    System.currentTimeMillis(),
                    GooglePlayNotificationType.SUBSCRIPTION_RENEWED,
                    purchaseToken,
                    "new_subscription_id",
                    null,
                    null,
                    false
                );

                // when
                service.handle(command);

                // then
                assertThat(paymentStoragePort.database).hasSize(2);
                Payment renewalPayment = paymentStoragePort.database.stream()
                    .filter(p -> "new_subscription_id".equals(p.getOrderTxId()))
                    .findFirst()
                    .orElseThrow();
                assertThat(renewalPayment.getSubscriptionKey()).isEqualTo(purchaseToken);
                assertThat(renewalPayment.getStatus()).isEqualTo(PaymentStatus.PAID);

                Voucher updatedVoucher = voucherStoragePort.database.get(0);
                assertThat(updatedVoucher.getPaymentId()).isEqualTo(renewalPayment.getId());
            }

            @Test
            @DisplayName("[success] SUBSCRIPTION_RECOVERED 타입도 갱신으로 처리한다")
            void success_recovered() {
                // given
                String purchaseToken = "recovered_token";
                Payment existingPayment = createPayment(1L, purchaseToken, "original_order");
                paymentStoragePort.save(existingPayment);

                Voucher voucher = createVoucher(1L, 1L, VoucherType.STORAGE_PLUS, 100L);
                voucherStoragePort.save(voucher);

                GooglePlayNotificationCommand command = new GooglePlayNotificationCommand(
                    "com.odcloud.app",
                    System.currentTimeMillis(),
                    GooglePlayNotificationType.SUBSCRIPTION_RECOVERED,
                    purchaseToken,
                    "recovered_subscription_id",
                    null,
                    null,
                    false
                );

                // when
                service.handle(command);

                // then
                assertThat(paymentStoragePort.database).hasSize(2);
            }

            @Test
            @DisplayName("[success] SUBSCRIPTION_RESTARTED 타입도 갱신으로 처리한다")
            void success_restarted() {
                // given
                String purchaseToken = "restarted_token";
                Payment existingPayment = createPayment(1L, purchaseToken, "original_order");
                paymentStoragePort.save(existingPayment);

                Voucher voucher = createVoucher(1L, 1L, VoucherType.STORAGE_BASIC, 100L);
                voucherStoragePort.save(voucher);

                GooglePlayNotificationCommand command = new GooglePlayNotificationCommand(
                    "com.odcloud.app",
                    System.currentTimeMillis(),
                    GooglePlayNotificationType.SUBSCRIPTION_RESTARTED,
                    purchaseToken,
                    "restarted_subscription_id",
                    null,
                    null,
                    false
                );

                // when
                service.handle(command);

                // then
                assertThat(paymentStoragePort.database).hasSize(2);
            }

            @Test
            @DisplayName("[error] 결제 정보를 찾을 수 없으면 예외를 발생시킨다")
            void error_paymentNotFound() {
                // given
                GooglePlayNotificationCommand command = new GooglePlayNotificationCommand(
                    "com.odcloud.app",
                    System.currentTimeMillis(),
                    GooglePlayNotificationType.SUBSCRIPTION_RENEWED,
                    "non_existent_token",
                    "new_subscription_id",
                    null,
                    null,
                    false
                );

                // when & then
                assertThatThrownBy(() -> service.handle(command))
                    .isInstanceOf(CustomBusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.Business_NOT_FOUND_PAYMENT);
            }
        }

        @Nested
        @DisplayName("환불 알림인 경우")
        class Context_refundNotification {

            @Test
            @DisplayName("[success] purchaseToken으로 결제를 찾아 환불 처리한다")
            void success_refundByPurchaseToken() {
                // given
                String purchaseToken = "refund_token";
                Payment payment = createPayment(1L, purchaseToken, "order_123");
                paymentStoragePort.save(payment);

                Voucher voucher = createVoucher(1L, 1L, VoucherType.ADVERTISE_30, 100L);
                voucherStoragePort.save(voucher);

                GooglePlayNotificationCommand command = new GooglePlayNotificationCommand(
                    "com.odcloud.app",
                    System.currentTimeMillis(),
                    GooglePlayNotificationType.SUBSCRIPTION_REVOKED,
                    purchaseToken,
                    null,
                    null,
                    1,
                    false
                );

                // when
                service.handle(command);

                // then
                Payment refundedPayment = paymentStoragePort.database.get(1);
                assertThat(refundedPayment.getStatus()).isEqualTo(PaymentStatus.REFUNDED);

                Voucher revokedVoucher = voucherStoragePort.database.get(0);
                assertThat(revokedVoucher.getStatus()).isEqualTo(VoucherStatus.REVOKED);
            }

            @Test
            @DisplayName("[success] purchaseToken이 없으면 orderId로 결제를 찾아 환불 처리한다")
            void success_refundByOrderId() {
                // given
                String orderId = "order_456";
                Payment payment = createPayment(1L, null, orderId);
                paymentStoragePort.save(payment);

                Voucher voucher = createVoucher(1L, 1L, VoucherType.ADVERTISE_90, 100L);
                voucherStoragePort.save(voucher);

                GooglePlayNotificationCommand command = new GooglePlayNotificationCommand(
                    "com.odcloud.app",
                    System.currentTimeMillis(),
                    GooglePlayNotificationType.SUBSCRIPTION_REVOKED,
                    "unknown_token",
                    null,
                    orderId,
                    1,
                    false
                );

                // when
                service.handle(command);

                // then
                Payment refundedPayment = paymentStoragePort.database.get(1);
                assertThat(refundedPayment.getStatus()).isEqualTo(PaymentStatus.REFUNDED);
            }

            @Test
            @DisplayName("[success] 스토리지 바우처 환불 시 그룹 스토리지를 감소시킨다")
            void success_refundStorageVoucher() {
                // given
                Long groupId = 100L;
                Group group = Group.builder()
                    .id(groupId)
                    .name("Test Group")
                    .ownerEmail("owner@test.com")
                    .storageUsed(50L * 1024 * 1024 * 1024)
                    .storageTotal(103221225472L + VoucherType.STORAGE_BASIC.getStorageIncreaseSize())
                    .regDt(LocalDateTime.now())
                    .build();
                groupStoragePort.save(group);

                String purchaseToken = "storage_refund_token";
                Payment payment = createPayment(1L, purchaseToken, "storage_order");
                paymentStoragePort.save(payment);

                Voucher voucher = createVoucher(1L, 1L, VoucherType.STORAGE_BASIC, groupId);
                voucherStoragePort.save(voucher);

                long originalStorageTotal = group.getStorageTotal();

                GooglePlayNotificationCommand command = new GooglePlayNotificationCommand(
                    "com.odcloud.app",
                    System.currentTimeMillis(),
                    GooglePlayNotificationType.SUBSCRIPTION_REVOKED,
                    purchaseToken,
                    null,
                    null,
                    1,
                    false
                );

                // when
                service.handle(command);

                // then
                Group updatedGroup = groupStoragePort.findById(groupId);
                assertThat(updatedGroup.getStorageTotal())
                    .isLessThan(originalStorageTotal);
            }

            @Test
            @DisplayName("[success] 광고 제거 바우처 환불 시 그룹 스토리지는 변경하지 않는다")
            void success_refundAdvertiseVoucher_noStorageChange() {
                // given
                Long groupId = 200L;
                Group group = Group.builder()
                    .id(groupId)
                    .name("Advertise Group")
                    .ownerEmail("ad@test.com")
                    .storageUsed(0L)
                    .storageTotal(3221225472L)
                    .regDt(LocalDateTime.now())
                    .build();
                groupStoragePort.save(group);

                String purchaseToken = "advertise_refund_token";
                Payment payment = createPayment(1L, purchaseToken, "advertise_order");
                paymentStoragePort.save(payment);

                Voucher voucher = createVoucher(1L, 1L, VoucherType.ADVERTISE_30, groupId);
                voucherStoragePort.save(voucher);

                long originalStorageTotal = group.getStorageTotal();

                GooglePlayNotificationCommand command = new GooglePlayNotificationCommand(
                    "com.odcloud.app",
                    System.currentTimeMillis(),
                    GooglePlayNotificationType.SUBSCRIPTION_REVOKED,
                    purchaseToken,
                    null,
                    null,
                    1,
                    false
                );

                // when
                service.handle(command);

                // then
                Group updatedGroup = groupStoragePort.findById(groupId);
                assertThat(updatedGroup.getStorageTotal()).isEqualTo(originalStorageTotal);
            }

            @Test
            @DisplayName("[error] 결제 정보를 찾을 수 없으면 예외를 발생시킨다")
            void error_paymentNotFound() {
                // given
                GooglePlayNotificationCommand command = new GooglePlayNotificationCommand(
                    "com.odcloud.app",
                    System.currentTimeMillis(),
                    GooglePlayNotificationType.SUBSCRIPTION_REVOKED,
                    "non_existent_token",
                    null,
                    "non_existent_order",
                    1,
                    false
                );

                // when & then
                assertThatThrownBy(() -> service.handle(command))
                    .isInstanceOf(CustomBusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.Business_NOT_FOUND_PAYMENT);
            }
        }

        @Nested
        @DisplayName("처리 대상이 아닌 알림인 경우")
        class Context_otherNotification {

            @Test
            @DisplayName("[success] SUBSCRIPTION_CANCELED는 아무 작업 없이 종료한다")
            void success_canceled() {
                // given
                GooglePlayNotificationCommand command = new GooglePlayNotificationCommand(
                    "com.odcloud.app",
                    System.currentTimeMillis(),
                    GooglePlayNotificationType.SUBSCRIPTION_CANCELED,
                    "some_token",
                    null,
                    null,
                    null,
                    false
                );

                // when
                service.handle(command);

                // then
                assertThat(paymentStoragePort.database).isEmpty();
            }

            @Test
            @DisplayName("[success] SUBSCRIPTION_PURCHASED는 아무 작업 없이 종료한다")
            void success_purchased() {
                // given
                GooglePlayNotificationCommand command = new GooglePlayNotificationCommand(
                    "com.odcloud.app",
                    System.currentTimeMillis(),
                    GooglePlayNotificationType.SUBSCRIPTION_PURCHASED,
                    "new_purchase_token",
                    "subscription_id",
                    null,
                    null,
                    false
                );

                // when
                service.handle(command);

                // then
                assertThat(paymentStoragePort.database).isEmpty();
            }
        }
    }

    private Payment createPayment(Long id, String subscriptionKey, String orderTxId) {
        return Payment.builder()
            .id(id)
            .accountId(1L)
            .storeType(StoreType.GOOGLE)
            .subscriptionKey(subscriptionKey)
            .orderTxId(orderTxId)
            .status(PaymentStatus.PAID)
            .storeProcessDt(LocalDateTime.now())
            .regDt(LocalDateTime.now())
            .build();
    }

    private Voucher createVoucher(Long id, Long paymentId, VoucherType voucherType, Long groupId) {
        return Voucher.builder()
            .id(id)
            .paymentId(paymentId)
            .voucherType(voucherType)
            .status(VoucherStatus.ACTIVE)
            .accountId(1L)
            .groupId(groupId)
            .startAt(LocalDateTime.now())
            .endDt(LocalDateTime.now().plusDays(30))
            .regDt(LocalDateTime.now())
            .build();
    }
}