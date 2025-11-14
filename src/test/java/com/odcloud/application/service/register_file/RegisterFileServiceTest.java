package com.odcloud.application.service.register_file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.odcloud.application.port.in.command.RegisterFileCommand;
import com.odcloud.domain.model.Folder;
import com.odcloud.fakeClass.FakeFileStoragePort;
import com.odcloud.fakeClass.FakeFileUploadPort;
import com.odcloud.fakeClass.FakeFolderStoragePort;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class RegisterFileServiceTest {

    private FakeFileUploadPort fakeFileUploadPort;
    private FakeFileStoragePort fakeFileStoragePort;
    private FakeFolderStoragePort fakeFolderStoragePort;
    private RegisterFileService registerFileService;

    @BeforeEach
    void setUp() {
        fakeFileUploadPort = new FakeFileUploadPort();
        fakeFileStoragePort = new FakeFileStoragePort();
        fakeFolderStoragePort = new FakeFolderStoragePort();
        registerFileService = new RegisterFileService(
            fakeFileUploadPort,
            fakeFileStoragePort,
            fakeFolderStoragePort
        );
    }

    @Nested
    @DisplayName("[register] 파일 등록")
    class Describe_register {

        @Test
        @DisplayName("[success] 정상적으로 파일을 등록한다")
        void success() {
            // given
            Folder folder = Folder.builder()
                .id(1L)
                .groupId("test-group")
                .name("Test Folder")
                .path("/test-group/folder1")
                .isRoot(false)
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
            assertThat(fakeFileUploadPort.uploadedFiles).hasSize(1);
        }

        @Test
        @DisplayName("[success] 여러 파일을 한번에 등록한다")
        void success_multipleFiles() {
            // given
            Folder folder = Folder.builder()
                .id(1L)
                .groupId("test-group")
                .name("Test Folder")
                .path("/test-group/folder1")
                .isRoot(false)
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

            // when
            RegisterFileServiceResponse response = registerFileService.register(command);

            // then
            assertThat(response).isNotNull();
            assertThat(fakeFileStoragePort.database).hasSize(3);
            assertThat(fakeFileStoragePort.database.get(0).getFileName()).isEqualTo("test1.txt");
            assertThat(fakeFileStoragePort.database.get(1).getFileName()).isEqualTo("test2.pdf");
            assertThat(fakeFileStoragePort.database.get(2).getFileName()).isEqualTo("test3.jpg");
            assertThat(fakeFileUploadPort.uploadedFiles).hasSize(3);
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
            assertThat(fakeFileUploadPort.uploadedFiles).isEmpty();
        }

        @Test
        @DisplayName("[failure] FileUploadPort에서 예외 발생 시 예외를 전파한다")
        void failure_fileUploadError() {
            // given
            Folder folder = Folder.builder()
                .id(1L)
                .groupId("test-group")
                .name("Test Folder")
                .path("/test-group/folder1")
                .isRoot(false)
                .build();
            fakeFolderStoragePort.database.add(folder);

            fakeFileUploadPort.shouldThrowException = true;

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

            // when & then
            assertThatThrownBy(() -> registerFileService.register(command))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("File upload failure");
        }

        @Test
        @DisplayName("[success] 빈 파일 리스트로도 호출 가능하다")
        void success_emptyFileList() {
            // given
            Folder folder = Folder.builder()
                .id(1L)
                .groupId("test-group")
                .name("Test Folder")
                .path("/test-group/folder1")
                .isRoot(false)
                .build();
            fakeFolderStoragePort.database.add(folder);

            RegisterFileCommand command = RegisterFileCommand.builder()
                .folderId(1L)
                .files(new ArrayList<>())
                .build();

            // when
            RegisterFileServiceResponse response = registerFileService.register(command);

            // then
            assertThat(response).isNotNull();
            assertThat(fakeFileStoragePort.database).isEmpty();
            assertThat(fakeFileUploadPort.uploadedFiles).isEmpty();
        }

        @Test
        @DisplayName("[success] 확장자가 없는 파일도 등록할 수 있다")
        void success_fileWithoutExtension() {
            // given
            Folder folder = Folder.builder()
                .id(1L)
                .groupId("test-group")
                .name("Test Folder")
                .path("/test-group/folder1")
                .isRoot(false)
                .build();
            fakeFolderStoragePort.database.add(folder);

            MockMultipartFile file = new MockMultipartFile(
                "file",
                "README",
                "text/plain",
                "readme content".getBytes()
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
            assertThat(fakeFileStoragePort.database.get(0).getFileName()).isEqualTo("README");
        }

        @Test
        @DisplayName("[success] 대용량 파일도 등록할 수 있다")
        void success_largeFile() {
            // given
            Folder folder = Folder.builder()
                .id(1L)
                .groupId("test-group")
                .name("Test Folder")
                .path("/test-group/folder1")
                .isRoot(false)
                .build();
            fakeFolderStoragePort.database.add(folder);

            byte[] largeContent = new byte[1024 * 1024]; // 1MB
            MockMultipartFile file = new MockMultipartFile(
                "file",
                "large-file.bin",
                "application/octet-stream",
                largeContent
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
            assertThat(fakeFileUploadPort.uploadedFiles).hasSize(1);
        }
    }
}
