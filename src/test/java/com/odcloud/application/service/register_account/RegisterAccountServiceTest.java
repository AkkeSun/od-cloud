package com.odcloud.application.service.register_account;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.odcloud.adapter.out.client.google.GoogleUserInfoResponse;
import com.odcloud.application.port.in.command.RegisterAccountCommand;
import com.odcloud.domain.model.Group;
import com.odcloud.fakeClass.FakeAccountStoragePort;
import com.odcloud.fakeClass.FakeFilePort;
import com.odcloud.fakeClass.FakeFolderStoragePort;
import com.odcloud.fakeClass.FakeGoogleOAuth2Port;
import com.odcloud.fakeClass.FakeGroupStoragePort;
import com.odcloud.fakeClass.FakeMailPort;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RegisterAccountServiceTest {

    private FakeMailPort fakeMailPort;
    private FakeGroupStoragePort fakeGroupStoragePort;
    private FakeGoogleOAuth2Port fakeGoogleOAuth2Port;
    private FakeAccountStoragePort fakeAccountStoragePort;
    private RegisterAccountService registerAccountService;

    @BeforeEach
    void setUp() {
        fakeMailPort = new FakeMailPort();
        fakeGroupStoragePort = new FakeGroupStoragePort();
        fakeGoogleOAuth2Port = new FakeGoogleOAuth2Port();
        fakeAccountStoragePort = new FakeAccountStoragePort();
        registerAccountService = new RegisterAccountService(
            fakeMailPort,
            new FakeFilePort(),
            fakeGroupStoragePort,
            fakeGoogleOAuth2Port,
            fakeAccountStoragePort,
            new FakeFolderStoragePort()
        );
    }

    @Nested
    @DisplayName("[register] 계정 등록")
    class Describe_register {

        @Test
        @DisplayName("[success] 정상적으로 계정을 등록한다")
        void success() {
            // given
            String groupId = "test-group";
            Group group = Group.builder()
                .id(groupId)
                .ownerEmail("owner@example.com")
                .name("Test Group")
                .build();
            fakeGroupStoragePort.groupDatabase.add(group);

            RegisterAccountCommand command = RegisterAccountCommand.builder()
                .googleAuthorization("Bearer test-token")
                .name("홍길동")
                .groupId(groupId)
                .build();

            // when
            RegisterAccountServiceResponse response = registerAccountService.register(command);

            // then
            assertThat(response).isNotNull();
            assertThat(fakeAccountStoragePort.database).hasSize(1);
            assertThat(fakeAccountStoragePort.database.get(0).getEmail()).isEqualTo(
                "fake@example.com");
            assertThat(fakeAccountStoragePort.database.get(0).getName()).isEqualTo("홍길동");
            assertThat(fakeGroupStoragePort.groupAccountDatabase).hasSize(1);
            assertThat(fakeGroupStoragePort.groupAccountDatabase.get(0).getStatus()).isEqualTo(
                "PENDING");
            assertThat(fakeMailPort.sentMails).hasSize(1);
        }

        @Test
        @DisplayName("[failure] 이미 존재하는 이메일로 등록 시도하면 예외가 발생한다")
        void failure_existingEmail() {
            // given
            GoogleUserInfoResponse userInfo = GoogleUserInfoResponse.builder()
                .email("existing@example.com")
                .name("기존 사용자")
                .picture("https://example.com/photo.jpg")
                .build();
            fakeGoogleOAuth2Port.mockUserInfoResponse = userInfo;

            fakeAccountStoragePort.database.add(
                com.odcloud.domain.model.Account.builder()
                    .id(1L)
                    .email("existing@example.com")
                    .nickname("기존 사용자")
                    .build()
            );

            String groupId = "test-group";
            Group group = Group.builder()
                .id(groupId)
                .ownerEmail("owner@example.com")
                .build();
            fakeGroupStoragePort.groupDatabase.add(group);

            RegisterAccountCommand command = RegisterAccountCommand.builder()
                .googleAuthorization("Bearer test-token")
                .name("새 사용자")
                .groupId(groupId)
                .build();

            // when & then
            assertThatThrownBy(() -> registerAccountService.register(command))
                .isInstanceOf(CustomBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.Business_SAVED_USER);

            assertThat(fakeAccountStoragePort.database).hasSize(1);
            assertThat(fakeGroupStoragePort.groupAccountDatabase).isEmpty();
            assertThat(fakeMailPort.sentMails).isEmpty();
        }

        @Test
        @DisplayName("[failure] 존재하지 않는 그룹으로 등록 시도하면 예외가 발생한다")
        void failure_nonExistentGroup() {
            // given
            RegisterAccountCommand command = RegisterAccountCommand.builder()
                .googleAuthorization("Bearer test-token")
                .name("홍길동")
                .groupId("non-existent-group")
                .build();

            // when & then
            assertThatThrownBy(() -> registerAccountService.register(command))
                .isInstanceOf(CustomBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.Business_DoesNotExists_GROUP);

            assertThat(fakeAccountStoragePort.database).hasSize(1);
            assertThat(fakeGroupStoragePort.groupAccountDatabase).isEmpty();
            assertThat(fakeMailPort.sentMails).isEmpty();
        }

        @Test
        @DisplayName("[failure] GoogleOAuth2Port에서 예외 발생 시 예외를 전파한다")
        void failure_googleOAuth2Error() {
            // given
            fakeGoogleOAuth2Port.shouldThrowExceptionOnGetUserInfo = true;

            String groupId = "test-group";
            Group group = Group.builder()
                .id(groupId)
                .ownerEmail("owner@example.com")
                .build();
            fakeGroupStoragePort.groupDatabase.add(group);

            RegisterAccountCommand command = RegisterAccountCommand.builder()
                .googleAuthorization("Bearer invalid-token")
                .name("홍길동")
                .groupId(groupId)
                .build();

            // when & then
            assertThatThrownBy(() -> registerAccountService.register(command))
                .isInstanceOf(CustomBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode",
                    ErrorCode.Business_GOOGLE_USER_INFO_ERROR);

            assertThat(fakeAccountStoragePort.database).isEmpty();
            assertThat(fakeMailPort.sentMails).isEmpty();
        }

        @Test
        @DisplayName("[success] 다양한 사용자 정보로 계정을 등록한다")
        void success_variousUserInfo() {
            // given
            GoogleUserInfoResponse customUserInfo = GoogleUserInfoResponse.builder()
                .sub("custom-sub-456")
                .name("커스텀 사용자")
                .given_name("사용자")
                .picture("https://custom.example.com/avatar.png")
                .email("custom@example.com")
                .email_verified(true)
                .build();
            fakeGoogleOAuth2Port.mockUserInfoResponse = customUserInfo;

            String groupId = "custom-group";
            Group group = Group.builder()
                .id(groupId)
                .ownerEmail("owner@example.com")
                .name("Custom Group")
                .build();
            fakeGroupStoragePort.groupDatabase.add(group);

            RegisterAccountCommand command = RegisterAccountCommand.builder()
                .googleAuthorization("Bearer custom-token")
                .name("커스텀 이름")
                .groupId(groupId)
                .build();

            // when
            RegisterAccountServiceResponse response = registerAccountService.register(command);

            // then
            assertThat(response).isNotNull();
            assertThat(fakeAccountStoragePort.database).hasSize(1);
            assertThat(fakeAccountStoragePort.database.get(0).getEmail()).isEqualTo(
                "custom@example.com");
            assertThat(fakeAccountStoragePort.database.get(0).getName()).isEqualTo("커스텀 이름");
            assertThat(fakeAccountStoragePort.database.get(0).getNickname()).isEqualTo("커스텀 사용자");
        }

        @Test
        @DisplayName("[success] 빈 이름으로도 계정을 등록할 수 있다")
        void success_emptyName() {
            // given
            String groupId = "test-group";
            Group group = Group.builder()
                .id(groupId)
                .ownerEmail("owner@example.com")
                .build();
            fakeGroupStoragePort.groupDatabase.add(group);

            RegisterAccountCommand command = RegisterAccountCommand.builder()
                .googleAuthorization("Bearer test-token")
                .name("")
                .groupId(groupId)
                .build();

            // when
            RegisterAccountServiceResponse response = registerAccountService.register(command);

            // then
            assertThat(response).isNotNull();
            assertThat(fakeAccountStoragePort.database).hasSize(1);
            assertThat(fakeAccountStoragePort.database.get(0).getName()).isEmpty();
        }

        @Test
        @DisplayName("[failure] 이미 존재하는 그룹명으로 등록 시도하면 예외가 발생한다")
        void failure_existingGroupName() {
            // given
            String existingGroupName = "기존 그룹";
            Group existingGroup = Group.builder()
                .id("existing-group-id")
                .name(existingGroupName)
                .ownerEmail("existing@example.com")
                .build();
            fakeGroupStoragePort.groupDatabase.add(existingGroup);

            RegisterAccountCommand command = RegisterAccountCommand.builder()
                .googleAuthorization("Bearer test-token")
                .name("홍길동")
                .newGroupName(existingGroupName)
                .build();

            // when & then
            assertThatThrownBy(() -> registerAccountService.register(command))
                .isInstanceOf(CustomBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode",
                    ErrorCode.Business_SAVED_GROUP);

            assertThat(fakeAccountStoragePort.database).hasSize(1);
            assertThat(fakeGroupStoragePort.groupDatabase).hasSize(1);
            assertThat(fakeGroupStoragePort.groupAccountDatabase).isEmpty();
            assertThat(fakeMailPort.sentMails).isEmpty();
        }
    }
}
