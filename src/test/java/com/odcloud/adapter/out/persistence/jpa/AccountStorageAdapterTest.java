package com.odcloud.adapter.out.persistence.jpa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.odcloud.IntegrationTestSupport;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AccountStorageAdapterTest extends IntegrationTestSupport {

    @Autowired
    AccountStorageAdapter adapter;

    @Autowired
    AccountRepository repository;

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Nested
    @DisplayName("[register] 계정을 저장하는 메서드")
    class Describe_register {

        @Test
        @DisplayName("[success] 계정을 저장한다")
        void success() {
            // given
            Account account = Account.builder()
                .username("testuser")
                .password("password123")
                .name("홍길동")
                .email("test@example.com")
                .role("ROLE_USER")
                .twoFactorSecret("SECRET")
                .isAdminApproved(false)
                .regDt(LocalDateTime.now())
                .build();

            // when
            adapter.register(account);

            // then
            assertThat(repository.count()).isEqualTo(1);
            AccountEntity savedEntity = repository.findAll().get(0);
            assertThat(savedEntity.getUsername()).isEqualTo("testuser");
            assertThat(savedEntity.getEmail()).isEqualTo("test@example.com");
            assertThat(savedEntity.getRole()).isEqualTo("ROLE_USER");
            assertThat(savedEntity.getIsAdminApproved()).isFalse();
        }

        @Test
        @DisplayName("[success] 비밀번호가 암호화되어 저장된다")
        void success_passwordEncoded() {
            // given
            Account account = Account.builder()
                .username("testuser")
                .password("plainPassword")
                .name("홍길동")
                .email("test@example.com")
                .role("ROLE_USER")
                .twoFactorSecret("SECRET")
                .isAdminApproved(false)
                .regDt(LocalDateTime.now())
                .build();

            // when
            adapter.register(account);

            // then
            AccountEntity savedEntity = repository.findAll().get(0);
            assertThat(savedEntity.getPassword()).isNotEqualTo("plainPassword");
            assertThat(savedEntity.getPassword()).startsWith("$2a$"); // BCrypt 형식
        }

        @Test
        @DisplayName("[success] 이름이 암호화되어 저장된다")
        void success_nameEncrypted() {
            // given
            Account account = Account.builder()
                .username("testuser")
                .password("password123")
                .name("홍길동")
                .email("test@example.com")
                .role("ROLE_USER")
                .twoFactorSecret("SECRET")
                .isAdminApproved(false)
                .regDt(LocalDateTime.now())
                .build();

            // when
            adapter.register(account);

            // then
            AccountEntity savedEntity = repository.findAll().get(0);
            assertThat(savedEntity.getName()).isNotEqualTo("홍길동");
        }
    }

    @Nested
    @DisplayName("[update] 계정을 수정하는 메서드")
    class Describe_update {

        @Test
        @DisplayName("[success] 계정을 수정한다")
        void success() {
            // given
            Account account = Account.builder()
                .username("testuser")
                .password("password123")
                .name("홍길동")
                .email("test@example.com")
                .role("ROLE_USER")
                .twoFactorSecret("SECRET")
                .isAdminApproved(false)
                .regDt(LocalDateTime.now())
                .build();
            adapter.register(account);

            Account savedAccount = adapter.findByUsername("testuser");

            // when
            Account updatedAccount = Account.builder()
                .id(savedAccount.getId())
                .username(savedAccount.getUsername())
                .password(savedAccount.getPassword())
                .name("김철수")
                .email("updated@example.com")
                .role(savedAccount.getRole())
                .twoFactorSecret(savedAccount.getTwoFactorSecret())
                .isAdminApproved(true)
                .regDt(savedAccount.getRegDt())
                .build();

            adapter.update(updatedAccount);

            // then
            Account result = adapter.findByUsername("testuser");
            assertThat(result.getName()).isEqualTo("김철수");
            assertThat(result.getEmail()).isEqualTo("updated@example.com");
            assertThat(result.isAdminApproved()).isTrue();
        }
    }

    @Nested
    @DisplayName("[existsByUsername] 사용자명 존재 여부를 확인하는 메서드")
    class Describe_existsByUsername {

        @Test
        @DisplayName("[success] 존재하는 사용자명이면 true를 반환한다")
        void success_exists() {
            // given
            Account account = Account.builder()
                .username("existinguser")
                .password("password123")
                .name("홍길동")
                .email("test@example.com")
                .role("ROLE_USER")
                .twoFactorSecret("SECRET")
                .isAdminApproved(false)
                .regDt(LocalDateTime.now())
                .build();
            adapter.register(account);

            // when
            boolean exists = adapter.existsByUsername("existinguser");

            // then
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("[success] 존재하지 않는 사용자명이면 false를 반환한다")
        void success_notExists() {
            // when
            boolean exists = adapter.existsByUsername("nonexistentuser");

            // then
            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("[findByUsername] 사용자명으로 계정을 조회하는 메서드")
    class Describe_findByUsername {

        @Test
        @DisplayName("[success] 사용자명으로 계정을 조회한다")
        void success() {
            // given
            Account account = Account.builder()
                .username("testuser")
                .password("password123")
                .name("홍길동")
                .email("test@example.com")
                .role("ROLE_USER")
                .twoFactorSecret("SECRET")
                .isAdminApproved(false)
                .regDt(LocalDateTime.now())
                .build();
            adapter.register(account);

            // when
            Account result = adapter.findByUsername("testuser");

            // then
            assertThat(result).isNotNull();
            assertThat(result.getUsername()).isEqualTo("testuser");
            assertThat(result.getName()).isEqualTo("홍길동");
            assertThat(result.getEmail()).isEqualTo("test@example.com");
            assertThat(result.getRole()).isEqualTo("ROLE_USER");
        }

        @Test
        @DisplayName("[success] 암호화된 이름이 복호화되어 반환된다")
        void success_nameDecrypted() {
            // given
            Account account = Account.builder()
                .username("testuser")
                .password("password123")
                .name("홍길동")
                .email("test@example.com")
                .role("ROLE_USER")
                .twoFactorSecret("SECRET")
                .isAdminApproved(false)
                .regDt(LocalDateTime.now())
                .build();
            adapter.register(account);

            // when
            Account result = adapter.findByUsername("testuser");

            // then
            assertThat(result.getName()).isEqualTo("홍길동");
        }

        @Test
        @DisplayName("[error] 존재하지 않는 사용자명이면 CustomBusinessException을 던진다")
        void error_notFound() {
            // when & then
            CustomBusinessException exception = assertThrows(
                CustomBusinessException.class,
                () -> adapter.findByUsername("nonexistentuser")
            );

            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.Business_NOT_FOUND_ACCOUNT);
        }
    }

    @Nested
    @DisplayName("[findByUsernameAndPassword] 사용자명과 비밀번호로 계정을 조회하는 메서드")
    class Describe_findByUsernameAndPassword {

        @Test
        @DisplayName("[success] 사용자명과 비밀번호가 일치하면 계정을 조회한다")
        void success() {
            // given
            Account account = Account.builder()
                .username("testuser")
                .password("password123")
                .name("홍길동")
                .email("test@example.com")
                .role("ROLE_USER")
                .twoFactorSecret("SECRET")
                .isAdminApproved(false)
                .regDt(LocalDateTime.now())
                .build();
            adapter.register(account);

            // when
            Account result = adapter.findByUsernameAndPassword("testuser", "password123");

            // then
            assertThat(result).isNotNull();
            assertThat(result.getUsername()).isEqualTo("testuser");
            assertThat(result.getName()).isEqualTo("홍길동");
        }

        @Test
        @DisplayName("[error] 비밀번호가 일치하지 않으면 CustomBusinessException을 던진다")
        void error_passwordMismatch() {
            // given
            Account account = Account.builder()
                .username("testuser")
                .password("password123")
                .name("홍길동")
                .email("test@example.com")
                .role("ROLE_USER")
                .twoFactorSecret("SECRET")
                .isAdminApproved(false)
                .regDt(LocalDateTime.now())
                .build();
            adapter.register(account);

            // when & then
            CustomBusinessException exception = assertThrows(
                CustomBusinessException.class,
                () -> adapter.findByUsernameAndPassword("testuser", "wrongpassword")
            );

            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.Business_NOT_FOUND_ACCOUNT);
        }

        @Test
        @DisplayName("[error] 존재하지 않는 사용자명이면 CustomBusinessException을 던진다")
        void error_userNotFound() {
            // when & then
            CustomBusinessException exception = assertThrows(
                CustomBusinessException.class,
                () -> adapter.findByUsernameAndPassword("nonexistentuser", "password123")
            );

            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.Business_NOT_FOUND_ACCOUNT);
        }
    }
}