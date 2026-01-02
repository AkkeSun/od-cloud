package com.odcloud.application.group.service.update_group;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.odcloud.application.group.port.in.command.UpdateGroupCommand;
import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.FolderInfo;
import com.odcloud.domain.model.Group;
import com.odcloud.fakeClass.FakeAccountStoragePort;
import com.odcloud.fakeClass.FakeFolderStoragePort;
import com.odcloud.fakeClass.FakeGroupStoragePort;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UpdateGroupServiceTest {

    private FakeGroupStoragePort fakeGroupStoragePort;
    private FakeAccountStoragePort fakeAccountStoragePort;
    private FakeFolderStoragePort fakeFolderStoragePort;
    private UpdateGroupService updateGroupService;

    @BeforeEach
    void setUp() {
        fakeGroupStoragePort = new FakeGroupStoragePort();
        fakeAccountStoragePort = new FakeAccountStoragePort();
        fakeFolderStoragePort = new FakeFolderStoragePort();
        updateGroupService = new UpdateGroupService(
            fakeGroupStoragePort,
            fakeAccountStoragePort,
            fakeFolderStoragePort
        );
    }

    @Nested
    @DisplayName("[update] 그룹 정보 수정")
    class Describe_update {

        @Test
        @DisplayName("[failure] 그룹 소유자가 아닌 사용자가 수정 시도하면 예외가 발생한다")
        void failure_notGroupOwner() {
            // given
            Group group = Group.builder()
                .id("group-1")
                .name("Test Group")
                .ownerEmail("owner@example.com")
                .storageUsed(0L)
                .storageTotal(3221225472L)
                .regDt(LocalDateTime.now())
                .build();
            fakeGroupStoragePort.groupDatabase.add(group);

            UpdateGroupCommand command = UpdateGroupCommand.builder()
                .groupId("group-1")
                .currentOwnerEmail("notowner@example.com")
                .name("Updated Name")
                .build();

            // when & then
            assertThatThrownBy(() -> updateGroupService.update(command))
                .isInstanceOf(CustomBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.Business_INVALID_GROUP_OWNER);
        }

        @Test
        @DisplayName("[failure] ownerEmail 변경 시 존재하지 않는 이메일이면 예외가 발생한다")
        void failure_ownerEmailNotExists() {
            // given
            Group group = Group.builder()
                .id("group-1")
                .name("Test Group")
                .ownerEmail("owner@example.com")
                .storageUsed(0L)
                .storageTotal(3221225472L)
                .regDt(LocalDateTime.now())
                .build();
            fakeGroupStoragePort.groupDatabase.add(group);

            UpdateGroupCommand command = UpdateGroupCommand.builder()
                .groupId("group-1")
                .currentOwnerEmail("owner@example.com")
                .newOwnerEmail("nonexistent@example.com")
                .build();

            // when & then
            assertThatThrownBy(() -> updateGroupService.update(command))
                .isInstanceOf(CustomBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.Business_NOT_FOUND_ACCOUNT);
        }

        @Test
        @DisplayName("[failure] ownerEmail 변경 시 새 이메일이 이미 3개 그룹의 소유자이면 예외가 발생한다")
        void failure_newOwnerEmailLimitExceeded() {
            // given
            Group group = Group.builder()
                .id("group-1")
                .name("Test Group")
                .ownerEmail("owner@example.com")
                .storageUsed(0L)
                .storageTotal(3221225472L)
                .regDt(LocalDateTime.now())
                .build();
            fakeGroupStoragePort.groupDatabase.add(group);

            Account newOwner = Account.builder()
                .id(2L)
                .email("newowner@example.com")
                .nickname("New Owner")
                .name("새 오너")
                .groups(new ArrayList<>())
                .build();
            fakeAccountStoragePort.database.add(newOwner);

            // 새 오너가 이미 3개 그룹의 소유자
            for (int i = 1; i <= 3; i++) {
                Group existingGroup = Group.builder()
                    .id("existing-group-" + i)
                    .name("Existing Group " + i)
                    .ownerEmail("newowner@example.com")
                    .build();
                fakeGroupStoragePort.groupDatabase.add(existingGroup);
            }

            UpdateGroupCommand command = UpdateGroupCommand.builder()
                .groupId("group-1")
                .currentOwnerEmail("owner@example.com")
                .newOwnerEmail("newowner@example.com")
                .build();

            // when & then
            assertThatThrownBy(() -> updateGroupService.update(command))
                .isInstanceOf(CustomBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.Business_GROUP_LIMIT_EXCEEDED);
        }

        @Test
        @DisplayName("[success] ownerEmail 변경이 성공한다")
        void success_updateOwnerEmail() {
            // given
            Group group = Group.builder()
                .id("group-1")
                .name("Test Group")
                .ownerEmail("owner@example.com")
                .storageUsed(0L)
                .storageTotal(3221225472L)
                .regDt(LocalDateTime.now())
                .build();
            fakeGroupStoragePort.groupDatabase.add(group);

            Account newOwner = Account.builder()
                .id(2L)
                .email("newowner@example.com")
                .nickname("New Owner")
                .name("새 오너")
                .groups(new ArrayList<>())
                .build();
            fakeAccountStoragePort.database.add(newOwner);

            UpdateGroupCommand command = UpdateGroupCommand.builder()
                .groupId("group-1")
                .currentOwnerEmail("owner@example.com")
                .newOwnerEmail("newowner@example.com")
                .build();

            // when
            UpdateGroupServiceResponse response = updateGroupService.update(command);

            // then
            assertThat(response.result()).isTrue();
            assertThat(response.ownerEmail()).isEqualTo("newowner@example.com");
            assertThat(response.name()).isEqualTo("Test Group");

            Group updatedGroup = fakeGroupStoragePort.findById("group-1");
            assertThat(updatedGroup.getOwnerEmail()).isEqualTo("newowner@example.com");
            assertThat(updatedGroup.getModDt()).isNotNull();
        }

        @Test
        @DisplayName("[failure] name 변경 시 이미 존재하는 그룹명이면 예외가 발생한다")
        void failure_nameAlreadyExists() {
            // given
            Group group = Group.builder()
                .id("group-1")
                .name("Test Group")
                .ownerEmail("owner@example.com")
                .storageUsed(0L)
                .storageTotal(3221225472L)
                .regDt(LocalDateTime.now())
                .build();
            fakeGroupStoragePort.groupDatabase.add(group);

            Group existingGroup = Group.builder()
                .id("group-2")
                .name("Existing Group")
                .ownerEmail("other@example.com")
                .build();
            fakeGroupStoragePort.groupDatabase.add(existingGroup);

            UpdateGroupCommand command = UpdateGroupCommand.builder()
                .groupId("group-1")
                .currentOwnerEmail("owner@example.com")
                .name("Existing Group")
                .build();

            // when & then
            assertThatThrownBy(() -> updateGroupService.update(command))
                .isInstanceOf(CustomBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.Business_SAVED_GROUP);
        }

        @Test
        @DisplayName("[success] name 변경이 성공하고 루트 폴더명도 함께 변경된다")
        void success_updateName() {
            // given
            Group group = Group.builder()
                .id("group-1")
                .name("Old Group Name")
                .ownerEmail("owner@example.com")
                .storageUsed(0L)
                .storageTotal(3221225472L)
                .regDt(LocalDateTime.now())
                .build();
            fakeGroupStoragePort.groupDatabase.add(group);

            FolderInfo rootFolder = FolderInfo.builder()
                .id(1L)
                .groupId("group-1")
                .name("Old Group Name")
                .owner("owner@example.com")
                .path("/group-1")
                .parentId(null)
                .regDt(LocalDateTime.now())
                .build();
            fakeFolderStoragePort.database.add(rootFolder);

            UpdateGroupCommand command = UpdateGroupCommand.builder()
                .groupId("group-1")
                .currentOwnerEmail("owner@example.com")
                .name("New Group Name")
                .build();

            // when
            UpdateGroupServiceResponse response = updateGroupService.update(command);

            // then
            assertThat(response.result()).isTrue();
            assertThat(response.name()).isEqualTo("New Group Name");
            assertThat(response.ownerEmail()).isEqualTo("owner@example.com");

            Group updatedGroup = fakeGroupStoragePort.findById("group-1");
            assertThat(updatedGroup.getName()).isEqualTo("New Group Name");
            assertThat(updatedGroup.getModDt()).isNotNull();

            FolderInfo updatedRootFolder = fakeFolderStoragePort.findRootFolderByGroupId("group-1");
            assertThat(updatedRootFolder.getName()).isEqualTo("New Group Name");
            assertThat(updatedRootFolder.getModDt()).isNotNull();
        }

        @Test
        @DisplayName("[success] ownerEmail과 name 둘 다 변경이 성공한다")
        void success_updateBothOwnerEmailAndName() {
            // given
            Group group = Group.builder()
                .id("group-1")
                .name("Old Group Name")
                .ownerEmail("owner@example.com")
                .storageUsed(0L)
                .storageTotal(3221225472L)
                .regDt(LocalDateTime.now())
                .build();
            fakeGroupStoragePort.groupDatabase.add(group);

            Account newOwner = Account.builder()
                .id(2L)
                .email("newowner@example.com")
                .nickname("New Owner")
                .name("새 오너")
                .groups(new ArrayList<>())
                .build();
            fakeAccountStoragePort.database.add(newOwner);

            FolderInfo rootFolder = FolderInfo.builder()
                .id(1L)
                .groupId("group-1")
                .name("Old Group Name")
                .owner("owner@example.com")
                .path("/group-1")
                .parentId(null)
                .regDt(LocalDateTime.now())
                .build();
            fakeFolderStoragePort.database.add(rootFolder);

            UpdateGroupCommand command = UpdateGroupCommand.builder()
                .groupId("group-1")
                .currentOwnerEmail("owner@example.com")
                .newOwnerEmail("newowner@example.com")
                .name("New Group Name")
                .build();

            // when
            UpdateGroupServiceResponse response = updateGroupService.update(command);

            // then
            assertThat(response.result()).isTrue();
            assertThat(response.ownerEmail()).isEqualTo("newowner@example.com");
            assertThat(response.name()).isEqualTo("New Group Name");

            Group updatedGroup = fakeGroupStoragePort.findById("group-1");
            assertThat(updatedGroup.getOwnerEmail()).isEqualTo("newowner@example.com");
            assertThat(updatedGroup.getName()).isEqualTo("New Group Name");
            assertThat(updatedGroup.getModDt()).isNotNull();

            FolderInfo updatedRootFolder = fakeFolderStoragePort.findRootFolderByGroupId("group-1");
            assertThat(updatedRootFolder.getName()).isEqualTo("New Group Name");
            assertThat(updatedRootFolder.getModDt()).isNotNull();
        }

        @Test
        @DisplayName("[success] 변경 사항이 없으면 업데이트하지 않는다")
        void success_noChanges() {
            // given
            Group group = Group.builder()
                .id("group-1")
                .name("Test Group")
                .ownerEmail("owner@example.com")
                .storageUsed(0L)
                .storageTotal(3221225472L)
                .regDt(LocalDateTime.now())
                .build();
            fakeGroupStoragePort.groupDatabase.add(group);

            UpdateGroupCommand command = UpdateGroupCommand.builder()
                .groupId("group-1")
                .currentOwnerEmail("owner@example.com")
                .build();

            // when
            UpdateGroupServiceResponse response = updateGroupService.update(command);

            // then
            assertThat(response.result()).isTrue();
            assertThat(response.ownerEmail()).isEqualTo("owner@example.com");
            assertThat(response.name()).isEqualTo("Test Group");
        }
    }
}
