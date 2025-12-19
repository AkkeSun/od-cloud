package com.odcloud.application.service.update_group_account_status;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.odcloud.application.port.in.command.UpdateGroupAccountStatusCommand;
import com.odcloud.domain.model.Group;
import com.odcloud.domain.model.GroupAccount;
import com.odcloud.fakeClass.FakeGroupStoragePort;
import com.odcloud.fakeClass.FakeMailPort;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UpdateGroupAccountStatusServiceTest {

    private FakeMailPort fakeMailPort;
    private FakeGroupStoragePort fakeGroupStoragePort;
    private UpdateGroupAccountStatusService updateGroupAccountStatusService;

    @BeforeEach
    void setUp() {
        fakeMailPort = new FakeMailPort();
        fakeGroupStoragePort = new FakeGroupStoragePort();
        updateGroupAccountStatusService = new UpdateGroupAccountStatusService(
            fakeMailPort,
            fakeGroupStoragePort
        );
    }

    @Nested
    @DisplayName("[updateStatus] 그룹 계정 상태 변경")
    class Describe_updateStatus {

        @Test
        @DisplayName("[success] 정상적으로 상태를 업데이트한다")
        void success() {
            // given
            String groupId = "test-group";
            String ownerEmail = "owner@example.com";
            Long accountId = 1L;

            Group group = Group.builder()
                .id(groupId)
                .ownerEmail(ownerEmail)
                .name("Test Group")
                .build();
            fakeGroupStoragePort.groupDatabase.add(group);

            GroupAccount groupAccount = GroupAccount.builder()
                .id(1L)
                .groupId(groupId)
                .accountId(accountId)
                .email("user@example.com")
                .name("사용자")
                .status("PENDING")
                .build();
            fakeGroupStoragePort.groupAccountDatabase.add(groupAccount);

            UpdateGroupAccountStatusCommand command = UpdateGroupAccountStatusCommand.builder()
                .groupId(groupId)
                .accountId(accountId)
                .groupOwnerEmail(ownerEmail)
                .status("ACTIVE")
                .build();

            // when
            UpdateGroupAccountStatusServiceResponse response = updateGroupAccountStatusService
                .updateStatus(command);

            // then
            assertThat(response).isNotNull();
            assertThat(fakeGroupStoragePort.groupAccountDatabase.get(0).getStatus()).isEqualTo(
                "ACTIVE");
        }

        @Test
        @DisplayName("[failure] 그룹 소유자가 아닌 사람이 상태 변경 시도하면 예외가 발생한다")
        void failure_notGroupOwner() {
            // given
            String groupId = "test-group";
            String ownerEmail = "owner@example.com";
            String notOwnerEmail = "notowner@example.com";
            Long accountId = 1L;

            Group group = Group.builder()
                .id(groupId)
                .ownerEmail(ownerEmail)
                .name("Test Group")
                .build();
            fakeGroupStoragePort.groupDatabase.add(group);

            GroupAccount groupAccount = GroupAccount.builder()
                .id(1L)
                .groupId(groupId)
                .accountId(accountId)
                .email("user@example.com")
                .status("PENDING")
                .build();
            fakeGroupStoragePort.groupAccountDatabase.add(groupAccount);

            UpdateGroupAccountStatusCommand command = UpdateGroupAccountStatusCommand.builder()
                .groupId(groupId)
                .accountId(accountId)
                .groupOwnerEmail(notOwnerEmail)
                .status("APPROVED")
                .build();

            // when & then
            assertThatThrownBy(() -> updateGroupAccountStatusService.updateStatus(command))
                .isInstanceOf(CustomBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.Business_INVALID_GROUP_OWNER);

            assertThat(fakeGroupStoragePort.groupAccountDatabase.get(0).getStatus()).isEqualTo(
                "PENDING");
            assertThat(fakeMailPort.sentMails).isEmpty();
        }


        @Test
        @DisplayName("[failure] 존재하지 않는 GroupAccount 상태 변경 시도하면 예외가 발생한다")
        void failure_nonExistentGroupAccount() {
            // given
            String groupId = "test-group";
            String ownerEmail = "owner@example.com";

            Group group = Group.builder()
                .id(groupId)
                .ownerEmail(ownerEmail)
                .name("Test Group")
                .build();
            fakeGroupStoragePort.groupDatabase.add(group);

            UpdateGroupAccountStatusCommand command = UpdateGroupAccountStatusCommand.builder()
                .groupId(groupId)
                .accountId(999L)
                .groupOwnerEmail(ownerEmail)
                .status("APPROVED")
                .build();

            // when & then
            assertThatThrownBy(() -> updateGroupAccountStatusService.updateStatus(command))
                .isInstanceOf(CustomBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode",
                    ErrorCode.Business_DoesNotExists_GROUP_ACCOUNT);

            assertThat(fakeMailPort.sentMails).isEmpty();
        }

        @Test
        @DisplayName("[failure] PENDING 상태가 아닌 사용자의 상태 변경 시도하면 예외가 발생한다")
        void failure_notPendingStatus() {
            // given
            String groupId = "test-group";
            String ownerEmail = "owner@example.com";
            Long accountId = 1L;

            Group group = Group.builder()
                .id(groupId)
                .ownerEmail(ownerEmail)
                .name("Test Group")
                .build();
            fakeGroupStoragePort.groupDatabase.add(group);

            GroupAccount groupAccount = GroupAccount.builder()
                .id(1L)
                .groupId(groupId)
                .accountId(accountId)
                .email("user@example.com")
                .name("사용자")
                .status("ACTIVE")
                .build();
            fakeGroupStoragePort.groupAccountDatabase.add(groupAccount);

            UpdateGroupAccountStatusCommand command = UpdateGroupAccountStatusCommand.builder()
                .groupId(groupId)
                .accountId(accountId)
                .groupOwnerEmail(ownerEmail)
                .status("DENIED")
                .build();

            // when & then
            assertThatThrownBy(() -> updateGroupAccountStatusService.updateStatus(command))
                .isInstanceOf(CustomBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode",
                    ErrorCode.Business_INVALID_GROUP_ACCOUNT_STATUS);

            assertThat(fakeGroupStoragePort.groupAccountDatabase.get(0).getStatus()).isEqualTo(
                "ACTIVE");
        }

        @Test
        @DisplayName("[success] DENIED 상태로 변경하면서 거절 사유를 저장한다")
        void success_deniedWithCause() {
            // given
            String groupId = "test-group";
            String ownerEmail = "owner@example.com";
            Long accountId = 1L;
            String deniedCause = "그룹 가입 요건을 충족하지 못했습니다.";

            Group group = Group.builder()
                .id(groupId)
                .ownerEmail(ownerEmail)
                .name("Test Group")
                .build();
            fakeGroupStoragePort.groupDatabase.add(group);

            GroupAccount groupAccount = GroupAccount.builder()
                .id(1L)
                .groupId(groupId)
                .accountId(accountId)
                .email("user@example.com")
                .name("사용자")
                .status("PENDING")
                .build();
            fakeGroupStoragePort.groupAccountDatabase.add(groupAccount);

            UpdateGroupAccountStatusCommand command = UpdateGroupAccountStatusCommand.builder()
                .groupId(groupId)
                .accountId(accountId)
                .groupOwnerEmail(ownerEmail)
                .status("DENIED")
                .deniedCause(deniedCause)
                .build();

            // when
            UpdateGroupAccountStatusServiceResponse response = updateGroupAccountStatusService
                .updateStatus(command);

            // then
            assertThat(response).isNotNull();
            assertThat(fakeGroupStoragePort.groupAccountDatabase.get(0).getStatus()).isEqualTo(
                "DENIED");
            assertThat(fakeGroupStoragePort.groupAccountDatabase.get(0).getDeniedCause()).isEqualTo(
                deniedCause);
            assertThat(fakeMailPort.sentMails).isEmpty();
        }
    }
}
