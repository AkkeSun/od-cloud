package com.odcloud.adapter.in.register_account;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.application.service.register_account.RegisterAccountServiceResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RegisterAccountResponseTest {

    @Nested
    @DisplayName("[of] ServiceResponse를 Response로 변환하는 정적 팩토리 메서드")
    class Describe_of {

        @Test
        @DisplayName("[success] ServiceResponse를 Response로 변환한다")
        void success() {
            // given
            RegisterAccountServiceResponse serviceResponse = RegisterAccountServiceResponse.builder()
                .result(true)
                .otpUrl("otpauth://totp/testuser?secret=ABC123")
                .build();

            // when
            RegisterAccountResponse response = RegisterAccountResponse.of(serviceResponse);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();
            assertThat(response.optUrl()).isEqualTo("otpauth://totp/testuser?secret=ABC123");
        }

        @Test
        @DisplayName("[success] result가 false인 ServiceResponse를 변환한다")
        void success_resultFalse() {
            // given
            RegisterAccountServiceResponse serviceResponse = RegisterAccountServiceResponse.builder()
                .result(false)
                .otpUrl(null)
                .build();

            // when
            RegisterAccountResponse response = RegisterAccountResponse.of(serviceResponse);

            // then
            assertThat(response.result()).isFalse();
            assertThat(response.optUrl()).isNull();
        }
    }

    @Nested
    @DisplayName("[builder] Response 빌더 패턴 테스트")
    class Describe_builder {

        @Test
        @DisplayName("[success] 모든 필드를 설정하여 Response를 생성한다")
        void success() {
            // when
            RegisterAccountResponse response = RegisterAccountResponse.builder()
                .result(true)
                .optUrl("otpauth://totp/testuser?secret=ABC123")
                .build();

            // then
            assertThat(response.result()).isTrue();
            assertThat(response.optUrl()).isEqualTo("otpauth://totp/testuser?secret=ABC123");
        }

        @Test
        @DisplayName("[success] null 값으로 Response를 생성한다")
        void success_nullValues() {
            // when
            RegisterAccountResponse response = RegisterAccountResponse.builder()
                .result(null)
                .optUrl(null)
                .build();

            // then
            assertThat(response.result()).isNull();
            assertThat(response.optUrl()).isNull();
        }
    }
}