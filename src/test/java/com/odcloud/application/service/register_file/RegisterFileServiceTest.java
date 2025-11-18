package com.odcloud.application.service.register_file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.odcloud.application.port.in.command.RegisterFileCommand;
import com.odcloud.domain.model.Folder;
import com.odcloud.fakeClass.FakeFilePort;
import com.odcloud.fakeClass.FakeFileStoragePort;
import com.odcloud.fakeClass.FakeFolderStoragePort;
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
    private RegisterFileService registerFileService;

    @BeforeEach
    void setUp() {
        fakeFilePort = new FakeFilePort();
        fakeFileStoragePort = new FakeFileStoragePort();
        fakeFolderStoragePort = new FakeFolderStoragePort();
        registerFileService = new RegisterFileService(
            fakeFilePort,
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
            assertThat(fakeFilePort.uploadFileCallCount).isEqualTo(3);
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
        @DisplayName("[failure] 동일한 파일명이 이미 존재하면 예외가 발생한다")
        void failure_duplicateFileName() {
            // given
            Folder folder = Folder.builder()
                .id(1L)
                .groupId("test-group")
                .name("Test Folder")
                .path("/test-group/folder1")
                .build();
            fakeFolderStoragePort.database.add(folder);

            // 기존 파일 추가
            MockMultipartFile existingFile = new MockMultipartFile(
                "file",
                "duplicate.txt",
                "text/plain",
                "existing content".getBytes()
            );
            RegisterFileCommand existingCommand = RegisterFileCommand.builder()
                .folderId(1L)
                .files(List.of(existingFile))
                .build();
            registerFileService.register(existingCommand);

            // 중복된 파일명으로 등록 시도
            MockMultipartFile duplicateFile = new MockMultipartFile(
                "file",
                "duplicate.txt",
                "text/plain",
                "new content".getBytes()
            );
            RegisterFileCommand command = RegisterFileCommand.builder()
                .folderId(1L)
                .files(List.of(duplicateFile))
                .build();

            // when & then
            assertThatThrownBy(() -> registerFileService.register(command))
                .isInstanceOf(CustomBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.Business_SAVED_FILE_NAME);

            // 기존 파일만 존재해야 함
            assertThat(fakeFileStoragePort.database).hasSize(1);
            assertThat(fakeFileStoragePort.database.get(0).getFileName()).isEqualTo("duplicate.txt");
        }

        @Test
        @DisplayName("[failure] 여러 파일 등록 중 중복 파일명이 있으면 이미 업로드된 파일들을 삭제하고 예외가 발생한다")
        void failure_duplicateFileName_multipleFiles() {
            // given
            Folder folder = Folder.builder()
                .id(1L)
                .groupId("test-group")
                .name("Test Folder")
                .path("/test-group/folder1")
                .build();
            fakeFolderStoragePort.database.add(folder);

            // 기존 파일 추가
            MockMultipartFile existingFile = new MockMultipartFile(
                "file",
                "existing.txt",
                "text/plain",
                "existing content".getBytes()
            );
            RegisterFileCommand existingCommand = RegisterFileCommand.builder()
                .folderId(1L)
                .files(List.of(existingFile))
                .build();
            registerFileService.register(existingCommand);

            // 여러 파일 중 하나가 중복된 파일명
            MockMultipartFile file1 = new MockMultipartFile(
                "file1",
                "new1.txt",
                "text/plain",
                "content1".getBytes()
            );
            MockMultipartFile file2 = new MockMultipartFile(
                "file2",
                "new2.txt",
                "text/plain",
                "content2".getBytes()
            );
            MockMultipartFile duplicateFile = new MockMultipartFile(
                "file3",
                "existing.txt",
                "text/plain",
                "duplicate content".getBytes()
            );

            RegisterFileCommand command = RegisterFileCommand.builder()
                .folderId(1L)
                .files(List.of(file1, file2, duplicateFile))
                .build();

            // when & then
            assertThatThrownBy(() -> registerFileService.register(command))
                .isInstanceOf(CustomBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.Business_SAVED_FILE_NAME);

            // 이미 업로드된 파일들이 삭제되었는지 확인
            assertThat(fakeFilePort.deletedFiles).containsExactly("new1.txt", "new2.txt");
            assertThat(fakeFilePort.deleteFilesCallCount).isEqualTo(1);

            // Note: 실제 환경에서는 @Transactional로 인해 롤백되지만,
            // Fake 구현에서는 트랜잭션이 없으므로 database에 파일이 저장된 상태로 남음
            // 중요한 것은 물리적 파일이 삭제(deleteFiles 호출)되었다는 것
        }
    }
}
