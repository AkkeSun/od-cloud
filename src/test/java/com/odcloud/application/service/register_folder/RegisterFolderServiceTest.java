package com.odcloud.application.service.register_folder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.odcloud.application.port.in.command.RegisterFolderCommand;
import com.odcloud.domain.model.Folder;
import com.odcloud.fakeClass.FakeFileUploadPort;
import com.odcloud.fakeClass.FakeFolderStoragePort;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RegisterFolderServiceTest {

    private FakeFolderStoragePort fakeFolderStoragePort;
    private FakeFileUploadPort fakeFileUploadPort;
    private RegisterFolderService registerFolderService;

    @BeforeEach
    void setUp() {
        fakeFolderStoragePort = new FakeFolderStoragePort();
        fakeFileUploadPort = new FakeFileUploadPort();
        registerFolderService = new RegisterFolderService(
            fakeFolderStoragePort,
            fakeFileUploadPort
        );
    }

    @Nested
    @DisplayName("[createFolder] 폴더 생성")
    class Describe_createFolder {

        @Test
        @DisplayName("[success] 정상적으로 폴더를 생성한다")
        void success() {
            // given
            Folder parentFolder = Folder.builder()
                .id(1L)
                .groupId("test-group")
                .name("Parent Folder")
                .path("/test-group")
                .isRoot(true)
                .build();
            fakeFolderStoragePort.database.add(parentFolder);

            RegisterFolderCommand command = RegisterFolderCommand.builder()
                .parentId(1L)
                .groupId("test-group")
                .name("New Folder")
                .owner("owner@example.com")
                .accessLevel("PUBLIC")
                .build();

            // when
            RegisterFolderServiceResponse response = registerFolderService.createFolder(command);

            // then
            assertThat(response).isNotNull();
            assertThat(fakeFolderStoragePort.database).hasSize(2);
            assertThat(fakeFolderStoragePort.database.get(1).getName()).isEqualTo("New Folder");
            assertThat(fakeFolderStoragePort.database.get(1).getParentId()).isEqualTo(1L);
            assertThat(fakeFolderStoragePort.database.get(1).getPath()).startsWith("/test-group/");
            assertThat(fakeFileUploadPort.createdFolders).hasSize(1);
        }

        @Test
        @DisplayName("[failure] 같은 이름의 폴더가 이미 존재하면 예외가 발생한다")
        void failure_duplicateFolderName() {
            // given
            Folder parentFolder = Folder.builder()
                .id(1L)
                .groupId("test-group")
                .name("Parent Folder")
                .path("/test-group")
                .isRoot(true)
                .build();
            fakeFolderStoragePort.database.add(parentFolder);

            Folder existingFolder = Folder.builder()
                .id(2L)
                .parentId(1L)
                .groupId("test-group")
                .name("Existing Folder")
                .path("/test-group/existing")
                .isRoot(false)
                .build();
            fakeFolderStoragePort.database.add(existingFolder);

            RegisterFolderCommand command = RegisterFolderCommand.builder()
                .parentId(1L)
                .groupId("test-group")
                .name("Existing Folder")
                .owner("owner@example.com")
                .accessLevel("PUBLIC")
                .build();

            // when & then
            assertThatThrownBy(() -> registerFolderService.createFolder(command))
                .isInstanceOf(CustomBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.Business_SAVED_FOLDER_NAME);

            assertThat(fakeFolderStoragePort.database).hasSize(2);
            assertThat(fakeFileUploadPort.createdFolders).isEmpty();
        }

        @Test
        @DisplayName("[failure] 존재하지 않는 부모 폴더에 폴더 생성 시도하면 예외가 발생한다")
        void failure_nonExistentParentFolder() {
            // given
            RegisterFolderCommand command = RegisterFolderCommand.builder()
                .parentId(999L)
                .groupId("test-group")
                .name("New Folder")
                .owner("owner@example.com")
                .accessLevel("PUBLIC")
                .build();

            // when & then
            assertThatThrownBy(() -> registerFolderService.createFolder(command))
                .isInstanceOf(CustomBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.Business_DoesNotExists_FOLDER);

            assertThat(fakeFolderStoragePort.database).isEmpty();
            assertThat(fakeFileUploadPort.createdFolders).isEmpty();
        }

        @Test
        @DisplayName("[success] 다양한 폴더 이름으로 폴더를 생성한다")
        void success_variousFolderNames() {
            // given
            Folder parentFolder = Folder.builder()
                .id(1L)
                .groupId("test-group")
                .name("Parent Folder")
                .path("/test-group")
                .isRoot(true)
                .build();
            fakeFolderStoragePort.database.add(parentFolder);

            RegisterFolderCommand command = RegisterFolderCommand.builder()
                .parentId(1L)
                .groupId("test-group")
                .name("한글 폴더 이름")
                .owner("owner@example.com")
                .accessLevel("PRIVATE")
                .build();

            // when
            RegisterFolderServiceResponse response = registerFolderService.createFolder(command);

            // then
            assertThat(response).isNotNull();
            assertThat(fakeFolderStoragePort.database).hasSize(2);
            assertThat(fakeFolderStoragePort.database.get(1).getName()).isEqualTo("한글 폴더 이름");
            assertThat(fakeFolderStoragePort.database.get(1).getAccessLevel()).isEqualTo("PRIVATE");
        }

        @Test
        @DisplayName("[success] 여러 단계의 하위 폴더를 생성할 수 있다")
        void success_nestedFolders() {
            // given
            Folder rootFolder = Folder.builder()
                .id(1L)
                .groupId("test-group")
                .name("Root")
                .path("/test-group")
                .isRoot(true)
                .build();
            fakeFolderStoragePort.database.add(rootFolder);

            // Create first level subfolder
            RegisterFolderCommand command1 = RegisterFolderCommand.builder()
                .parentId(1L)
                .groupId("test-group")
                .name("Level 1")
                .owner("owner@example.com")
                .accessLevel("PUBLIC")
                .build();

            RegisterFolderServiceResponse response1 = registerFolderService.createFolder(command1);
            assertThat(response1).isNotNull();

            // Create second level subfolder
            Folder level1Folder = fakeFolderStoragePort.database.get(1);
            RegisterFolderCommand command2 = RegisterFolderCommand.builder()
                .parentId(level1Folder.getId())
                .groupId("test-group")
                .name("Level 2")
                .owner("owner@example.com")
                .accessLevel("PUBLIC")
                .build();

            // when
            RegisterFolderServiceResponse response2 = registerFolderService.createFolder(command2);

            // then
            assertThat(response2).isNotNull();
            assertThat(fakeFolderStoragePort.database).hasSize(3);
            assertThat(fakeFolderStoragePort.database.get(2).getName()).isEqualTo("Level 2");
            assertThat(fakeFolderStoragePort.database.get(2).getPath()).contains(level1Folder.getPath());
        }

        @Test
        @DisplayName("[success] 특수문자가 포함된 폴더 이름으로 생성할 수 있다")
        void success_specialCharacters() {
            // given
            Folder parentFolder = Folder.builder()
                .id(1L)
                .groupId("test-group")
                .name("Parent Folder")
                .path("/test-group")
                .isRoot(true)
                .build();
            fakeFolderStoragePort.database.add(parentFolder);

            RegisterFolderCommand command = RegisterFolderCommand.builder()
                .parentId(1L)
                .groupId("test-group")
                .name("Folder_With-Special.Characters (2023)")
                .owner("owner@example.com")
                .accessLevel("PUBLIC")
                .build();

            // when
            RegisterFolderServiceResponse response = registerFolderService.createFolder(command);

            // then
            assertThat(response).isNotNull();
            assertThat(fakeFolderStoragePort.database).hasSize(2);
            assertThat(fakeFolderStoragePort.database.get(1).getName())
                .isEqualTo("Folder_With-Special.Characters (2023)");
        }

        @Test
        @DisplayName("[failure] FileUploadPort에서 예외 발생 시 예외를 전파한다")
        void failure_fileUploadError() {
            // given
            Folder parentFolder = Folder.builder()
                .id(1L)
                .groupId("test-group")
                .name("Parent Folder")
                .path("/test-group")
                .isRoot(true)
                .build();
            fakeFolderStoragePort.database.add(parentFolder);

            fakeFileUploadPort.shouldThrowException = true;

            RegisterFolderCommand command = RegisterFolderCommand.builder()
                .parentId(1L)
                .groupId("test-group")
                .name("New Folder")
                .owner("owner@example.com")
                .accessLevel("PUBLIC")
                .build();

            // when & then
            assertThatThrownBy(() -> registerFolderService.createFolder(command))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Folder creation failure");
        }
    }
}
