package com.odcloud.application.group.service.update_group_account_use_yn;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.odcloud.application.port.in.command.UpdateGroupAccountUseYnCommand;
import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.GroupAccount;
import com.odcloud.fakeClass.FakeGroupStoragePort;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UpdateGroupAccountUseYnServiceTest {

    private FakeGroupStoragePort fakeGroupStoragePort;
    private UpdateGroupAccountUseYnService updateGroupAccountUseYnService;

    @BeforeEach
    void setUp() {
        fakeGroupStoragePort = new FakeGroupStoragePort();
        updateGroupAccountUseYnService = new UpdateGroupAccountUseYnService(
            fakeGroupStoragePort
        );
    }

    @Nested
    @DisplayName("[updateShowYn] 그룹 계정 노출 여부 변경")
    class Describe_updateShowYn {

        @Test
        @DisplayName("[success] Y로 정상적으로 노출 여부를 업데이트한다")
        void success_Y() {
            // given
            Long groupId = 1L;
            Long accountId = 1L;

            GroupAccount groupAccount = GroupAccount.builder()
                .id(1L)
                .groupId(groupId)
                .accountId(accountId)
                .email("user@example.com")
                .name("사용자")
                .status("ACTIVE")
                .showYn("N")
                .build();
            fakeGroupStoragePort.groupAccountDatabase.add(groupAccount);

            Account account = Account.builder()
                .id(accountId)
                .email("user@example.com")
                .build();

            UpdateGroupAccountUseYnCommand command = UpdateGroupAccountUseYnCommand.builder()
                .groupId(groupId)
                .account(account)
                .showYn("Y")
                .build();

            // when
            UpdateGroupAccountUseYnServiceResponse response = updateGroupAccountUseYnService
                .updateShowYn(command);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();
            assertThat(fakeGroupStoragePort.groupAccountDatabase.get(0).getShowYn()).isEqualTo("Y");
        }

        @Test
        @DisplayName("[success] N으로 정상적으로 노출 여부를 업데이트한다")
        void success_N() {
            // given
            Long groupId = 1L;
            Long accountId = 1L;

            GroupAccount groupAccount = GroupAccount.builder()
                .id(1L)
                .groupId(groupId)
                .accountId(accountId)
                .email("user@example.com")
                .name("사용자")
                .status("ACTIVE")
                .showYn("Y")
                .build();
            fakeGroupStoragePort.groupAccountDatabase.add(groupAccount);

            Account account = Account.builder()
                .id(accountId)
                .email("user@example.com")
                .build();

            UpdateGroupAccountUseYnCommand command = UpdateGroupAccountUseYnCommand.builder()
                .groupId(groupId)
                .account(account)
                .showYn("N")
                .build();

            // when
            UpdateGroupAccountUseYnServiceResponse response = updateGroupAccountUseYnService
                .updateShowYn(command);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();
            assertThat(fakeGroupStoragePort.groupAccountDatabase.get(0).getShowYn()).isEqualTo("N");
        }

        @Test
        @DisplayName("[failure] 존재하지 않는 GroupAccount 노출 여부 변경 시도하면 예외가 발생한다")
        void failure_nonExistentGroupAccount() {
            // given
            Long groupId = 1L;
            Long accountId = 999L;

            Account account = Account.builder()
                .id(accountId)
                .email("user@example.com")
                .build();

            UpdateGroupAccountUseYnCommand command = UpdateGroupAccountUseYnCommand.builder()
                .groupId(groupId)
                .account(account)
                .showYn("Y")
                .build();

            // when & then
            assertThatThrownBy(() -> updateGroupAccountUseYnService.updateShowYn(command))
                .isInstanceOf(CustomBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode",
                    ErrorCode.Business_DoesNotExists_GROUP_ACCOUNT);
        }
    }
}
