package com.odcloud.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.application.port.in.command.RegisterAccountCommand;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class AccountTest {

    @Nested
    @DisplayName("[of] RegisterAccountCommand로부터 Account를 생성하는 정적 팩토리 메서드")
    class Describe_of_from_command {

        @Test
        @DisplayName("[success] RegisterAccountCommand와 twoFactorSecret으로 Account를 생성한다")
        void success() {
            // given
            RegisterAccountCommand command = RegisterAccountCommand.builder()
                .username("testuser")
                .password("password123")
                .name("홍길동")
                .email("test@example.com")
                .role("ROLE_USER")
                .build();
            String twoFactorSecret = "TEST_SECRET_KEY";

            // when
            Account account = Account.of(command, twoFactorSecret);

            // then
            assertThat(account).isNotNull();
            assertThat(account.getUsername()).isEqualTo("testuser");
            assertThat(account.getPassword()).isEqualTo("password123");
            assertThat(account.getName()).isEqualTo("홍길동");
            assertThat(account.getEmail()).isEqualTo("test@example.com");
            assertThat(account.getRole()).isEqualTo("ROLE_USER");
            assertThat(account.getTwoFactorSecret()).isEqualTo("TEST_SECRET_KEY");
            assertThat(account.getIsAdminApproved()).isFalse();
            assertThat(account.getRegDt()).isNotNull();
            assertThat(account.getRegDt()).isBefore(LocalDateTime.now().plusSeconds(1));
        }

        @Test
        @DisplayName("[success] isAdminApproved는 기본값으로 false가 설정된다")
        void success_adminApprovedDefaultFalse() {
            // given
            RegisterAccountCommand command = RegisterAccountCommand.builder()
                .username("testuser")
                .password("password123")
                .name("홍길동")
                .email("test@example.com")
                .role("ROLE_USER")
                .build();

            // when
            Account account = Account.of(command, "SECRET");

            // then
            assertThat(account.isAdminApproved()).isFalse();
        }
    }

    @Nested
    @DisplayName("[of] Claims로부터 Account를 생성하는 정적 팩토리 메서드")
    class Describe_of_from_claims {

        @Test
        @DisplayName("[success] Claims로부터 Account를 생성한다")
        void success() {
            // given
            Claims claims = Jwts.claims().setSubject("testuser");
            claims.put("role", "ROLE_USER");
            claims.put("id", "test@example.com");

            // when
            Account account = Account.of(claims);

            // then
            assertThat(account).isNotNull();
            assertThat(account.getUsername()).isEqualTo("testuser");
            assertThat(account.getRole()).isEqualTo("ROLE_USER");
            assertThat(account.getEmail()).isEqualTo("test@example.com");
        }

        @Test
        @DisplayName("[success] role이 null인 경우 null로 설정된다")
        void success_roleNull() {
            // given
            Claims claims = Jwts.claims().setSubject("testuser");
            claims.put("id", "test@example.com");

            // when
            Account account = Account.of(claims);

            // then
            assertThat(account.getRole()).isNull();
        }

        @Test
        @DisplayName("[success] id가 null인 경우 email이 null로 설정된다")
        void success_idNull() {
            // given
            Claims claims = Jwts.claims().setSubject("testuser");
            claims.put("role", "ROLE_USER");

            // when
            Account account = Account.of(claims);

            // then
            assertThat(account.getEmail()).isNull();
        }
    }

    @Nested
    @DisplayName("[isAdminApproved] 관리자 승인 여부를 확인하는 메서드")
    class Describe_isAdminApproved {

        @Test
        @DisplayName("[success] 승인되지 않은 경우 false를 반환한다")
        void success_notApproved() {
            // given
            Account account = Account.builder()
                .username("testuser")
                .isAdminApproved(false)
                .build();

            // when
            Boolean result = account.isAdminApproved();

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("[success] 승인된 경우 true를 반환한다")
        void success_approved() {
            // given
            Account account = Account.builder()
                .username("testuser")
                .isAdminApproved(true)
                .build();

            // when
            Boolean result = account.isAdminApproved();

            // then
            assertThat(result).isTrue();
        }
    }

    @Nested
    @DisplayName("[approve] 관리자 승인을 처리하는 메서드")
    class Describe_approve {

        @Test
        @DisplayName("[success] 승인되지 않은 계정을 승인한다")
        void success() {
            // given
            Account account = Account.builder()
                .username("testuser")
                .isAdminApproved(false)
                .build();

            // when
            account.approve();

            // then
            assertThat(account.isAdminApproved()).isTrue();
        }

        @Test
        @DisplayName("[success] 이미 승인된 계정도 승인 처리가 가능하다")
        void success_alreadyApproved() {
            // given
            Account account = Account.builder()
                .username("testuser")
                .isAdminApproved(true)
                .build();

            // when
            account.approve();

            // then
            assertThat(account.isAdminApproved()).isTrue();
        }
    }

    @Nested
    @DisplayName("[builder] Account 빌더 패턴 테스트")
    class Describe_builder {

        @Test
        @DisplayName("[success] 모든 필드를 설정하여 Account를 생성한다")
        void success_allFields() {
            // given
            LocalDateTime now = LocalDateTime.now();

            // when
            Account account = Account.builder()
                .id(1L)
                .username("testuser")
                .password("password123")
                .name("홍길동")
                .email("test@example.com")
                .role("ROLE_USER")
                .twoFactorSecret("SECRET")
                .isAdminApproved(false)
                .regDt(now)
                .build();

            // then
            assertThat(account.getId()).isEqualTo(1L);
            assertThat(account.getUsername()).isEqualTo("testuser");
            assertThat(account.getPassword()).isEqualTo("password123");
            assertThat(account.getName()).isEqualTo("홍길동");
            assertThat(account.getEmail()).isEqualTo("test@example.com");
            assertThat(account.getRole()).isEqualTo("ROLE_USER");
            assertThat(account.getTwoFactorSecret()).isEqualTo("SECRET");
            assertThat(account.getIsAdminApproved()).isFalse();
            assertThat(account.getRegDt()).isEqualTo(now);
        }

        @Test
        @DisplayName("[success] 일부 필드만 설정하여 Account를 생성한다")
        void success_partialFields() {
            // when
            Account account = Account.builder()
                .username("testuser")
                .email("test@example.com")
                .build();

            // then
            assertThat(account.getUsername()).isEqualTo("testuser");
            assertThat(account.getEmail()).isEqualTo("test@example.com");
            assertThat(account.getPassword()).isNull();
            assertThat(account.getName()).isNull();
            assertThat(account.getRole()).isNull();
        }
    }
}