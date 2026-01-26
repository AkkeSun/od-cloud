package com.odcloud.adapter.out.client.payment;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.application.voucher.port.in.command.CreateVoucherCommand;
import com.odcloud.domain.model.StoreType;
import com.odcloud.domain.model.VoucherType;
import com.odcloud.infrastructure.constant.ProfileConstant;
import com.odcloud.infrastructure.constant.ProfileConstant.ApplePayment;
import com.odcloud.infrastructure.constant.ProfileConstant.GooglePlayPayment;
import java.io.IOException;
import java.time.LocalDateTime;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class PaymentVerificationAdapterTest {

    private PaymentVerificationAdapter adapter;
    private MockWebServer appleServer;
    private MockWebServer googlePlayServer;

    @BeforeEach
    void setUp() throws IOException {
        appleServer = new MockWebServer();
        appleServer.start();

        googlePlayServer = new MockWebServer();
        googlePlayServer.start();

        adapter = new PaymentVerificationAdapter(ProfileConstant.builder()
            .applePayment(ApplePayment.builder()
                .verifyReceiptApi(appleServer.url("/").toString())
                .password("test-shared-secret")
                .build())
            .googlePlayPayment(GooglePlayPayment.builder()
                .verifyPurchaseApi(googlePlayServer.url("/").toString())
                .packageName("com.test.app")
                .build())
            .build());
    }

    @AfterEach
    void tearDown() throws IOException {
        appleServer.shutdown();
        googlePlayServer.shutdown();
    }

    @Nested
    @DisplayName("[Apple] Apple 결제 검증")
    class Describe_applePaymentVerification {

        @Test
        @DisplayName("[success] Apple 결제 검증 성공")
        void success() {
            // given
            CreateVoucherCommand command = CreateVoucherCommand.builder()
                .accountId(1L)
                .storeType(StoreType.APPLE)
                .subscriptionKey("test-receipt-data")
                .orderTxId("APPLE_TX_12345")
                .storeProcessDt(LocalDateTime.now())
                .voucherType(VoucherType.STORAGE_BASIC)
                .groupId(10L)
                .memo("테스트")
                .build();

            String responseBody = """
                {
                    "status": 0,
                    "environment": "Sandbox"
                }
                """;

            appleServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json"));

            // when
            boolean result = adapter.verify(command);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("[fail] Apple 결제 검증 실패 - status가 0이 아닌 경우")
        void fail_invalidStatus() {
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

            String responseBody = """
                {
                    "status": 21007,
                    "environment": "Production"
                }
                """;

            appleServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json"));

            // when
            boolean result = adapter.verify(command);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("[fail] Apple 서버 에러 시 검증 실패")
        void fail_serverError() {
            // given
            CreateVoucherCommand command = CreateVoucherCommand.builder()
                .accountId(1L)
                .storeType(StoreType.APPLE)
                .subscriptionKey("test-receipt-data")
                .orderTxId("APPLE_TX_12345")
                .storeProcessDt(LocalDateTime.now())
                .voucherType(VoucherType.STORAGE_BASIC)
                .groupId(10L)
                .memo("테스트")
                .build();

            appleServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("{\"error\": \"internal_server_error\"}")
                .addHeader("Content-Type", "application/json"));

            // when
            boolean result = adapter.verify(command);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("[Google] Google Play 결제 검증")
    class Describe_googlePlayPaymentVerification {

        @Test
        @DisplayName("[success] Google Play 결제 검증 성공")
        void success() {
            // given
            CreateVoucherCommand command = CreateVoucherCommand.builder()
                .accountId(1L)
                .storeType(StoreType.GOOGLE)
                .subscriptionKey("test-purchase-token")
                .orderTxId("GPA.1234-5678-9012")
                .storeProcessDt(LocalDateTime.now())
                .voucherType(VoucherType.STORAGE_BASIC)
                .groupId(10L)
                .memo("테스트")
                .build();

            String responseBody = """
                {
                    "kind": "androidpublisher#productPurchase",
                    "purchaseTimeMillis": "1234567890000",
                    "purchaseState": 0,
                    "consumptionState": 0,
                    "developerPayload": "",
                    "orderId": "GPA.1234-5678-9012",
                    "purchaseType": 0,
                    "acknowledgementState": 1,
                    "purchaseToken": "test-purchase-token",
                    "productId": "STORAGE_BASIC",
                    "quantity": 1,
                    "regionCode": "KR"
                }
                """;

            googlePlayServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json"));

            // when
            boolean result = adapter.verify(command);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("[fail] Google Play 결제 검증 실패 - purchaseState가 0이 아닌 경우")
        void fail_invalidPurchaseState() {
            // given
            CreateVoucherCommand command = CreateVoucherCommand.builder()
                .accountId(1L)
                .storeType(StoreType.GOOGLE)
                .subscriptionKey("canceled-purchase-token")
                .orderTxId("GPA.CANCELED-1234")
                .storeProcessDt(LocalDateTime.now())
                .voucherType(VoucherType.STORAGE_BASIC)
                .groupId(10L)
                .memo("테스트")
                .build();

            String responseBody = """
                {
                    "kind": "androidpublisher#productPurchase",
                    "purchaseTimeMillis": "1234567890000",
                    "purchaseState": 1,
                    "consumptionState": 0,
                    "orderId": "GPA.CANCELED-1234",
                    "purchaseToken": "canceled-purchase-token",
                    "productId": "STORAGE_BASIC"
                }
                """;

            googlePlayServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json"));

            // when
            boolean result = adapter.verify(command);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("[fail] Google Play 서버 에러 시 검증 실패")
        void fail_serverError() {
            // given
            CreateVoucherCommand command = CreateVoucherCommand.builder()
                .accountId(1L)
                .storeType(StoreType.GOOGLE)
                .subscriptionKey("test-purchase-token")
                .orderTxId("GPA.1234-5678-9012")
                .storeProcessDt(LocalDateTime.now())
                .voucherType(VoucherType.STORAGE_BASIC)
                .groupId(10L)
                .memo("테스트")
                .build();

            googlePlayServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody("{\"error\": \"not_found\"}")
                .addHeader("Content-Type", "application/json"));

            // when
            boolean result = adapter.verify(command);

            // then
            assertThat(result).isFalse();
        }
    }
}
