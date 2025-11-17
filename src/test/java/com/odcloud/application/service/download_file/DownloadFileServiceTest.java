package com.odcloud.application.service.download_file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.odcloud.domain.model.File;
import com.odcloud.fakeClass.FakeFilePort;
import com.odcloud.fakeClass.FakeFileStoragePort;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class DownloadFileServiceTest {

    private FakeFilePort fakeFilePort;
    private FakeFileStoragePort fakeFileStoragePort;
    private DownloadFileService downloadFileService;

    @BeforeEach
    void setUp() {
        fakeFilePort = new FakeFilePort();
        fakeFileStoragePort = new FakeFileStoragePort();
        downloadFileService = new DownloadFileService(fakeFilePort, fakeFileStoragePort);
    }

    @Nested
    @DisplayName("[downloadFile] 단일 파일 다운로드")
    class Describe_downloadFile {

        @Test
        @DisplayName("[success] 정상적으로 파일을 다운로드한다")
        void success() {
            // given
            File file = File.builder()
                .id(1L)
                .folderId(1L)
                .fileName("test.txt")
                .fileLoc("/test-group/folder1/test.txt")
                .regDt(LocalDateTime.now())
                .build();
            fakeFileStoragePort.database.add(file);

            // when
            DownloadFileServiceResponse response = downloadFileService.downloadFile(1L);

            // then
            assertThat(response).isNotNull();
            assertThat(response.resource()).isNotNull();
            assertThat(response.headers()).isNotNull();
            assertThat(response.headers().getContentDisposition().getFilename()).isEqualTo("test.txt");
            assertThat(fakeFilePort.readFileCallCount).isEqualTo(1);
        }

        @Test
        @DisplayName("[failure] 존재하지 않는 파일 다운로드 시도 시 예외가 발생한다")
        void failure_fileNotFound() {
            // given
            Long nonExistentFileId = 999L;

            // when & then
            assertThatThrownBy(() -> downloadFileService.downloadFile(nonExistentFileId))
                .isInstanceOf(CustomBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.Business_DoesNotExists_FILE);

            assertThat(fakeFilePort.readFileCallCount).isEqualTo(0);
        }

        @Test
        @DisplayName("[failure] FilePort에서 오류 발생 시 예외가 전파된다")
        void failure_filePortError() {
            // given
            File file = File.builder()
                .id(1L)
                .folderId(1L)
                .fileName("test.txt")
                .fileLoc("/test-group/folder1/test.txt")
                .regDt(LocalDateTime.now())
                .build();
            fakeFileStoragePort.database.add(file);
            fakeFilePort.shouldThrowException = true;

            // when & then
            assertThatThrownBy(() -> downloadFileService.downloadFile(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("File operation failure");
        }
    }
}
