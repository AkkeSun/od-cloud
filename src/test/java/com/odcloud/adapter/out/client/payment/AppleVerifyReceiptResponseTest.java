package com.odcloud.adapter.out.client.payment;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class AppleVerifyReceiptResponseTest {

    @Nested
    @DisplayName("[isValid] 유효성 검증 메서드")
    class Describe_isValid {

        @Test
        @DisplayName("[success] status가 0이면 true를 반환한다")
        void success_statusZero_returnsTrue() {
            // given
            AppleVerifyReceiptResponse response = new AppleVerifyReceiptResponse(0, "Sandbox");

            // when
            boolean result = response.isValid();

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("[success] status가 21000이면 false를 반환한다 (App Store 읽기 불가)")
        void success_status21000_returnsFalse() {
            // given
            AppleVerifyReceiptResponse response = new AppleVerifyReceiptResponse(21000, "Production");

            // when
            boolean result = response.isValid();

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("[success] status가 21002이면 false를 반환한다 (영수증 데이터 잘못됨)")
        void success_status21002_returnsFalse() {
            // given
            AppleVerifyReceiptResponse response = new AppleVerifyReceiptResponse(21002, "Sandbox");

            // when
            boolean result = response.isValid();

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("[success] status가 21003이면 false를 반환한다 (영수증 인증 불가)")
        void success_status21003_returnsFalse() {
            // given
            AppleVerifyReceiptResponse response = new AppleVerifyReceiptResponse(21003, "Sandbox");

            // when
            boolean result = response.isValid();

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("[success] status가 21005이면 false를 반환한다 (서버 사용 불가)")
        void success_status21005_returnsFalse() {
            // given
            AppleVerifyReceiptResponse response = new AppleVerifyReceiptResponse(21005, "Sandbox");

            // when
            boolean result = response.isValid();

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("[success] status가 21007이면 false를 반환한다 (Sandbox 영수증를 Production으로 전송)")
        void success_status21007_returnsFalse() {
            // given
            AppleVerifyReceiptResponse response = new AppleVerifyReceiptResponse(21007, "Sandbox");

            // when
            boolean result = response.isValid();

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("[success] status가 21008이면 false를 반환한다 (Production 영수증를 Sandbox로 전송)")
        void success_status21008_returnsFalse() {
            // given
            AppleVerifyReceiptResponse response = new AppleVerifyReceiptResponse(21008, "Production");

            // when
            boolean result = response.isValid();

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("[record] 레코드 생성")
    class Describe_record {

        @Test
        @DisplayName("[success] 모든 필드를 지정하여 생성한다")
        void success_createWithAllFields() {
            // when
            AppleVerifyReceiptResponse response = new AppleVerifyReceiptResponse(0, "Production");

            // then
            assertThat(response.status()).isEqualTo(0);
            assertThat(response.environment()).isEqualTo("Production");
        }
    }
}
