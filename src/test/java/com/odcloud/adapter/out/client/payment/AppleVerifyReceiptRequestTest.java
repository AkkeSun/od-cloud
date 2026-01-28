package com.odcloud.adapter.out.client.payment;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class AppleVerifyReceiptRequestTest {

    @Nested
    @DisplayName("[of] 팩토리 메서드")
    class Describe_of {

        @Test
        @DisplayName("[success] receiptData와 password로 Request를 생성한다")
        void success_createRequest() {
            // given
            String receiptData = "test-receipt-data";
            String password = "test-password";

            // when
            AppleVerifyReceiptRequest request = AppleVerifyReceiptRequest.of(receiptData, password);

            // then
            assertThat(request.receiptData()).isEqualTo(receiptData);
            assertThat(request.password()).isEqualTo(password);
            assertThat(request.excludeOldTransactions()).isTrue();
        }

        @Test
        @DisplayName("[success] excludeOldTransactions는 기본값으로 true가 설정된다")
        void success_excludeOldTransactionsDefaultTrue() {
            // when
            AppleVerifyReceiptRequest request = AppleVerifyReceiptRequest.of("receipt", "pass");

            // then
            assertThat(request.excludeOldTransactions()).isTrue();
        }
    }

    @Nested
    @DisplayName("[record] 레코드 생성")
    class Describe_record {

        @Test
        @DisplayName("[success] 모든 필드를 지정하여 생성한다")
        void success_createWithAllFields() {
            // when
            AppleVerifyReceiptRequest request = new AppleVerifyReceiptRequest(
                "receipt-data",
                "password",
                false
            );

            // then
            assertThat(request.receiptData()).isEqualTo("receipt-data");
            assertThat(request.password()).isEqualTo("password");
            assertThat(request.excludeOldTransactions()).isFalse();
        }
    }
}
