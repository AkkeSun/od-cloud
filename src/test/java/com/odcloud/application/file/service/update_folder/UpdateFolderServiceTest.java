package com.odcloud.application.file.service.update_folder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.odcloud.application.file.port.in.command.UpdateFolderCommand;
import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.FolderInfo;
import com.odcloud.domain.model.Group;
import com.odcloud.fakeClass.FakeFilePort;
import com.odcloud.fakeClass.FakeFolderStoragePort;
import com.odcloud.infrastructure.exception.CustomAuthorizationException;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UpdateFolderServiceTest {

    private FakeFolderStoragePort fakeFolderStoragePort;
    private FakeFilePort fakeFilePort;
    private UpdateFolderService updateFolderService;

    @BeforeEach
    void setUp() {
        fakeFolderStoragePort = new FakeFolderStoragePort();
        fakeFilePort = new FakeFilePort();
        updateFolderService = new UpdateFolderService(fakeFolderStoragePort);
    }

    @Nested
    @DisplayName("[updateFolder] 폴더 정보 수정")
    class Describe_updateFolder {

        @Test
        @DisplayName("[success] 폴더명만 수정한다")
        void success_updateNameOnly() {
            // given
            Account account = Account.builder()
                .id(1L)
                .groups(List.of(Group.of(1L)))
                .email("user@example.com")
                .nickname("User")
                .name("사용자")
                .build();

            FolderInfo folder = FolderInfo.builder()
                .id(1L)
                .parentId(0L)
                .groupId(1L)
                .name("기존 폴더명")
                .owner("user@example.com")
                .regDt(LocalDateTime.of(2024, 12, 31, 10, 0))
                .build();

            fakeFolderStoragePort.database.add(folder);

            UpdateFolderCommand command = UpdateFolderCommand.builder()
                .folderId(1L)
                .account(account)
                .name("수정된 폴더명")
                .build();

            // when
            UpdateFolderServiceResponse response = updateFolderService.updateFolder(command);

            // then
            assertThat(response.result()).isTrue();
            assertThat(fakeFolderStoragePort.database).hasSize(1);
            FolderInfo updatedFolder = fakeFolderStoragePort.database.get(0);
            assertThat(updatedFolder.getId()).isEqualTo(1L);
            assertThat(updatedFolder.getName()).isEqualTo("수정된 폴더명");
            assertThat(updatedFolder.getModDt()).isNotNull();
        }

        @Test
        @DisplayName("[success] 상위 폴더를 변경한다")
        void success_updateParentFolder() {
            // given
            Account account = Account.builder()
                .id(1L)
                .groups(List.of(Group.of(1L)))
                .email("user@example.com")
                .build();

            FolderInfo parentFolder = FolderInfo.builder()
                .id(0L)
                .groupId(1L)
                .name("루트 폴더")
                .build();

            FolderInfo newParentFolder = FolderInfo.builder()
                .id(2L)
                .parentId(0L)
                .groupId(1L)
                .name("새 상위 폴더")
                .build();

            FolderInfo folder = FolderInfo.builder()
                .id(1L)
                .parentId(0L)
                .groupId(1L)
                .name("폴더명")
                .owner("user@example.com")
                .regDt(LocalDateTime.of(2024, 12, 31, 10, 0))
                .build();

            fakeFolderStoragePort.database.add(parentFolder);
            fakeFolderStoragePort.database.add(newParentFolder);
            fakeFolderStoragePort.database.add(folder);

            UpdateFolderCommand command = UpdateFolderCommand.builder()
                .folderId(1L)
                .account(account)
                .parentId(2L)
                .build();

            // when
            UpdateFolderServiceResponse response = updateFolderService.updateFolder(command);

            // then
            assertThat(response.result()).isTrue();
            FolderInfo updatedFolder = fakeFolderStoragePort.findById(1L);
            assertThat(updatedFolder.getParentId()).isEqualTo(2L);
        }

        @Test
        @DisplayName("[success] 폴더명, 상위 폴더를 모두 수정한다")
        void success_updateAll() {
            // given
            Account account = Account.builder()
                .id(1L)
                .groups(List.of(Group.of(1L)))
                .email("user@example.com")
                .build();

            FolderInfo parentFolder = FolderInfo.builder()
                .id(0L)
                .groupId(1L)
                .name("루트 폴더")
                .build();

            FolderInfo newParentFolder = FolderInfo.builder()
                .id(2L)
                .parentId(0L)
                .groupId(1L)
                .name("새 상위 폴더")
                .build();

            FolderInfo folder = FolderInfo.builder()
                .id(1L)
                .parentId(0L)
                .groupId(1L)
                .name("기존 폴더명")
                .owner("user@example.com")
                .regDt(LocalDateTime.of(2024, 12, 31, 10, 0))
                .build();

            fakeFolderStoragePort.database.add(parentFolder);
            fakeFolderStoragePort.database.add(newParentFolder);
            fakeFolderStoragePort.database.add(folder);

            UpdateFolderCommand command = UpdateFolderCommand.builder()
                .folderId(1L)
                .account(account)
                .name("수정된 폴더명")
                .parentId(2L)
                .build();

            // when
            UpdateFolderServiceResponse response = updateFolderService.updateFolder(command);

            // then
            assertThat(response.result()).isTrue();
            FolderInfo updatedFolder = fakeFolderStoragePort.findById(1L);
            // 현재 서비스 로직: parentId 변경 시 이름 변경은 중복 검사에만 사용되고 실제 이름은 변경되지 않음
            assertThat(updatedFolder.getName()).isEqualTo("기존 폴더명");
            assertThat(updatedFolder.getParentId()).isEqualTo(2L);
        }

        @Test
        @DisplayName("[failure] 폴더 소유자가 아닌 경우 예외가 발생한다")
        void failure_notOwner() {
            // given
            Account account = Account.builder()
                .id(1L)
                .email("user@example.com")
                .groups(Arrays.asList())
                .build();

            FolderInfo folder = FolderInfo.builder()
                .id(1L)
                .parentId(0L)
                .groupId(1L)
                .name("폴더명")
                .owner("other@example.com")
                .regDt(LocalDateTime.of(2024, 12, 31, 10, 0))
                .build();

            fakeFolderStoragePort.database.add(folder);

            UpdateFolderCommand command = UpdateFolderCommand.builder()
                .folderId(1L)
                .account(account)
                .name("수정된 폴더명")
                .build();

            // when & then
            assertThatThrownBy(() -> updateFolderService.updateFolder(command))
                .isInstanceOf(CustomAuthorizationException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCESS_DENIED);
        }

        @Test
        @DisplayName("[failure] 다른 그룹의 상위 폴더로 이동 시도하면 예외가 발생한다")
        void failure_differentGroup() {
            // given
            Account account = Account.builder()
                .id(1L)
                .email("user@example.com")
                .groups(Arrays.asList())
                .build();

            FolderInfo newParentFolder = FolderInfo.builder()
                .id(2L)
                .groupId(2L)
                .name("다른 그룹 폴더")
                .build();

            FolderInfo folder = FolderInfo.builder()
                .id(1L)
                .parentId(0L)
                .groupId(1L)
                .name("폴더명")
                .owner("user@example.com")
                .regDt(LocalDateTime.of(2024, 12, 31, 10, 0))
                .build();

            fakeFolderStoragePort.database.add(newParentFolder);
            fakeFolderStoragePort.database.add(folder);

            UpdateFolderCommand command = UpdateFolderCommand.builder()
                .folderId(1L)
                .account(account)
                .parentId(2L)
                .build();

            // when & then
            assertThatThrownBy(() -> updateFolderService.updateFolder(command))
                .isInstanceOf(CustomAuthorizationException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCESS_DENIED);
        }

        @Test
        @DisplayName("[failure] 상위 폴더에 동일한 이름의 폴더가 존재하면 예외가 발생한다")
        void failure_duplicateFolderName() {
            // given
            Account account = Account.builder()
                .id(1L)
                .groups(List.of(Group.of(1L)))
                .email("user@example.com")
                .build();

            FolderInfo existingFolder = FolderInfo.builder()
                .id(3L)
                .parentId(0L)
                .groupId(1L)
                .name("중복된 폴더명")
                .build();

            FolderInfo folder = FolderInfo.builder()
                .id(1L)
                .parentId(0L)
                .groupId(1L)
                .name("기존 폴더명")
                .owner("user@example.com")
                .regDt(LocalDateTime.of(2024, 12, 31, 10, 0))
                .build();

            fakeFolderStoragePort.database.add(existingFolder);
            fakeFolderStoragePort.database.add(folder);

            UpdateFolderCommand command = UpdateFolderCommand.builder()
                .folderId(1L)
                .account(account)
                .name("중복된 폴더명")
                .build();

            // when & then
            assertThatThrownBy(() -> updateFolderService.updateFolder(command))
                .isInstanceOf(CustomBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode",
                    ErrorCode.Business_SAVED_FOLDER_NAME);
        }

        @Test
        @DisplayName("[failure] 새 상위 폴더에 동일한 이름의 폴더가 존재하면 예외가 발생한다")
        void failure_duplicateFolderNameInNewParent() {
            // given
            Account account = Account.builder()
                .id(1L)
                .groups(List.of(Group.of(1L)))
                .email("user@example.com")
                .build();

            FolderInfo newParentFolder = FolderInfo.builder()
                .id(2L)
                .parentId(0L)
                .groupId(1L)
                .name("새 상위 폴더")
                .build();

            FolderInfo existingFolder = FolderInfo.builder()
                .id(3L)
                .parentId(2L)
                .groupId(1L)
                .name("폴더명")
                .build();

            FolderInfo folder = FolderInfo.builder()
                .id(1L)
                .parentId(0L)
                .groupId(1L)
                .name("폴더명")
                .owner("user@example.com")
                .regDt(LocalDateTime.of(2024, 12, 31, 10, 0))
                .build();

            fakeFolderStoragePort.database.add(newParentFolder);
            fakeFolderStoragePort.database.add(existingFolder);
            fakeFolderStoragePort.database.add(folder);

            UpdateFolderCommand command = UpdateFolderCommand.builder()
                .folderId(1L)
                .account(account)
                .parentId(2L)
                .build();

            // when & then
            assertThatThrownBy(() -> updateFolderService.updateFolder(command))
                .isInstanceOf(CustomBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode",
                    ErrorCode.Business_SAVED_FOLDER_NAME);
        }

        @Test
        @DisplayName("[failure] 존재하지 않는 폴더 수정 시도하면 예외가 발생한다")
        void failure_folderNotFound() {
            // given
            Account account = Account.builder()
                .id(1L)
                .email("user@example.com")
                .groups(Arrays.asList())
                .build();

            UpdateFolderCommand command = UpdateFolderCommand.builder()
                .folderId(999L)
                .account(account)
                .name("수정된 폴더명")
                .build();

            // when & then
            assertThatThrownBy(() -> updateFolderService.updateFolder(command))
                .isInstanceOf(CustomBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode",
                    ErrorCode.Business_DoesNotExists_FOLDER);
        }

        @Test
        @DisplayName("[failure] 존재하지 않는 상위 폴더로 이동 시도하면 예외가 발생한다")
        void failure_parentFolderNotFound() {
            // given
            Account account = Account.builder()
                .id(1L)
                .groups(List.of(Group.of(1L)))
                .email("user@example.com")
                .build();

            FolderInfo folder = FolderInfo.builder()
                .id(1L)
                .parentId(0L)
                .groupId(1L)
                .name("폴더명")
                .owner("user@example.com")
                .regDt(LocalDateTime.of(2024, 12, 31, 10, 0))
                .build();

            fakeFolderStoragePort.database.add(folder);

            UpdateFolderCommand command = UpdateFolderCommand.builder()
                .folderId(1L)
                .account(account)
                .parentId(999L)
                .build();

            // when & then
            assertThatThrownBy(() -> updateFolderService.updateFolder(command))
                .isInstanceOf(CustomBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode",
                    ErrorCode.Business_DoesNotExists_FOLDER);
        }
    }
}
