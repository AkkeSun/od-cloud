package com.odcloud.adapter.in.controller.callback_google_oauth2;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CallbackGoogleOAuth2RequestTest {

    @Nested
    @DisplayName("[builder] Request 빌더 패턴 테스트")
    class Describe_builder {

        @Test
        @DisplayName("[success] code를 설정하여 Request를 생성한다")
        void success() {
            // when
            CallbackGoogleOAuth2Request request = CallbackGoogleOAuth2Request.builder()
                .code("google-auth-code-123")
                .build();

            // then
            assertThat(request).isNotNull();
            assertThat(request.getCode()).isEqualTo("google-auth-code-123");
        }

        @Test
        @DisplayName("[success] null 값으로 Request를 생성한다")
        void success_nullValues() {
            // when
            CallbackGoogleOAuth2Request request = CallbackGoogleOAuth2Request.builder()
                .code(null)
                .build();

            // then
            assertThat(request.getCode()).isNull();
        }

        @Test
        @DisplayName("[success] 빈 문자열로 Request를 생성한다")
        void success_emptyString() {
            // when
            CallbackGoogleOAuth2Request request = CallbackGoogleOAuth2Request.builder()
                .code("")
                .build();

            // then
            assertThat(request.getCode()).isEmpty();
        }

        @Test
        @DisplayName("[success] 공백 문자열로 Request를 생성한다")
        void success_whitespaceString() {
            // when
            CallbackGoogleOAuth2Request request = CallbackGoogleOAuth2Request.builder()
                .code("   ")
                .build();

            // then
            assertThat(request.getCode()).isEqualTo("   ");
        }
    }
}
