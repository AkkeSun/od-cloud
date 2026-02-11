package com.odcloud.application.file.service.delete_folder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.FileInfo;
import com.odcloud.domain.model.FolderInfo;
import com.odcloud.domain.model.Group;
import com.odcloud.fakeClass.FakeFilePort;
import com.odcloud.fakeClass.FakeFileStoragePort;
import com.odcloud.fakeClass.FakeFolderStoragePort;
import com.odcloud.fakeClass.FakeRedisStoragePort;
import com.odcloud.infrastructure.exception.CustomAuthorizationException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class DeleteFolderServiceTest {

    private FakeFileStoragePort fakeFileStoragePort;
    private FakeFolderStoragePort fakeFolderStoragePort;
    private com.odcloud.fakeClass.FakeGroupStoragePort fakeGroupStoragePort;
    private DeleteFolderService deleteFolderService;

    @BeforeEach
    void setUp() {
        fakeFileStoragePort = new FakeFileStoragePort();
        fakeFolderStoragePort = new FakeFolderStoragePort();
        fakeGroupStoragePort = new com.odcloud.fakeClass.FakeGroupStoragePort();
        deleteFolderService = new DeleteFolderService(
            new FakeFilePort(),
            fakeGroupStoragePort,
            fakeFileStoragePort,
            fakeFolderStoragePort,
            new FakeRedisStoragePort()
        );
    }

    @Nested
    @DisplayName("[deleteFolder] 폴더 삭제")
    class Describe_deleteFolder {

        @Test
        @DisplayName("[success] 빈 폴더를 삭제한다")
        void success_emptyFolder() {
            // given
            String ownerEmail = "owner@test.com";
            FolderInfo folder = FolderInfo.builder()
                .id(1L)
                .parentId(null)
                .groupId(1L)
                .owner(ownerEmail)
                .name("Empty Folder")
                .build();
            fakeFolderStoragePort.database.add(folder);

            Account ownerAccount = Account.builder()
                .email(ownerEmail)
                .groups(List.of(Group.of(1L)))
                .build();

            // when
            DeleteFolderServiceResponse response = deleteFolderService.deleteFolder(ownerAccount,
                1L);

            // then
            assertThat(response.result()).isTrue();
            assertThat(fakeFolderStoragePort.database).isEmpty();
        }

        @Test
        @DisplayName("[success] 파일이 있는 폴더를 삭제한다")
        void success_folderWithFiles() {
            // given
            String ownerEmail = "owner@test.com";

            Group group = Group.builder()
                .id(1L)
                .name("Test Group")
                .ownerEmail(ownerEmail)
                .storageTotal(1000L)
                .storageUsed(300L)
                .build();
            fakeGroupStoragePort.groupDatabase.add(group);

            FolderInfo folder = FolderInfo.builder()
                .id(1L)
                .parentId(null)
                .groupId(1L)
                .owner(ownerEmail)
                .name("Folder With Files")
                .build();
            fakeFolderStoragePort.database.add(folder);

            FileInfo file1 = FileInfo.builder()
                .id(1L)
                .folderId(1L)
                .fileName("test1.txt")
                .fileLoc("/folder/test1.txt")
                .fileSize(100L)
                .build();
            FileInfo file2 = FileInfo.builder()
                .id(2L)
                .folderId(1L)
                .fileName("test2.txt")
                .fileLoc("/folder/test2.txt")
                .fileSize(200L)
                .build();
            fakeFileStoragePort.database.addAll(List.of(file1, file2));

            Account ownerAccount = Account.builder()
                .email(ownerEmail)
                .groups(List.of(Group.of(1L)))
                .build();

            // when
            DeleteFolderServiceResponse response = deleteFolderService.deleteFolder(ownerAccount,
                1L);

            // then
            assertThat(response.result()).isTrue();
            assertThat(fakeFolderStoragePort.database).isEmpty();
            assertThat(fakeFileStoragePort.database).isEmpty();
        }

        @Test
        @DisplayName("[success] 하위 폴더가 있는 폴더를 재귀적으로 삭제한다")
        void success_folderWithSubFolders() {
            // given
            String ownerEmail = "owner@test.com";

            FolderInfo parentFolder = FolderInfo.builder()
                .id(1L)
                .parentId(null)
                .groupId(1L)
                .owner(ownerEmail)
                .name("Parent Folder")
                .build();

            FolderInfo childFolder1 = FolderInfo.builder()
                .id(2L)
                .parentId(1L)
                .groupId(1L)
                .owner(ownerEmail)
                .name("Child Folder 1")
                .build();

            FolderInfo childFolder2 = FolderInfo.builder()
                .id(3L)
                .parentId(1L)
                .groupId(1L)
                .owner(ownerEmail)
                .name("Child Folder 2")
                .build();

            FolderInfo grandChildFolder = FolderInfo.builder()
                .id(4L)
                .parentId(2L)
                .groupId(1L)
                .owner(ownerEmail)
                .name("Grand Child Folder")
                .build();

            fakeFolderStoragePort.database.addAll(
                List.of(parentFolder, childFolder1, childFolder2, grandChildFolder));

            Account ownerAccount = Account.builder()
                .email(ownerEmail)
                .groups(List.of(Group.of(1L)))
                .build();

            // when
            DeleteFolderServiceResponse response = deleteFolderService.deleteFolder(ownerAccount,
                1L);

            // then
            assertThat(response.result()).isTrue();
            assertThat(fakeFolderStoragePort.database).isEmpty();
        }

        @Test
        @DisplayName("[success] 하위 폴더와 파일이 모두 있는 폴더를 삭제한다")
        void success_folderWithSubFoldersAndFiles() {
            // given
            String ownerEmail = "owner@test.com";

            Group group = Group.builder()
                .id(1L)
                .name("Test Group")
                .ownerEmail(ownerEmail)
                .storageTotal(1000L)
                .storageUsed(300L)
                .build();
            fakeGroupStoragePort.groupDatabase.add(group);

            FolderInfo parentFolder = FolderInfo.builder()
                .id(1L)
                .parentId(null)
                .groupId(1L)
                .owner(ownerEmail)
                .name("Parent Folder")
                .build();

            FolderInfo childFolder = FolderInfo.builder()
                .id(2L)
                .parentId(1L)
                .groupId(1L)
                .owner(ownerEmail)
                .name("Child Folder")
                .build();

            fakeFolderStoragePort.database.addAll(List.of(parentFolder, childFolder));

            FileInfo fileInParent = FileInfo.builder()
                .id(1L)
                .folderId(1L)
                .fileName("parent.txt")
                .fileLoc("/parent/parent.txt")
                .fileSize(100L)
                .build();

            FileInfo fileInChild = FileInfo.builder()
                .id(2L)
                .folderId(2L)
                .fileName("child.txt")
                .fileLoc("/parent/child/child.txt")
                .fileSize(200L)
                .build();

            fakeFileStoragePort.database.addAll(List.of(fileInParent, fileInChild));

            Account ownerAccount = Account.builder()
                .email(ownerEmail)
                .groups(List.of(Group.of(1L)))
                .build();

            // when
            DeleteFolderServiceResponse response = deleteFolderService.deleteFolder(ownerAccount,
                1L);

            // then
            assertThat(response.result()).isTrue();
            assertThat(fakeFolderStoragePort.database).isEmpty();
            assertThat(fakeFileStoragePort.database).isEmpty();
        }

        @Test
        @DisplayName("[failure] 소유자가 아닌 사용자가 폴더 삭제를 시도하면 예외가 발생한다")
        void failure_notOwner() {
            // given
            String ownerEmail = "owner@test.com";
            String otherUserEmail = "other@test.com";

            FolderInfo folder = FolderInfo.builder()
                .id(1L)
                .parentId(null)
                .groupId(1L)
                .owner(ownerEmail)
                .name("Private Folder")
                .build();
            fakeFolderStoragePort.database.add(folder);

            Account otherAccount = Account.builder()
                .email(otherUserEmail)
                .groups(List.of(Group.of(2L)))
                .build();

            // when & then
            assertThatThrownBy(() -> deleteFolderService.deleteFolder(otherAccount, 1L))
                .isInstanceOf(CustomAuthorizationException.class);

            assertThat(fakeFolderStoragePort.database).hasSize(1);
        }
    }
}
