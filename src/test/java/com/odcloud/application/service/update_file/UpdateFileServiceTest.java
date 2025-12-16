package com.odcloud.application.service.update_file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.odcloud.application.port.in.command.UpdateFileCommand;
import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.FileInfo;
import com.odcloud.domain.model.FolderInfo;
import com.odcloud.fakeClass.FakeFilePort;
import com.odcloud.fakeClass.FakeFileStoragePort;
import com.odcloud.fakeClass.FakeFolderStoragePort;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UpdateFileServiceTest {

    private FakeFileStoragePort fakeFileStoragePort;
    private FakeFolderStoragePort fakeFolderStoragePort;
    private FakeFilePort fakeFilePort;
    private UpdateFileService updateFileService;

    @BeforeEach
    void setUp() {
        fakeFileStoragePort = new FakeFileStoragePort();
        fakeFolderStoragePort = new FakeFolderStoragePort();
        fakeFilePort = new FakeFilePort();
        updateFileService = new UpdateFileService(
            fakeFileStoragePort,
            fakeFolderStoragePort,
            fakeFilePort
        );
    }

    @Nested
    @DisplayName("[update] 파일 정보 수정")
    class Describe_update {

        @Test
        @DisplayName("[success] 파일명만 변경한다")
        void success_updateFileNameOnly() {
            // given
            FolderInfo folder = FolderInfo.builder()
                .id(1L)
                .groupId("test-group")
                .name("Test Folder")
                .owner("test@example.com")
                .path("/test-group/folder1")
                .build();
            fakeFolderStoragePort.database.add(folder);

            FileInfo file = FileInfo.builder()
                .id(1L)
                .folderId(1L)
                .fileName("old-name.txt")
                .fileLoc("/test-group/folder1/abc123_20241120.txt")
                .regDt(LocalDateTime.now())
                .build();
            fakeFileStoragePort.database.add(file);

            Account account = Account.builder()
                .email("test@example.com")
                .build();

            UpdateFileCommand command = UpdateFileCommand.builder()
                .fileId(1L)
                .account(account)
                .fileName("new-name.txt")
                .build();

            // when
            UpdateFileServiceResponse response = updateFileService.update(command);

            // then
            assertThat(response.result()).isTrue();
            FileInfo updatedFile = fakeFileStoragePort.findById(1L);
            assertThat(updatedFile.getFileName()).isEqualTo("new-name.txt");
            assertThat(updatedFile.getFileLoc()).isEqualTo(
                "/test-group/folder1/abc123_20241120.txt");
            assertThat(updatedFile.getFolderId()).isEqualTo(1L);
            assertThat(fakeFilePort.moveFileCallCount).isZero();
        }

        @Test
        @DisplayName("[success] 폴더만 변경한다")
        void success_updateFolderOnly() {
            // given
            FolderInfo sourceFolder = FolderInfo.builder()
                .id(1L)
                .groupId("test-group")
                .name("Source Folder")
                .owner("test@example.com")
                .path("/test-group/folder1")
                .build();
            fakeFolderStoragePort.database.add(sourceFolder);

            FolderInfo targetFolder = FolderInfo.builder()
                .id(2L)
                .groupId("test-group")
                .name("Target Folder")
                .owner("test@example.com")
                .path("/test-group/folder2")
                .build();
            fakeFolderStoragePort.database.add(targetFolder);

            FileInfo file = FileInfo.builder()
                .id(1L)
                .folderId(1L)
                .fileName("test.txt")
                .fileLoc("/test-group/folder1/abc123_20241120.txt")
                .regDt(LocalDateTime.now())
                .build();
            fakeFileStoragePort.database.add(file);

            Account account = Account.builder()
                .email("test@example.com")
                .build();

            UpdateFileCommand command = UpdateFileCommand.builder()
                .fileId(1L)
                .account(account)
                .folderId(2L)
                .build();

            // when
            UpdateFileServiceResponse response = updateFileService.update(command);

            // then
            assertThat(response.result()).isTrue();
            FileInfo updatedFile = fakeFileStoragePort.findById(1L);
            assertThat(updatedFile.getFileName()).isEqualTo("test.txt");
            assertThat(updatedFile.getFolderId()).isEqualTo(2L);
            assertThat(updatedFile.getFileLoc()).startsWith("/test-group/folder2/");
            assertThat(fakeFilePort.moveFileCallCount).isEqualTo(1);
        }

        @Test
        @DisplayName("[success] 파일명과 폴더를 동시에 변경한다")
        void success_updateBothFileNameAndFolder() {
            // given
            FolderInfo sourceFolder = FolderInfo.builder()
                .id(1L)
                .groupId("test-group")
                .name("Source Folder")
                .owner("test@example.com")
                .path("/test-group/folder1")
                .build();
            fakeFolderStoragePort.database.add(sourceFolder);

            FolderInfo targetFolder = FolderInfo.builder()
                .id(2L)
                .groupId("test-group")
                .name("Target Folder")
                .owner("test@example.com")
                .path("/test-group/folder2")
                .build();
            fakeFolderStoragePort.database.add(targetFolder);

            FileInfo file = FileInfo.builder()
                .id(1L)
                .folderId(1L)
                .fileName("old-name.txt")
                .fileLoc("/test-group/folder1/abc123_20241120.txt")
                .regDt(LocalDateTime.now())
                .build();
            fakeFileStoragePort.database.add(file);

            Account account = Account.builder()
                .email("test@example.com")
                .build();

            UpdateFileCommand command = UpdateFileCommand.builder()
                .fileId(1L)
                .account(account)
                .fileName("new-name.txt")
                .folderId(2L)
                .build();

            // when
            UpdateFileServiceResponse response = updateFileService.update(command);

            // then
            assertThat(response.result()).isTrue();
            FileInfo updatedFile = fakeFileStoragePort.findById(1L);
            assertThat(updatedFile.getFileName()).isEqualTo("new-name.txt");
            assertThat(updatedFile.getFolderId()).isEqualTo(2L);
            assertThat(updatedFile.getFileLoc()).startsWith("/test-group/folder2/");
            assertThat(fakeFilePort.moveFileCallCount).isEqualTo(1);
        }

        @Test
        @DisplayName("[failure] 존재하지 않는 파일을 수정하려고 하면 예외가 발생한다")
        void failure_fileNotFound() {
            // given
            Account account = Account.builder()
                .email("test@example.com")
                .build();

            UpdateFileCommand command = UpdateFileCommand.builder()
                .fileId(999L)
                .account(account)
                .fileName("new-name.txt")
                .build();

            // when & then
            assertThatThrownBy(() -> updateFileService.update(command))
                .isInstanceOf(CustomBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.Business_DoesNotExists_FILE);
        }

        @Test
        @DisplayName("[failure] 동일한 폴더에 같은 파일명이 이미 존재하면 예외가 발생한다")
        void failure_duplicateFileNameInSameFolder() {
            // given
            FolderInfo folder = FolderInfo.builder()
                .id(1L)
                .groupId("test-group")
                .name("Test Folder")
                .owner("test@example.com")
                .path("/test-group/folder1")
                .build();
            fakeFolderStoragePort.database.add(folder);

            FileInfo existingFile = FileInfo.builder()
                .id(1L)
                .folderId(1L)
                .fileName("existing.txt")
                .fileLoc("/test-group/folder1/abc123_20241120.txt")
                .regDt(LocalDateTime.now())
                .build();
            fakeFileStoragePort.database.add(existingFile);

            FileInfo targetFile = FileInfo.builder()
                .id(2L)
                .folderId(1L)
                .fileName("target.txt")
                .fileLoc("/test-group/folder1/def456_20241120.txt")
                .regDt(LocalDateTime.now())
                .build();
            fakeFileStoragePort.database.add(targetFile);

            Account account = Account.builder()
                .email("test@example.com")
                .build();

            UpdateFileCommand command = UpdateFileCommand.builder()
                .fileId(2L)
                .account(account)
                .fileName("existing.txt")
                .build();

            // when & then
            assertThatThrownBy(() -> updateFileService.update(command))
                .isInstanceOf(CustomBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.Business_SAVED_FILE_NAME);
        }

        @Test
        @DisplayName("[failure] 대상 폴더에 같은 파일명이 이미 존재하면 예외가 발생한다")
        void failure_duplicateFileNameInTargetFolder() {
            // given
            FolderInfo sourceFolder = FolderInfo.builder()
                .id(1L)
                .groupId("test-group")
                .name("Source Folder")
                .owner("test@example.com")
                .path("/test-group/folder1")
                .build();
            fakeFolderStoragePort.database.add(sourceFolder);

            FolderInfo targetFolder = FolderInfo.builder()
                .id(2L)
                .groupId("test-group")
                .name("Target Folder")
                .owner("test@example.com")
                .path("/test-group/folder2")
                .build();
            fakeFolderStoragePort.database.add(targetFolder);

            FileInfo existingFileInTargetFolder = FileInfo.builder()
                .id(1L)
                .folderId(2L)
                .fileName("duplicate.txt")
                .fileLoc("/test-group/folder2/abc123_20241120.txt")
                .regDt(LocalDateTime.now())
                .build();
            fakeFileStoragePort.database.add(existingFileInTargetFolder);

            FileInfo fileToMove = FileInfo.builder()
                .id(2L)
                .folderId(1L)
                .fileName("duplicate.txt")
                .fileLoc("/test-group/folder1/def456_20241120.txt")
                .regDt(LocalDateTime.now())
                .build();
            fakeFileStoragePort.database.add(fileToMove);

            Account account = Account.builder()
                .email("test@example.com")
                .build();

            UpdateFileCommand command = UpdateFileCommand.builder()
                .fileId(2L)
                .account(account)
                .folderId(2L)
                .build();

            // when & then
            assertThatThrownBy(() -> updateFileService.update(command))
                .isInstanceOf(CustomBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.Business_SAVED_FILE_NAME);
        }
        
        @Test
        @DisplayName("[success] PRIVATE 폴더의 소유자는 접근할 수 있다")
        void success_ownerCanAccessPrivateFolder() {
            // given
            FolderInfo sourceFolder = FolderInfo.builder()
                .id(1L)
                .groupId("test-group")
                .name("Source Folder")
                .owner("owner@example.com")
                .path("/test-group/folder1")
                .build();
            fakeFolderStoragePort.database.add(sourceFolder);

            FolderInfo targetFolder = FolderInfo.builder()
                .id(2L)
                .groupId("test-group")
                .name("Private Folder")
                .owner("owner@example.com")
                .path("/test-group/folder2")
                .build();
            fakeFolderStoragePort.database.add(targetFolder);

            FileInfo file = FileInfo.builder()
                .id(1L)
                .folderId(1L)
                .fileName("test.txt")
                .fileLoc("/test-group/folder1/abc123_20241120.txt")
                .regDt(LocalDateTime.now())
                .build();
            fakeFileStoragePort.database.add(file);

            Account account = Account.builder()
                .email("owner@example.com")
                .build();

            UpdateFileCommand command = UpdateFileCommand.builder()
                .fileId(1L)
                .account(account)
                .folderId(2L)
                .build();

            // when
            UpdateFileServiceResponse response = updateFileService.update(command);

            // then
            assertThat(response.result()).isTrue();
            FileInfo updatedFile = fakeFileStoragePort.findById(1L);
            assertThat(updatedFile.getFolderId()).isEqualTo(2L);
        }

        @Test
        @DisplayName("[failure] 존재하지 않는 대상 폴더를 지정하면 예외가 발생한다")
        void failure_targetFolderNotFound() {
            // given
            FolderInfo folder = FolderInfo.builder()
                .id(1L)
                .groupId("test-group")
                .name("Test Folder")
                .owner("test@example.com")
                .path("/test-group/folder1")
                .build();
            fakeFolderStoragePort.database.add(folder);

            FileInfo file = FileInfo.builder()
                .id(1L)
                .folderId(1L)
                .fileName("test.txt")
                .fileLoc("/test-group/folder1/abc123_20241120.txt")
                .regDt(LocalDateTime.now())
                .build();
            fakeFileStoragePort.database.add(file);

            Account account = Account.builder()
                .email("test@example.com")
                .build();

            UpdateFileCommand command = UpdateFileCommand.builder()
                .fileId(1L)
                .account(account)
                .folderId(999L)
                .build();

            // when & then
            assertThatThrownBy(() -> updateFileService.update(command))
                .isInstanceOf(CustomBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.Business_DoesNotExists_FOLDER);
        }
    }
}
