package com.odcloud.application.service.register_group;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.odcloud.application.port.in.command.RegisterGroupCommand;
import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.Group;
import com.odcloud.fakeClass.FakeAccountStoragePort;
import com.odcloud.fakeClass.FakeFileUploadPort;
import com.odcloud.fakeClass.FakeFolderStoragePort;
import com.odcloud.fakeClass.FakeGroupStoragePort;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RegisterGroupServiceTest {

    private FakeFileUploadPort fakeFileUploadPort;
    private FakeGroupStoragePort fakeGroupStoragePort;
    private FakeFolderStoragePort fakeFolderStoragePort;
    private FakeAccountStoragePort fakeAccountStoragePort;
    private RegisterGroupService registerGroupService;

    @BeforeEach
    void setUp() {
        fakeFileUploadPort = new FakeFileUploadPort();
        fakeGroupStoragePort = new FakeGroupStoragePort();
        fakeFolderStoragePort = new FakeFolderStoragePort();
        fakeAccountStoragePort = new FakeAccountStoragePort();
        registerGroupService = new RegisterGroupService(
            fakeFileUploadPort,
            fakeGroupStoragePort,
            fakeFolderStoragePort,
            fakeAccountStoragePort
        );
    }

    @Nested
    @DisplayName("[register] 그룹 등록")
    class Describe_register {

        @Test
        @DisplayName("[success] 정상적으로 그룹을 등록한다")
        void success() {
            // given
            Account owner = Account.builder()
                .id(1L)
                .email("owner@example.com")
                .nickname("Owner")
                .name("오너")
                .groups(new ArrayList<>())
                .build();
            fakeAccountStoragePort.database.add(owner);

            RegisterGroupCommand command = RegisterGroupCommand.builder()
                .id("test-group")
                .ownerEmail("owner@example.com")
                .description("Test Group Description")
                .build();

            // when
            RegisterGroupServiceResponse response = registerGroupService.register(command);

            // then
            assertThat(fakeGroupStoragePort.groupDatabase).hasSize(1);
            assertThat(fakeGroupStoragePort.groupDatabase.get(0).getId()).isEqualTo("test-group");
            assertThat(fakeGroupStoragePort.groupDatabase.get(0).getOwnerEmail()).isEqualTo(
                "owner@example.com");
            assertThat(fakeGroupStoragePort.groupDatabase.get(0).getDescription()).isEqualTo(
                "Test Group Description");
            assertThat(fakeGroupStoragePort.groupAccountDatabase).hasSize(1);
            assertThat(fakeGroupStoragePort.groupAccountDatabase.get(0).getGroupId()).isEqualTo(
                "test-group");
            assertThat(fakeGroupStoragePort.groupAccountDatabase.get(0).getAccountId()).isEqualTo(
                1L);
            assertThat(fakeGroupStoragePort.groupAccountDatabase.get(0).getStatus()).isEqualTo(
                "ACTIVE");
            assertThat(fakeFolderStoragePort.database).hasSize(1);
            assertThat(fakeFolderStoragePort.database.get(0).getGroupId()).isEqualTo("test-group");
            assertThat(fakeFolderStoragePort.database.get(0).getPath()).isEqualTo("/test-group");
            assertThat(fakeFileUploadPort.createdFolders).hasSize(1);
            assertThat(fakeFileUploadPort.createdFolders.get(0)).isEqualTo("/test-group");
        }

        @Test
        @DisplayName("[failure] 이미 존재하는 그룹 ID로 등록 시도하면 예외가 발생한다")
        void failure_existingGroupId() {
            // given
            Account owner = Account.builder()
                .id(1L)
                .email("owner@example.com")
                .nickname("Owner")
                .groups(new ArrayList<>())
                .build();
            fakeAccountStoragePort.database.add(owner);

            Group existingGroup = Group.builder()
                .id("existing-group")
                .ownerEmail("owner@example.com")
                .description("Existing Group")
                .build();
            fakeGroupStoragePort.groupDatabase.add(existingGroup);

            RegisterGroupCommand command = RegisterGroupCommand.builder()
                .id("existing-group")
                .ownerEmail("owner@example.com")
                .description("New Group")
                .build();

            // when & then
            assertThatThrownBy(() -> registerGroupService.register(command))
                .isInstanceOf(CustomBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.Business_SAVED_GROUP);

            assertThat(fakeGroupStoragePort.groupDatabase).hasSize(1);
            assertThat(fakeGroupStoragePort.groupAccountDatabase).isEmpty();
            assertThat(fakeFolderStoragePort.database).isEmpty();
            assertThat(fakeFileUploadPort.createdFolders).isEmpty();
        }
    }
}
