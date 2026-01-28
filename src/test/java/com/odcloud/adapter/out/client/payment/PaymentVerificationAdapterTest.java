package com.odcloud.adapter.out.client.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.odcloud.application.voucher.port.in.command.CreateVoucherCommand;
import com.odcloud.domain.model.StoreType;
import com.odcloud.domain.model.VoucherType;
import com.odcloud.fakeClass.FakeProfileConstant;
import com.odcloud.infrastructure.constant.ProfileConstant;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentVerificationAdapterTest {

    @Mock
    private AppleVerifyReceiptClient appleClient;

    @Mock
    private GooglePlayVerifyPurchaseClient googlePlayClient;

    private PaymentVerificationAdapter adapter;
    private ProfileConstant profileConstant;

    @BeforeEach
    void setUp() throws Exception {
        profileConstant = FakeProfileConstant.create();
        adapter = new PaymentVerificationAdapter(profileConstant);

        // Mock 클라이언트 주입
        setField(adapter, "appleClient", appleClient);
        setField(adapter, "googlePlayClient", googlePlayClient);
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Nested
    @DisplayName("[verify] Apple 결제 검증")
    class Describe_verifyApple {

        @Test
        @DisplayName("[success] Apple 결제 검증 성공시 true를 반환한다")
        void success_applePaymentVerification() {
            // given
            CreateVoucherCommand command = CreateVoucherCommand.builder()
                .accountId(1L)
                .storeType(StoreType.APPLE)
                .subscriptionKey("valid-receipt-data")
                .orderTxId("APPLE_TX_12345")
                .storeProcessDt(LocalDateTime.now())
                .voucherType(VoucherType.STORAGE_PLUS)
                .groupId(10L)
                .memo("테스트")
                .build();

            AppleVerifyReceiptResponse response = new AppleVerifyReceiptResponse(0, "Sandbox");
            given(appleClient.verifyReceipt(any(AppleVerifyReceiptRequest.class)))
                .willReturn(response);

            // when
            boolean result = adapter.verify(command);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("[error] Apple 결제 검증 실패시 false를 반환한다 (status != 0)")
        void error_applePaymentVerificationFailed() {
            // given
            CreateVoucherCommand command = CreateVoucherCommand.builder()
                .accountId(1L)
                .storeType(StoreType.APPLE)
                .subscriptionKey("invalid-receipt-data")
                .orderTxId("APPLE_TX_INVALID")
                .storeProcessDt(LocalDateTime.now())
                .voucherType(VoucherType.STORAGE_BASIC)
                .groupId(10L)
                .memo("테스트")
                .build();

            AppleVerifyReceiptResponse response = new AppleVerifyReceiptResponse(21002, "Sandbox");
            given(appleClient.verifyReceipt(any(AppleVerifyReceiptRequest.class)))
                .willReturn(response);

            // when
            boolean result = adapter.verify(command);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("[error] Apple API 호출 예외 발생시 false를 반환한다")
        void error_appleApiException() {
            // given
            CreateVoucherCommand command = CreateVoucherCommand.builder()
                .accountId(1L)
                .storeType(StoreType.APPLE)
                .subscriptionKey("receipt-data")
                .orderTxId("APPLE_TX_ERROR")
                .storeProcessDt(LocalDateTime.now())
                .voucherType(VoucherType.ADVERTISE_30)
                .groupId(null)
                .memo("테스트")
                .build();

            given(appleClient.verifyReceipt(any(AppleVerifyReceiptRequest.class)))
                .willThrow(new RuntimeException("Connection timeout"));

            // when
            boolean result = adapter.verify(command);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("[verify] Google Play 결제 검증")
    class Describe_verifyGoogle {

        @Test
        @DisplayName("[success] Google Play 결제 검증 성공시 true를 반환한다")
        void success_googlePlayPaymentVerification() {
            // given
            CreateVoucherCommand command = CreateVoucherCommand.builder()
                .accountId(2L)
                .storeType(StoreType.GOOGLE)
                .subscriptionKey("valid-purchase-token")
                .orderTxId("GOOGLE_TX_12345")
                .storeProcessDt(LocalDateTime.now())
                .voucherType(VoucherType.STORAGE_BASIC)
                .groupId(20L)
                .memo("테스트")
                .build();

            GooglePlayVerifyPurchaseResponse response = new GooglePlayVerifyPurchaseResponse(
                "androidpublisher#productPurchase",
                System.currentTimeMillis(),
                0, // Purchased
                1,
                null,
                "GPA.1234-5678",
                0,
                1,
                "valid-purchase-token",
                "STORAGE_BASIC",
                1,
                null,
                null,
                "KR"
            );
            given(googlePlayClient.verifyPurchase(anyString(), anyString(), anyString()))
                .willReturn(response);

            // when
            boolean result = adapter.verify(command);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("[error] Google Play 결제 검증 실패시 false를 반환한다 (purchaseState != 0)")
        void error_googlePlayPaymentVerificationFailed() {
            // given
            CreateVoucherCommand command = CreateVoucherCommand.builder()
                .accountId(2L)
                .storeType(StoreType.GOOGLE)
                .subscriptionKey("canceled-purchase-token")
                .orderTxId("GOOGLE_TX_CANCELED")
                .storeProcessDt(LocalDateTime.now())
                .voucherType(VoucherType.STORAGE_PLUS)
                .groupId(20L)
                .memo("테스트")
                .build();

            GooglePlayVerifyPurchaseResponse response = new GooglePlayVerifyPurchaseResponse(
                "androidpublisher#productPurchase",
                System.currentTimeMillis(),
                1, // Canceled
                0,
                null,
                "GPA.1234-5678",
                0,
                0,
                "canceled-purchase-token",
                "STORAGE_PLUS",
                1,
                null,
                null,
                "KR"
            );
            given(googlePlayClient.verifyPurchase(anyString(), anyString(), anyString()))
                .willReturn(response);

            // when
            boolean result = adapter.verify(command);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("[error] Google Play 결제가 Pending 상태면 false를 반환한다")
        void error_googlePlayPaymentPending() {
            // given
            CreateVoucherCommand command = CreateVoucherCommand.builder()
                .accountId(2L)
                .storeType(StoreType.GOOGLE)
                .subscriptionKey("pending-purchase-token")
                .orderTxId("GOOGLE_TX_PENDING")
                .storeProcessDt(LocalDateTime.now())
                .voucherType(VoucherType.STORAGE_BASIC)
                .groupId(20L)
                .memo("테스트")
                .build();

            GooglePlayVerifyPurchaseResponse response = new GooglePlayVerifyPurchaseResponse(
                "androidpublisher#productPurchase",
                System.currentTimeMillis(),
                2, // Pending
                0,
                null,
                "GPA.1234-5678",
                0,
                0,
                "pending-purchase-token",
                "STORAGE_BASIC",
                1,
                null,
                null,
                "KR"
            );
            given(googlePlayClient.verifyPurchase(anyString(), anyString(), anyString()))
                .willReturn(response);

            // when
            boolean result = adapter.verify(command);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("[error] Google Play API 호출 예외 발생시 false를 반환한다")
        void error_googlePlayApiException() {
            // given
            CreateVoucherCommand command = CreateVoucherCommand.builder()
                .accountId(2L)
                .storeType(StoreType.GOOGLE)
                .subscriptionKey("purchase-token")
                .orderTxId("GOOGLE_TX_ERROR")
                .storeProcessDt(LocalDateTime.now())
                .voucherType(VoucherType.ADVERTISE_30)
                .groupId(null)
                .memo("테스트")
                .build();

            given(googlePlayClient.verifyPurchase(anyString(), anyString(), anyString()))
                .willThrow(new RuntimeException("API Error"));

            // when
            boolean result = adapter.verify(command);

            // then
            assertThat(result).isFalse();
        }
    }
}
