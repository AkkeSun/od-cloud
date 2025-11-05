package com.odcloud.application.port.in.command;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RegisterAccountCommandTest {

    @Nested
    @DisplayName("[builder] Command 빌더 패턴 테스트")
    class Describe_builder {

        @Test
        @DisplayName("[success] 모든 필드를 설정하여 Command를 생성한다")
        void success() {
            // when
            RegisterAccountCommand command = RegisterAccountCommand.builder()
                .username("testuser")
                .password("password123")
                .name("홍길동")
                .email("test@example.com")
                .role("ROLE_USER")
                .build();

            // then
            assertThat(command).isNotNull();
            assertThat(command.username()).isEqualTo("testuser");
            assertThat(command.password()).isEqualTo("password123");
            assertThat(command.name()).isEqualTo("홍길동");
            assertThat(command.email()).isEqualTo("test@example.com");
            assertThat(command.role()).isEqualTo("ROLE_USER");
        }

        @Test
        @DisplayName("[success] 관리자 권한으로 Command를 생성한다")
        void success_adminRole() {
            // when
            RegisterAccountCommand command = RegisterAccountCommand.builder()
                .username("adminuser")
                .password("adminpass")
                .name("관리자")
                .email("admin@example.com")
                .role("ROLE_ADMIN")
                .build();

            // then
            assertThat(command.role()).isEqualTo("ROLE_ADMIN");
        }

        @Test
        @DisplayName("[success] null 값으로 Command를 생성한다")
        void success_nullValues() {
            // when
            RegisterAccountCommand command = RegisterAccountCommand.builder()
                .username(null)
                .password(null)
                .name(null)
                .email(null)
                .role(null)
                .build();

            // then
            assertThat(command.username()).isNull();
            assertThat(command.password()).isNull();
            assertThat(command.name()).isNull();
            assertThat(command.email()).isNull();
            assertThat(command.role()).isNull();
        }
    }

    @Nested
    @DisplayName("[record] Record 불변성 테스트")
    class Describe_immutability {

        @Test
        @DisplayName("[success] Command는 불변 객체이다")
        void success() {
            // given
            RegisterAccountCommand command1 = RegisterAccountCommand.builder()
                .username("testuser")
                .password("password123")
                .name("홍길동")
                .email("test@example.com")
                .role("ROLE_USER")
                .build();

            RegisterAccountCommand command2 = RegisterAccountCommand.builder()
                .username("testuser")
                .password("password123")
                .name("홍길동")
                .email("test@example.com")
                .role("ROLE_USER")
                .build();

            // when & then
            assertThat(command1).isEqualTo(command2);
            assertThat(command1.hashCode()).isEqualTo(command2.hashCode());
        }
    }
}