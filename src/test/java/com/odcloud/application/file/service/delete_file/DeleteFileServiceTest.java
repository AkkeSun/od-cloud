package com.odcloud.application.file.service.delete_file;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.application.file.port.in.command.DeleteFileCommand;
import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.FileInfo;
import com.odcloud.domain.model.FolderInfo;
import com.odcloud.domain.model.Group;
import com.odcloud.fakeClass.FakeFilePort;
import com.odcloud.fakeClass.FakeFileStoragePort;
import com.odcloud.fakeClass.FakeFolderStoragePort;
import com.odcloud.fakeClass.FakeGroupStoragePort;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class DeleteFileServiceTest {

    private FakeFileStoragePort fakeFileStoragePort;
    private FakeFolderStoragePort fakeFolderStoragePort;
    private FakeGroupStoragePort fakeGroupStoragePort;
    private DeleteFileService deleteFileService;

    @BeforeEach
    void setUp() {
        fakeFileStoragePort = new FakeFileStoragePort();
        fakeFolderStoragePort = new FakeFolderStoragePort();
        fakeGroupStoragePort = new FakeGroupStoragePort();
        deleteFileService = new DeleteFileService(
            new FakeFilePort(),
            fakeFileStoragePort,
            fakeFolderStoragePort,
            fakeGroupStoragePort
        );
    }

    @Nested
    @DisplayName("[deleteFile] 파일 삭제")
    class Describe_deleteFile {

        @Test
        @DisplayName("[partial_success] 일부 파일만 삭제 성공한다")
        void partialSuccess_someFilesFailDueToPermission() {
            // given
            String ownerEmail = "owner@test.com";
            String otherUserEmail = "other@test.com";

            FolderInfo privateFolder = FolderInfo.builder()
                .id(1L)
                .groupId(null)
                .owner(ownerEmail)
                .name("Private Folder")
                .path("/private")
                .build();
            fakeFolderStoragePort.database.add(privateFolder);

            FileInfo file1 = FileInfo.builder()
                .id(1L)
                .folderId(1L)
                .fileName("test1.txt")
                .fileLoc("/storage/test1.txt")
                .build();
            FileInfo file2 = FileInfo.builder()
                .id(2L)
                .folderId(1L)
                .fileName("test2.txt")
                .fileLoc("/storage/test2.txt")
                .build();
            fakeFileStoragePort.database.addAll(List.of(file1, file2));

            Account otherAccount = Account.builder()
                .email(otherUserEmail)
                .groups(List.of())
                .build();

            DeleteFileCommand command = DeleteFileCommand.builder()
                .account(otherAccount)
                .fileIds(List.of(1L, 2L))
                .build();

            // when
            DeleteFileServiceResponse response = deleteFileService.deleteFile(command);

            // then
            assertThat(response.result()).isFalse();
            assertThat(response.logs()).hasSize(2);
            assertThat(
                response.logs().stream().allMatch(log -> log.errorMessage() != null)).isTrue();
            assertThat(fakeFileStoragePort.database).hasSize(2); // 모두 삭제 실패
        }

        @Test
        @DisplayName("[success] PUBLIC 폴더에 접근 가능한 사용자가 파일을 삭제한다")
        void success_publicFolder_hasAccess() {
            // given
            String groupId = "group-1";
            FolderInfo publicFolder = FolderInfo.builder()
                .id(1L)
                .groupId(groupId)
                .owner("owner@test.com")
                .name("Public Folder")
                .path("/public")
                .build();
            fakeFolderStoragePort.database.add(publicFolder);

            FileInfo file = FileInfo.builder()
                .id(1L)
                .folderId(1L)
                .fileName("test.txt")
                .fileLoc("/storage/test.txt")
                .build();
            fakeFileStoragePort.database.add(file);

            Account userWithAccess = Account.builder()
                .email("user@test.com")
                .groups(List.of(Group.of(groupId)))
                .build();

            DeleteFileCommand command = DeleteFileCommand.builder()
                .account(userWithAccess)
                .fileIds(List.of(1L))
                .build();

            // when
            DeleteFileServiceResponse response = deleteFileService.deleteFile(command);

            // then
            assertThat(response.result()).isTrue();
            assertThat(response.logs()).hasSize(1);
            assertThat(response.logs().get(0).errorMessage()).isNull();
            assertThat(fakeFileStoragePort.database).isEmpty();
        }

        @Test
        @DisplayName("[failure] PUBLIC 폴더에 접근 권한이 없는 사용자가 파일 삭제 시도하면 실패한다")
        void failure_publicFolder_noAccess() {
            // given
            String groupId = "group-1";
            FolderInfo publicFolder = FolderInfo.builder()
                .id(1L)
                .groupId(groupId)
                .owner("owner@test.com")
                .name("Public Folder")
                .path("/public")
                .build();
            fakeFolderStoragePort.database.add(publicFolder);

            FileInfo file = FileInfo.builder()
                .id(1L)
                .folderId(1L)
                .fileName("test.txt")
                .fileLoc("/storage/test.txt")
                .build();
            fakeFileStoragePort.database.add(file);

            Account userWithoutAccess = Account.builder()
                .email("user@test.com")
                .groups(List.of(Group.of("group-2")))
                .build();

            DeleteFileCommand command = DeleteFileCommand.builder()
                .account(userWithoutAccess)
                .fileIds(List.of(1L))
                .build();

            // when
            DeleteFileServiceResponse response = deleteFileService.deleteFile(command);

            // then
            assertThat(response.result()).isFalse();
            assertThat(response.logs()).hasSize(1);
            assertThat(response.logs().get(0).errorMessage()).isNotNull();
            assertThat(fakeFileStoragePort.database).hasSize(1);
        }

        @Test
        @DisplayName("[failure] 존재하지 않는 파일 삭제 시도하면 실패한다")
        void failure_fileNotFound() {
            // given
            Account account = Account.builder()
                .email("user@test.com")
                .groups(List.of())
                .build();

            DeleteFileCommand command = DeleteFileCommand.builder()
                .account(account)
                .fileIds(List.of(999L))
                .build();

            // when
            DeleteFileServiceResponse response = deleteFileService.deleteFile(command);

            // then
            assertThat(response.result()).isFalse();
            assertThat(response.logs()).hasSize(1);
            assertThat(response.logs().get(0).errorMessage()).isNotNull();
        }

        @Test
        @DisplayName("[success] 파일 삭제 시 그룹 스토리지 용량이 감소한다")
        void success_storageDecreased() {
            // given
            String groupId = "test-group";
            Group group = Group.builder()
                .id(groupId)
                .name("Test Group")
                .storageUsed(1000L)
                .storageTotal(3221225472L)
                .build();
            fakeGroupStoragePort.groupDatabase.add(group);

            FolderInfo folder = FolderInfo.builder()
                .id(1L)
                .groupId(groupId)
                .owner("owner@test.com")
                .name("Test Folder")
                .path("/test")
                .build();
            fakeFolderStoragePort.database.add(folder);

            FileInfo file = FileInfo.builder()
                .id(1L)
                .folderId(1L)
                .fileName("test.txt")
                .fileLoc("/storage/test.txt")
                .fileSize(300L)
                .build();
            fakeFileStoragePort.database.add(file);

            Account account = Account.builder()
                .email("user@test.com")
                .groups(List.of(Group.of(groupId)))
                .build();

            DeleteFileCommand command = DeleteFileCommand.builder()
                .account(account)
                .fileIds(List.of(1L))
                .build();

            // when
            DeleteFileServiceResponse response = deleteFileService.deleteFile(command);

            // then
            assertThat(response.result()).isTrue();
            assertThat(fakeFileStoragePort.database).isEmpty();

            Group updatedGroup = fakeGroupStoragePort.groupDatabase.get(0);
            assertThat(updatedGroup.getStorageUsed()).isEqualTo(700L);
        }

        @Test
        @DisplayName("[success] 여러 파일 삭제 시 그룹 스토리지 용량이 각각 감소한다")
        void success_multipleFilesStorageDecreased() {
            // given
            String groupId = "test-group";
            Group group = Group.builder()
                .id(groupId)
                .name("Test Group")
                .storageUsed(1500L)
                .storageTotal(3221225472L)
                .build();
            fakeGroupStoragePort.groupDatabase.add(group);

            FolderInfo folder = FolderInfo.builder()
                .id(1L)
                .groupId(groupId)
                .owner("owner@test.com")
                .name("Test Folder")
                .path("/test")
                .build();
            fakeFolderStoragePort.database.add(folder);

            FileInfo file1 = FileInfo.builder()
                .id(1L)
                .folderId(1L)
                .fileName("test1.txt")
                .fileLoc("/storage/test1.txt")
                .fileSize(300L)
                .build();
            FileInfo file2 = FileInfo.builder()
                .id(2L)
                .folderId(1L)
                .fileName("test2.txt")
                .fileLoc("/storage/test2.txt")
                .fileSize(200L)
                .build();
            fakeFileStoragePort.database.addAll(List.of(file1, file2));

            Account account = Account.builder()
                .email("user@test.com")
                .groups(List.of(Group.of(groupId)))
                .build();

            DeleteFileCommand command = DeleteFileCommand.builder()
                .account(account)
                .fileIds(List.of(1L, 2L))
                .build();

            // when
            DeleteFileServiceResponse response = deleteFileService.deleteFile(command);

            // then
            assertThat(response.result()).isTrue();
            assertThat(fakeFileStoragePort.database).isEmpty();

            Group updatedGroup = fakeGroupStoragePort.groupDatabase.get(0);
            assertThat(updatedGroup.getStorageUsed()).isEqualTo(1000L);
        }

        @Test
        @DisplayName("[success] 파일 크기가 null인 경우에도 삭제가 성공한다")
        void success_fileSizeNull() {
            // given
            String groupId = "test-group";
            Group group = Group.builder()
                .id(groupId)
                .name("Test Group")
                .storageUsed(1000L)
                .storageTotal(3221225472L)
                .build();
            fakeGroupStoragePort.groupDatabase.add(group);

            FolderInfo folder = FolderInfo.builder()
                .id(1L)
                .groupId(groupId)
                .owner("owner@test.com")
                .name("Test Folder")
                .path("/test")
                .build();
            fakeFolderStoragePort.database.add(folder);

            FileInfo file = FileInfo.builder()
                .id(1L)
                .folderId(1L)
                .fileName("test.txt")
                .fileLoc("/storage/test.txt")
                .fileSize(null)
                .build();
            fakeFileStoragePort.database.add(file);

            Account account = Account.builder()
                .email("user@test.com")
                .groups(List.of(Group.of(groupId)))
                .build();

            DeleteFileCommand command = DeleteFileCommand.builder()
                .account(account)
                .fileIds(List.of(1L))
                .build();

            // when
            DeleteFileServiceResponse response = deleteFileService.deleteFile(command);

            // then
            assertThat(response.result()).isTrue();
            assertThat(fakeFileStoragePort.database).isEmpty();

            Group updatedGroup = fakeGroupStoragePort.groupDatabase.get(0);
            assertThat(updatedGroup.getStorageUsed()).isEqualTo(1000L);
        }
    }
}
