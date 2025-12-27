package com.odcloud.application.service.update_group_account_status;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.odcloud.application.port.in.command.UpdateGroupAccountStatusCommand;
import com.odcloud.domain.model.Group;
import com.odcloud.domain.model.GroupAccount;
import com.odcloud.fakeClass.FakeAccountDeviceStoragePort;
import com.odcloud.fakeClass.FakeGroupStoragePort;
import com.odcloud.fakeClass.FakePushFcmUseCase;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UpdateGroupAccountStatusServiceTest {

    private FakePushFcmUseCase fakePushFcmUseCase;
    private FakeGroupStoragePort fakeGroupStoragePort;
    private FakeAccountDeviceStoragePort fakeAccountDeviceStoragePort;
    private UpdateGroupAccountStatusService updateGroupAccountStatusService;

    @BeforeEach
    void setUp() {
        fakePushFcmUseCase = new FakePushFcmUseCase();
        fakeGroupStoragePort = new FakeGroupStoragePort();
        fakeAccountDeviceStoragePort = new FakeAccountDeviceStoragePort();
        updateGroupAccountStatusService = new UpdateGroupAccountStatusService(
            fakePushFcmUseCase,
            fakeGroupStoragePort,
            fakeAccountDeviceStoragePort
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
            assertThat(fakePushFcmUseCase.sentCommands).isEmpty();
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

            assertThat(fakePushFcmUseCase.sentCommands).isEmpty();
        }

        @Test
        @DisplayName("[success] DENIED 상태로 변경하면서 메모를 저장한다")
        void success_deniedWithMemo() {
            // given
            String groupId = "test-group";
            String ownerEmail = "owner@example.com";
            Long accountId = 1L;
            String memo = "그룹 가입 요건을 충족하지 못했습니다.";

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
                .memo(memo)
                .build();

            // when
            UpdateGroupAccountStatusServiceResponse response = updateGroupAccountStatusService
                .updateStatus(command);

            // then
            assertThat(response).isNotNull();
            assertThat(fakeGroupStoragePort.groupAccountDatabase.get(0).getStatus()).isEqualTo(
                "DENIED");
            assertThat(fakeGroupStoragePort.groupAccountDatabase.get(0).getMemo()).isEqualTo(
                memo);
        }

        @Test
        @DisplayName("[success] BLOCK 상태로 변경하면서 메모를 저장한다")
        void success_blockWithMemo() {
            // given
            String groupId = "test-group";
            String ownerEmail = "owner@example.com";
            Long accountId = 1L;
            String memo = "부적절한 행동으로 차단되었습니다.";

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
                .status("BLOCK")
                .memo(memo)
                .build();

            // when
            UpdateGroupAccountStatusServiceResponse response = updateGroupAccountStatusService
                .updateStatus(command);

            // then
            assertThat(response).isNotNull();
            assertThat(fakeGroupStoragePort.groupAccountDatabase.get(0).getStatus()).isEqualTo(
                "BLOCK");
            assertThat(fakeGroupStoragePort.groupAccountDatabase.get(0).getMemo()).isEqualTo(
                memo);
        }
    }
}
