package com.odcloud.application.service.register_account;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.odcloud.application.port.in.command.RegisterAccountCommand;
import com.odcloud.fakeClass.DummySlackPort;
import com.odcloud.fakeClass.FakeAccountStoragePort;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

@ExtendWith(OutputCaptureExtension.class)
class RegisterAccountServiceTest {

    RegisterAccountService service;
    FakeAccountStoragePort fakeAccountStoragePort;
    DummySlackPort dummySlackPort;

    RegisterAccountServiceTest() {
        fakeAccountStoragePort = new FakeAccountStoragePort();
        dummySlackPort = new DummySlackPort();
        service = new RegisterAccountService(dummySlackPort, fakeAccountStoragePort);
    }

    @BeforeEach
    void setUp() {
        fakeAccountStoragePort.database.clear();
        fakeAccountStoragePort.id = 0L;
        fakeAccountStoragePort.shouldThrowException = false;
        dummySlackPort.sendCount = 0;
        dummySlackPort.lastSentRequest = null;
    }

    @Nested
    @DisplayName("[register] 계정을 등록하는 메서드")
    class Describe_register {

        @Test
        @DisplayName("[success] 계정 등록에 성공하고 Slack 메시지를 전송한다")
        void success(CapturedOutput output) {
            // given
            RegisterAccountCommand command = RegisterAccountCommand.builder()
                .username("testuser")
                .password("password123")
                .name("홍길동")
                .email("test@example.com")
                .role("ROLE_USER")
                .build();

            // when
            RegisterAccountServiceResponse response = service.register(command);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();
            assertThat(response.otpUrl()).isNotNull();
            assertThat(response.otpUrl()).contains("testuser");

            assertThat(fakeAccountStoragePort.database).hasSize(1);
            assertThat(fakeAccountStoragePort.database.get(0).getUsername()).isEqualTo("testuser");
            assertThat(fakeAccountStoragePort.database.get(0).getName()).isEqualTo("홍길동");
            assertThat(fakeAccountStoragePort.database.get(0).getEmail()).isEqualTo("test@example.com");
            assertThat(fakeAccountStoragePort.database.get(0).getRole()).isEqualTo("ROLE_USER");
            assertThat(fakeAccountStoragePort.database.get(0).getIsAdminApproved()).isFalse();

            assertThat(dummySlackPort.sendCount).isEqualTo(1);
            assertThat(dummySlackPort.lastSentRequest).isNotNull();
            assertThat(dummySlackPort.lastSentRequest.channel()).isEqualTo("#monitoring");
            assertThat(dummySlackPort.lastSentRequest.text()).contains("testuser");
            assertThat(dummySlackPort.lastSentRequest.text()).contains("홍길동");

            assertThat(output.toString()).contains("FakeAccountStoragePort registered");
            assertThat(output.toString()).contains("DummySlackPort sent");
        }

        @Test
        @DisplayName("[success] OTP URL에 사용자명이 포함된다")
        void success_otpUrlContainsUsername(CapturedOutput output) {
            // given
            RegisterAccountCommand command = RegisterAccountCommand.builder()
                .username("testuser123")
                .password("password123")
                .name("홍길동")
                .email("test@example.com")
                .role("ROLE_USER")
                .build();

            // when
            RegisterAccountServiceResponse response = service.register(command);

            // then
            assertThat(response.otpUrl()).isNotNull();
            assertThat(response.otpUrl()).contains("testuser123");
            assertThat(fakeAccountStoragePort.database).hasSize(1);
        }

        @Test
        @DisplayName("[error] 이미 등록된 사용자명이 존재하면 CustomBusinessException을 던진다")
        void error_duplicateUsername() {
            // given
            RegisterAccountCommand firstCommand = RegisterAccountCommand.builder()
                .username("existinguser")
                .password("password123")
                .name("기존사용자")
                .email("existing@example.com")
                .role("ROLE_USER")
                .build();
            service.register(firstCommand);

            RegisterAccountCommand duplicateCommand = RegisterAccountCommand.builder()
                .username("existinguser")
                .password("newpassword")
                .name("새사용자")
                .email("new@example.com")
                .role("ROLE_USER")
                .build();

            // when & then
            CustomBusinessException exception = assertThrows(
                CustomBusinessException.class,
                () -> service.register(duplicateCommand)
            );

            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.Business_SAVED_USER);
            assertThat(exception.getErrorCode().getMessage()).isEqualTo("등록된 사용자 정보 입니다");

            assertThat(fakeAccountStoragePort.database).hasSize(1);
            assertThat(dummySlackPort.sendCount).isEqualTo(1);
        }

        @Test
        @DisplayName("[success] 관리자 승인이 필요한 계정으로 등록된다")
        void success_adminApprovalRequired(CapturedOutput output) {
            // given
            RegisterAccountCommand command = RegisterAccountCommand.builder()
                .username("testuser")
                .password("password123")
                .name("홍길동")
                .email("test@example.com")
                .role("ROLE_USER")
                .build();

            // when
            service.register(command);

            // then
            assertThat(fakeAccountStoragePort.database.get(0).isAdminApproved()).isFalse();
            assertThat(output.toString()).contains("FakeAccountStoragePort registered");
        }

        @Test
        @DisplayName("[success] 다양한 권한(ROLE_ADMIN)으로 계정 등록이 가능하다")
        void success_adminRole(CapturedOutput output) {
            // given
            RegisterAccountCommand command = RegisterAccountCommand.builder()
                .username("adminuser")
                .password("adminpass123")
                .name("관리자")
                .email("admin@example.com")
                .role("ROLE_ADMIN")
                .build();

            // when
            RegisterAccountServiceResponse response = service.register(command);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();
            assertThat(response.otpUrl()).contains("adminuser");

            assertThat(fakeAccountStoragePort.database).hasSize(1);
            assertThat(fakeAccountStoragePort.database.get(0).getRole()).isEqualTo("ROLE_ADMIN");
        }

        @Test
        @DisplayName("[success] 2FA Secret이 자동으로 생성된다")
        void success_twoFactorSecretGenerated() {
            // given
            RegisterAccountCommand command = RegisterAccountCommand.builder()
                .username("testuser")
                .password("password123")
                .name("홍길동")
                .email("test@example.com")
                .role("ROLE_USER")
                .build();

            // when
            service.register(command);

            // then
            assertThat(fakeAccountStoragePort.database.get(0).getTwoFactorSecret()).isNotNull();
            assertThat(fakeAccountStoragePort.database.get(0).getTwoFactorSecret()).isNotEmpty();
        }

        @Test
        @DisplayName("[success] 등록 시간이 자동으로 기록된다")
        void success_regDateTimeRecorded() {
            // given
            RegisterAccountCommand command = RegisterAccountCommand.builder()
                .username("testuser")
                .password("password123")
                .name("홍길동")
                .email("test@example.com")
                .role("ROLE_USER")
                .build();

            // when
            service.register(command);

            // then
            assertThat(fakeAccountStoragePort.database.get(0).getRegDt()).isNotNull();
        }
    }
}