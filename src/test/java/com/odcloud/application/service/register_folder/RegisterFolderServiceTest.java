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
                .groupId("test-group")
                .name("Parent Folder")
                .path("/test-group")
                .build();
            fakeFolderStoragePort.save(parentFolder);

            RegisterFolderCommand command = RegisterFolderCommand.builder()
                .parentId(0L)
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
            assertThat(fakeFolderStoragePort.database.get(1).getParentId()).isEqualTo(0L);
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
                .build();
            fakeFolderStoragePort.database.add(parentFolder);

            Folder existingFolder = Folder.builder()
                .id(2L)
                .parentId(1L)
                .groupId("test-group")
                .name("Existing Folder")
                .path("/test-group/existing")
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

    }
}
