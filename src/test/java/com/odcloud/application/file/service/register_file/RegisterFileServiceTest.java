package com.odcloud.application.file.service.register_file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.odcloud.application.file.port.in.command.RegisterFileCommand;
import com.odcloud.domain.model.FolderInfo;
import com.odcloud.domain.model.Group;
import com.odcloud.fakeClass.FakeFilePort;
import com.odcloud.fakeClass.FakeFileStoragePort;
import com.odcloud.fakeClass.FakeFolderStoragePort;
import com.odcloud.fakeClass.FakeGroupStoragePort;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class RegisterFileServiceTest {

    private FakeFilePort fakeFilePort;
    private FakeFileStoragePort fakeFileStoragePort;
    private FakeFolderStoragePort fakeFolderStoragePort;
    private FakeGroupStoragePort fakeGroupStoragePort;
    private RegisterFileService registerFileService;

    @BeforeEach
    void setUp() {
        fakeFilePort = new FakeFilePort();
        fakeFileStoragePort = new FakeFileStoragePort();
        fakeFolderStoragePort = new FakeFolderStoragePort();
        fakeGroupStoragePort = new FakeGroupStoragePort();
        com.odcloud.infrastructure.constant.ProfileConstant.FileUpload fileUpload =
            new com.odcloud.infrastructure.constant.ProfileConstant.FileUpload("/base", "/data");
        com.odcloud.infrastructure.constant.ProfileConstant profileConstant =
            com.odcloud.infrastructure.constant.ProfileConstant.builder()
                .fileUpload(fileUpload)
                .build();
        registerFileService = new RegisterFileService(
            fakeFilePort,
            profileConstant,
            fakeFileStoragePort,
            fakeFolderStoragePort,
            fakeGroupStoragePort
        );
    }

    @Nested
    @DisplayName("[register] 파일 등록")
    class Describe_register {

        @Test
        @DisplayName("[success] 정상적으로 파일을 등록한다")
        void success() {
            // given
            Group group = Group.builder()
                .id(1L)
                .name("Test Group")
                .storageUsed(0L)
                .storageTotal(3221225472L)
                .build();
            fakeGroupStoragePort.groupDatabase.add(group);

            FolderInfo folder = FolderInfo.builder()
                .id(1L)
                .groupId(1L)
                .name("Test Folder")
                .build();
            fakeFolderStoragePort.database.add(folder);

            MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "test content".getBytes()
            );

            RegisterFileCommand command = RegisterFileCommand.builder()
                .folderId(1L)
                .files(List.of(file))
                .build();

            // when
            RegisterFileServiceResponse response = registerFileService.register(command);

            // then
            assertThat(response).isNotNull();
            assertThat(fakeFileStoragePort.database).hasSize(1);
            assertThat(fakeFileStoragePort.database.get(0).getFileName()).isEqualTo("test.txt");
            assertThat(fakeFileStoragePort.database.get(0).getFolderId()).isEqualTo(1L);
            assertThat(fakeFilePort.uploadFileCallCount).isEqualTo(1);

            Group updatedGroup = fakeGroupStoragePort.groupDatabase.get(0);
            assertThat(updatedGroup.getStorageUsed()).isEqualTo(file.getSize());
        }

        @Test
        @DisplayName("[success] 여러 파일을 한번에 등록한다")
        void success_multipleFiles() {
            // given
            Group group = Group.builder()
                .id(1L)
                .name("Test Group")
                .storageUsed(0L)
                .storageTotal(3221225472L)
                .build();
            fakeGroupStoragePort.groupDatabase.add(group);

            FolderInfo folder = FolderInfo.builder()
                .id(1L)
                .groupId(1L)
                .name("Test Folder")
                .build();
            fakeFolderStoragePort.database.add(folder);

            MockMultipartFile file1 = new MockMultipartFile(
                "file1",
                "test1.txt",
                "text/plain",
                "content1".getBytes()
            );

            MockMultipartFile file2 = new MockMultipartFile(
                "file2",
                "test2.pdf",
                "application/pdf",
                "content2".getBytes()
            );

            MockMultipartFile file3 = new MockMultipartFile(
                "file3",
                "test3.jpg",
                "image/jpeg",
                "content3".getBytes()
            );

            RegisterFileCommand command = RegisterFileCommand.builder()
                .folderId(1L)
                .files(List.of(file1, file2, file3))
                .build();

            long totalSize = file1.getSize() + file2.getSize() + file3.getSize();

            // when
            RegisterFileServiceResponse response = registerFileService.register(command);

            // then
            assertThat(response).isNotNull();
            assertThat(fakeFileStoragePort.database).hasSize(3);
            assertThat(fakeFileStoragePort.database.get(0).getFileName()).isEqualTo("test1.txt");
            assertThat(fakeFileStoragePort.database.get(1).getFileName()).isEqualTo("test2.pdf");
            assertThat(fakeFileStoragePort.database.get(2).getFileName()).isEqualTo("test3.jpg");
            assertThat(fakeFilePort.uploadFileCallCount).isEqualTo(3);

            Group updatedGroup = fakeGroupStoragePort.groupDatabase.get(0);
            assertThat(updatedGroup.getStorageUsed()).isEqualTo(totalSize);
        }

        @Test
        @DisplayName("[failure] 존재하지 않는 폴더에 파일 등록 시도하면 예외가 발생한다")
        void failure_nonExistentFolder() {
            // given
            MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "test content".getBytes()
            );

            RegisterFileCommand command = RegisterFileCommand.builder()
                .folderId(999L)
                .files(List.of(file))
                .build();

            // when & then
            assertThatThrownBy(() -> registerFileService.register(command))
                .isInstanceOf(CustomBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.Business_DoesNotExists_FOLDER);

            assertThat(fakeFileStoragePort.database).isEmpty();
            assertThat(fakeFilePort.uploadFileCallCount).isZero();
        }

        @Test
        @DisplayName("[failure] 스토리지 용량 초과 시 예외가 발생한다")
        void failure_storageLimitExceeded() {
            // given
            Group group = Group.builder()
                .id(1L)
                .name("Test Group")
                .storageUsed(3221225400L)
                .storageTotal(3221225472L)
                .build();
            fakeGroupStoragePort.groupDatabase.add(group);

            FolderInfo folder = FolderInfo.builder()
                .id(1L)
                .groupId(1L)
                .name("Test Folder")
                .build();
            fakeFolderStoragePort.database.add(folder);

            MockMultipartFile file = new MockMultipartFile(
                "file",
                "large-file.txt",
                "text/plain",
                new byte[100]
            );

            RegisterFileCommand command = RegisterFileCommand.builder()
                .folderId(1L)
                .files(List.of(file))
                .build();

            // when & then
            assertThatThrownBy(() -> registerFileService.register(command))
                .isInstanceOf(CustomBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.Business_STORAGE_LIMIT_EXCEEDED);

            assertThat(fakeFileStoragePort.database).isEmpty();
            assertThat(fakeFilePort.uploadFileCallCount).isZero();
        }

        @Test
        @DisplayName("[success] 스토리지 용량 여유가 있으면 파일 등록이 성공한다")
        void success_withEnoughStorage() {
            // given
            Group group = Group.builder()
                .id(1L)
                .name("Test Group")
                .storageUsed(1000L)
                .storageTotal(3221225472L)
                .build();
            fakeGroupStoragePort.groupDatabase.add(group);

            FolderInfo folder = FolderInfo.builder()
                .id(1L)
                .groupId(1L)
                .name("Test Folder")
                .build();
            fakeFolderStoragePort.database.add(folder);

            MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "content".getBytes()
            );

            RegisterFileCommand command = RegisterFileCommand.builder()
                .folderId(1L)
                .files(List.of(file))
                .build();

            // when
            RegisterFileServiceResponse response = registerFileService.register(command);

            // then
            assertThat(response).isNotNull();
            assertThat(fakeFileStoragePort.database).hasSize(1);

            Group updatedGroup = fakeGroupStoragePort.groupDatabase.get(0);
            assertThat(updatedGroup.getStorageUsed()).isEqualTo(1000L + file.getSize());
        }
    }
}
