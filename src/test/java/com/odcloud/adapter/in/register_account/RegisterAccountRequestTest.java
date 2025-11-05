package com.odcloud.adapter.in.register_account;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.application.port.in.command.RegisterAccountCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RegisterAccountRequestTest {

    @Nested
    @DisplayName("[toCommand] Request를 Command로 변환하는 메서드")
    class Describe_toCommand {

        @Test
        @DisplayName("[success] Request를 Command로 변환한다")
        void success() {
            // given
            RegisterAccountRequest request = RegisterAccountRequest.builder()
                .username("testuser")
                .password("password123")
                .name("홍길동")
                .email("test@example.com")
                .role("ROLE_USER")
                .build();

            // when
            RegisterAccountCommand command = request.toCommand();

            // then
            assertThat(command).isNotNull();
            assertThat(command.username()).isEqualTo("testuser");
            assertThat(command.password()).isEqualTo("password123");
            assertThat(command.name()).isEqualTo("홍길동");
            assertThat(command.email()).isEqualTo("test@example.com");
            assertThat(command.role()).isEqualTo("ROLE_USER");
        }

        @Test
        @DisplayName("[success] 관리자 권한 Request를 Command로 변환한다")
        void success_adminRole() {
            // given
            RegisterAccountRequest request = RegisterAccountRequest.builder()
                .username("adminuser")
                .password("adminpass")
                .name("관리자")
                .email("admin@example.com")
                .role("ROLE_ADMIN")
                .build();

            // when
            RegisterAccountCommand command = request.toCommand();

            // then
            assertThat(command.role()).isEqualTo("ROLE_ADMIN");
        }
    }

    @Nested
    @DisplayName("[builder] Request 빌더 패턴 테스트")
    class Describe_builder {

        @Test
        @DisplayName("[success] 모든 필드를 설정하여 Request를 생성한다")
        void success() {
            // when
            RegisterAccountRequest request = RegisterAccountRequest.builder()
                .username("testuser")
                .password("password123")
                .name("홍길동")
                .email("test@example.com")
                .role("ROLE_USER")
                .build();

            // then
            assertThat(request.username()).isEqualTo("testuser");
            assertThat(request.password()).isEqualTo("password123");
            assertThat(request.name()).isEqualTo("홍길동");
            assertThat(request.email()).isEqualTo("test@example.com");
            assertThat(request.role()).isEqualTo("ROLE_USER");
        }

        @Test
        @DisplayName("[success] null 값으로 Request를 생성한다")
        void success_nullValues() {
            // when
            RegisterAccountRequest request = RegisterAccountRequest.builder()
                .username(null)
                .password(null)
                .name(null)
                .email(null)
                .role(null)
                .build();

            // then
            assertThat(request.username()).isNull();
            assertThat(request.password()).isNull();
            assertThat(request.name()).isNull();
            assertThat(request.email()).isNull();
            assertThat(request.role()).isNull();
        }
    }

    @Nested
    @DisplayName("[toString] 문자열 반환")
    class Describe_toString {

        @Test
        @DisplayName("[success] toString 메서드가 정상 동작한다")
        void success() {
            // given
            RegisterAccountRequest request = RegisterAccountRequest.builder()
                .username("testuser")
                .password("password123")
                .name("홍길동")
                .email("test@example.com")
                .role("ROLE_USER")
                .build();

            // when
            String result = request.toString();

            // then
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
        }
    }
}