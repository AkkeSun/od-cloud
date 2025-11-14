package com.odcloud.application.service.download_file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.odcloud.application.port.in.command.DownloadFileCommand;
import com.odcloud.application.port.in.command.DownloadFilesCommand;
import com.odcloud.application.port.in.command.DownloadFolderCommand;
import com.odcloud.domain.model.File;
import com.odcloud.domain.model.Folder;
import com.odcloud.fakeClass.FakeFileDownloadPort;
import com.odcloud.fakeClass.FakeFileStoragePort;
import com.odcloud.fakeClass.FakeFolderStoragePort;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class DownloadFileServiceTest {

    private DownloadFileService downloadFileService;
    private FakeFileStoragePort fakeFileStoragePort;
    private FakeFolderStoragePort fakeFolderStoragePort;
    private FakeFileDownloadPort fakeFileDownloadPort;

    @BeforeEach
    void setUp() {
        fakeFileStoragePort = new FakeFileStoragePort();
        fakeFolderStoragePort = new FakeFolderStoragePort();
        fakeFileDownloadPort = new FakeFileDownloadPort();

        downloadFileService = new DownloadFileService(
            fakeFileStoragePort,
            fakeFolderStoragePort,
            fakeFileDownloadPort
        );
    }

    @Nested
    @DisplayName("[downloadFile] 단건 파일 다운로드")
    class Describe_downloadFile {

        @Test
        @DisplayName("[success] 파일을 다운로드한다")
        void success() {
            // given
            File file = File.builder()
                .id(1L)
                .folderId(1L)
                .fileName("test.txt")
                .fileLoc("/files/test.txt")
                .regDt(LocalDateTime.now())
                .build();
            fakeFileStoragePort.database.add(file);
            fakeFileDownloadPort.addFile("/files/test.txt", "file content".getBytes());

            DownloadFileCommand command = DownloadFileCommand.builder()
                .fileId(1L)
                .build();

            // when
            DownloadFileServiceResponse response = downloadFileService.downloadFile(command);

            // then
            assertThat(response.fileName()).isEqualTo("test.txt");
            assertThat(new String(response.content())).isEqualTo("file content");
            assertThat(response.contentType()).isEqualTo("text/plain");
        }

        @Test
        @DisplayName("[success] PDF 파일을 다운로드한다")
        void success_pdfFile() {
            // given
            File file = File.builder()
                .id(2L)
                .folderId(1L)
                .fileName("document.pdf")
                .fileLoc("/files/document.pdf")
                .regDt(LocalDateTime.now())
                .build();
            fakeFileStoragePort.database.add(file);
            fakeFileDownloadPort.addFile("/files/document.pdf", "PDF content".getBytes());

            DownloadFileCommand command = DownloadFileCommand.builder()
                .fileId(2L)
                .build();

            // when
            DownloadFileServiceResponse response = downloadFileService.downloadFile(command);

            // then
            assertThat(response.fileName()).isEqualTo("document.pdf");
            assertThat(response.contentType()).isEqualTo("application/pdf");
        }

        @Test
        @DisplayName("[exception] 존재하지 않는 파일 ID로 요청 시 예외가 발생한다")
        void exception_fileNotFound() {
            // given
            DownloadFileCommand command = DownloadFileCommand.builder()
                .fileId(999L)
                .build();

            // when & then
            assertThatThrownBy(() -> downloadFileService.downloadFile(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("파일을 찾을 수 없습니다");
        }
    }

    @Nested
    @DisplayName("[downloadFiles] 복수 파일 다운로드")
    class Describe_downloadFiles {

        @Test
        @DisplayName("[success] 여러 파일을 ZIP으로 압축하여 다운로드한다")
        void success() {
            // given
            File file1 = File.builder()
                .id(1L)
                .folderId(1L)
                .fileName("file1.txt")
                .fileLoc("/files/file1.txt")
                .regDt(LocalDateTime.now())
                .build();
            File file2 = File.builder()
                .id(2L)
                .folderId(1L)
                .fileName("file2.txt")
                .fileLoc("/files/file2.txt")
                .regDt(LocalDateTime.now())
                .build();

            fakeFileStoragePort.database.add(file1);
            fakeFileStoragePort.database.add(file2);
            fakeFileDownloadPort.addFile("/files/file1.txt", "content1".getBytes());
            fakeFileDownloadPort.addFile("/files/file2.txt", "content2".getBytes());

            DownloadFilesCommand command = DownloadFilesCommand.builder()
                .fileIds(List.of(1L, 2L))
                .build();

            // when
            DownloadFileServiceResponse response = downloadFileService.downloadFiles(command);

            // then
            assertThat(response.fileName()).isEqualTo("files.zip");
            assertThat(response.contentType()).isEqualTo("application/zip");
            assertThat(response.content()).isNotEmpty();
        }

        @Test
        @DisplayName("[exception] 파일 ID 목록이 비어있으면 예외가 발생한다")
        void exception_emptyFileIds() {
            // given
            DownloadFilesCommand command = DownloadFilesCommand.builder()
                .fileIds(List.of())
                .build();

            // when & then
            assertThatThrownBy(() -> downloadFileService.downloadFiles(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("다운로드할 파일이 없습니다");
        }
    }

    @Nested
    @DisplayName("[downloadFolder] 폴더 다운로드")
    class Describe_downloadFolder {

        @Test
        @DisplayName("[success] 폴더 내 모든 파일을 ZIP으로 압축하여 다운로드한다")
        void success() {
            // given
            Folder folder = Folder.builder()
                .id(1L)
                .groupId(1L)
                .name("MyFolder")
                .path("/folders/myfolder")
                .parentId(null)
                .isRoot(false)
                .regDt(LocalDateTime.now())
                .build();
            fakeFolderStoragePort.database.add(folder);

            File file1 = File.builder()
                .id(1L)
                .folderId(1L)
                .fileName("file1.txt")
                .fileLoc("/files/file1.txt")
                .regDt(LocalDateTime.now())
                .build();
            File file2 = File.builder()
                .id(2L)
                .folderId(1L)
                .fileName("file2.txt")
                .fileLoc("/files/file2.txt")
                .regDt(LocalDateTime.now())
                .build();

            fakeFileStoragePort.database.add(file1);
            fakeFileStoragePort.database.add(file2);
            fakeFileDownloadPort.addFile("/files/file1.txt", "content1".getBytes());
            fakeFileDownloadPort.addFile("/files/file2.txt", "content2".getBytes());

            DownloadFolderCommand command = DownloadFolderCommand.builder()
                .folderId(1L)
                .build();

            // when
            DownloadFileServiceResponse response = downloadFileService.downloadFolder(command);

            // then
            assertThat(response.fileName()).isEqualTo("MyFolder.zip");
            assertThat(response.contentType()).isEqualTo("application/zip");
            assertThat(response.content()).isNotEmpty();
        }

        @Test
        @DisplayName("[success] 서브폴더를 포함한 모든 파일을 다운로드한다")
        void success_withSubFolders() {
            // given
            Folder rootFolder = Folder.builder()
                .id(1L)
                .groupId(1L)
                .name("RootFolder")
                .path("/folders/root")
                .parentId(null)
                .isRoot(false)
                .regDt(LocalDateTime.now())
                .build();
            Folder subFolder = Folder.builder()
                .id(2L)
                .groupId(1L)
                .name("SubFolder")
                .path("/folders/root/sub")
                .parentId(1L)
                .isRoot(false)
                .regDt(LocalDateTime.now())
                .build();

            fakeFolderStoragePort.database.add(rootFolder);
            fakeFolderStoragePort.database.add(subFolder);

            File file1 = File.builder()
                .id(1L)
                .folderId(1L)
                .fileName("root_file.txt")
                .fileLoc("/files/root_file.txt")
                .regDt(LocalDateTime.now())
                .build();
            File file2 = File.builder()
                .id(2L)
                .folderId(2L)
                .fileName("sub_file.txt")
                .fileLoc("/files/sub_file.txt")
                .regDt(LocalDateTime.now())
                .build();

            fakeFileStoragePort.database.add(file1);
            fakeFileStoragePort.database.add(file2);
            fakeFileDownloadPort.addFile("/files/root_file.txt", "root content".getBytes());
            fakeFileDownloadPort.addFile("/files/sub_file.txt", "sub content".getBytes());

            DownloadFolderCommand command = DownloadFolderCommand.builder()
                .folderId(1L)
                .build();

            // when
            DownloadFileServiceResponse response = downloadFileService.downloadFolder(command);

            // then
            assertThat(response.fileName()).isEqualTo("RootFolder.zip");
            assertThat(response.contentType()).isEqualTo("application/zip");
            assertThat(response.content()).isNotEmpty();
        }

        @Test
        @DisplayName("[exception] 폴더에 파일이 없으면 예외가 발생한다")
        void exception_emptyFolder() {
            // given
            Folder folder = Folder.builder()
                .id(1L)
                .groupId(1L)
                .name("EmptyFolder")
                .path("/folders/empty")
                .parentId(null)
                .isRoot(false)
                .regDt(LocalDateTime.now())
                .build();
            fakeFolderStoragePort.database.add(folder);

            DownloadFolderCommand command = DownloadFolderCommand.builder()
                .folderId(1L)
                .build();

            // when & then
            assertThatThrownBy(() -> downloadFileService.downloadFolder(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("폴더에 다운로드할 파일이 없습니다");
        }
    }
}
