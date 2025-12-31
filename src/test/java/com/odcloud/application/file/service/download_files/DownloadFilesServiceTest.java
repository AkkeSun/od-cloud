package com.odcloud.application.file.service.download_files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.odcloud.domain.model.FileInfo;
import com.odcloud.fakeClass.FakeFilePort;
import com.odcloud.fakeClass.FakeFileStoragePort;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class DownloadFilesServiceTest {

    private FakeFilePort fakeFilePort;
    private FakeFileStoragePort fakeFileStoragePort;
    private DownloadFilesService downloadFilesService;

    @BeforeEach
    void setUp() {
        fakeFilePort = new FakeFilePort();
        fakeFileStoragePort = new FakeFileStoragePort();
        downloadFilesService = new DownloadFilesService(fakeFilePort, fakeFileStoragePort);
    }

    @Nested
    @DisplayName("[download] 여러 파일을 압축하여 다운로드")
    class Describe_download {

        @Test
        @DisplayName("[success] 여러 파일을 ZIP으로 압축하여 다운로드한다")
        void success_multipleFiles() {
            // given
            FileInfo file1 = FileInfo.builder()
                .id(1L)
                .folderId(1L)
                .fileName("test1.txt")
                .fileLoc("/test-group/folder1/test1.txt")
                .regDt(LocalDateTime.now())
                .build();

            FileInfo file2 = FileInfo.builder()
                .id(2L)
                .folderId(1L)
                .fileName("test2.pdf")
                .fileLoc("/test-group/folder1/test2.pdf")
                .regDt(LocalDateTime.now())
                .build();

            FileInfo file3 = FileInfo.builder()
                .id(3L)
                .folderId(1L)
                .fileName("test3.jpg")
                .fileLoc("/test-group/folder1/test3.jpg")
                .regDt(LocalDateTime.now())
                .build();

            fakeFileStoragePort.database.add(file1);
            fakeFileStoragePort.database.add(file2);
            fakeFileStoragePort.database.add(file3);

            List<Long> fileIds = List.of(1L, 2L, 3L);

            // when
            DownloadFilesServiceResponse response = downloadFilesService.download(fileIds);

            // then
            assertThat(response).isNotNull();
            assertThat(response.resource()).isNotNull();
            assertThat(response.headers()).isNotNull();
            assertThat(response.headers().getContentType().toString()).contains(
                "application/octet-stream");
            assertThat(fakeFilePort.readFilesCallCount).isEqualTo(1);
        }

        @Test
        @DisplayName("[failure] 존재하지 않는 파일 ID로 다운로드 시도 시 예외가 발생한다")
        void failure_fileNotFound() {
            // given
            List<Long> nonExistentFileIds = List.of(999L, 1000L);

            // when & then
            assertThatThrownBy(() -> downloadFilesService.download(nonExistentFileIds))
                .isInstanceOf(CustomBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.Business_DoesNotExists_FILE);

            assertThat(fakeFilePort.readFilesCallCount).isEqualTo(0);
        }


        @Test
        @DisplayName("[failure] FilePort에서 오류 발생 시 예외가 전파된다")
        void failure_filePortError() {
            // given
            FileInfo file = FileInfo.builder()
                .id(1L)
                .folderId(1L)
                .fileName("test.txt")
                .fileLoc("/test-group/folder1/test.txt")
                .regDt(LocalDateTime.now())
                .build();

            fakeFileStoragePort.database.add(file);
            fakeFilePort.shouldThrowException = true;

            List<Long> fileIds = List.of(1L);

            // when & then
            assertThatThrownBy(() -> downloadFilesService.download(fileIds))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("File operation failure");
        }
    }
}
