package com.odcloud.application.service.register_account;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RegisterAccountServiceResponseTest {

    @Nested
    @DisplayName("[of] OTP URL로 ServiceResponse를 생성하는 정적 팩토리 메서드")
    class Describe_of {

        @Test
        @DisplayName("[success] OTP URL로 ServiceResponse를 생성한다")
        void success() {
            // given
            String otpAuthUrl = "otpauth://totp/testuser?secret=ABC123";

            // when
            RegisterAccountServiceResponse response = RegisterAccountServiceResponse.of(otpAuthUrl);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();
            assertThat(response.otpUrl()).isEqualTo(otpAuthUrl);
        }

        @Test
        @DisplayName("[success] QR 코드 URL로 ServiceResponse를 생성한다")
        void success_qrCodeUrl() {
            // given
            String qrCodeUrl = "https://api.qrserver.com/v1/create-qr-code/?data=otpauth%3A%2F%2Ftotp%2Fuser";

            // when
            RegisterAccountServiceResponse response = RegisterAccountServiceResponse.of(qrCodeUrl);

            // then
            assertThat(response.result()).isTrue();
            assertThat(response.otpUrl()).isEqualTo(qrCodeUrl);
        }
    }

    @Nested
    @DisplayName("[builder] ServiceResponse 빌더 패턴 테스트")
    class Describe_builder {

        @Test
        @DisplayName("[success] 모든 필드를 설정하여 ServiceResponse를 생성한다")
        void success() {
            // when
            RegisterAccountServiceResponse response = RegisterAccountServiceResponse.builder()
                .result(true)
                .otpUrl("otpauth://totp/testuser?secret=ABC123")
                .build();

            // then
            assertThat(response.result()).isTrue();
            assertThat(response.otpUrl()).isEqualTo("otpauth://totp/testuser?secret=ABC123");
        }

        @Test
        @DisplayName("[success] result가 false인 ServiceResponse를 생성한다")
        void success_resultFalse() {
            // when
            RegisterAccountServiceResponse response = RegisterAccountServiceResponse.builder()
                .result(false)
                .otpUrl(null)
                .build();

            // then
            assertThat(response.result()).isFalse();
            assertThat(response.otpUrl()).isNull();
        }
    }

    @Nested
    @DisplayName("[record] Record 불변성 테스트")
    class Describe_immutability {

        @Test
        @DisplayName("[success] ServiceResponse는 불변 객체이다")
        void success() {
            // given
            RegisterAccountServiceResponse response1 = RegisterAccountServiceResponse.builder()
                .result(true)
                .otpUrl("otpauth://totp/testuser?secret=ABC123")
                .build();

            RegisterAccountServiceResponse response2 = RegisterAccountServiceResponse.builder()
                .result(true)
                .otpUrl("otpauth://totp/testuser?secret=ABC123")
                .build();

            // when & then
            assertThat(response1).isEqualTo(response2);
            assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        }
    }
}