package com.odcloud.application.file.service.download_files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.FileInfo;
import com.odcloud.domain.model.Group;
import com.odcloud.fakeClass.FakeFileStoragePort;
import com.odcloud.fakeClass.FakeProfileConstant;
import com.odcloud.infrastructure.exception.CustomAuthorizationException;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class DownloadFilesServiceTest {

    private FakeFileStoragePort fakeFileStoragePort;
    private DownloadFilesService downloadFilesService;

    @BeforeEach
    void setUp() {
        fakeFileStoragePort = new FakeFileStoragePort();
        downloadFilesService = new DownloadFilesService(fakeFileStoragePort,
            FakeProfileConstant.create());
    }

    @Nested
    @DisplayName("[download] 여러 파일의 다운로드 URL 조회")
    class Describe_download {

        @Test
        @DisplayName("[success] 접근 권한이 있는 여러 파일의 URL을 응답한다")
        void success_multipleFiles() {
            // given
            Long groupId = 1L;
            FileInfo file1 = FileInfo.builder()
                .id(1L)
                .folderId(1L)
                .groupId(groupId)
                .fileName("test1.txt")
                .fileLoc("/test-group/folder1/test1.txt")
                .regDt(LocalDateTime.now())
                .build();

            FileInfo file2 = FileInfo.builder()
                .id(2L)
                .folderId(1L)
                .groupId(groupId)
                .fileName("test2.pdf")
                .fileLoc("/test-group/folder1/test2.pdf")
                .regDt(LocalDateTime.now())
                .build();

            fakeFileStoragePort.database.add(file1);
            fakeFileStoragePort.database.add(file2);

            Account account = Account.builder()
                .email("user@test.com")
                .groups(List.of(Group.of(groupId)))
                .build();

            DownloadFilesCommand command = DownloadFilesCommand.builder()
                .account(account)
                .fileIds(List.of(1L, 2L))
                .build();

            // when
            DownloadFilesResponse response = downloadFilesService.download(command);

            // then
            assertThat(response).isNotNull();
            assertThat(response.files()).hasSize(2);
            assertThat(response.files()).extracting("fileName")
                .containsExactly("test1.txt", "test2.pdf");
            assertThat(response.files()).extracting("fileUrl")
                .containsExactly(
                    "http://localhost:8080/test-group/folder1/test1.txt",
                    "http://localhost:8080/test-group/folder1/test2.pdf");
        }

        @Test
        @DisplayName("[failure] 존재하지 않는 파일 ID로 조회 시도 시 예외가 발생한다")
        void failure_fileNotFound() {
            // given
            Account account = Account.builder()
                .email("user@test.com")
                .groups(List.of())
                .build();

            DownloadFilesCommand command = DownloadFilesCommand.builder()
                .account(account)
                .fileIds(List.of(999L, 1000L))
                .build();

            // when & then
            assertThatThrownBy(() -> downloadFilesService.download(command))
                .isInstanceOf(CustomBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.Business_DoesNotExists_FILE);
        }

        @Test
        @DisplayName("[failure] 요청한 파일 중 접근 권한이 없는 파일이 있으면 예외가 발생한다")
        void failure_accessDenied() {
            // given
            FileInfo file1 = FileInfo.builder()
                .id(1L)
                .folderId(1L)
                .groupId(1L)
                .fileName("test1.txt")
                .fileLoc("/test-group/folder1/test1.txt")
                .regDt(LocalDateTime.now())
                .build();

            FileInfo file2 = FileInfo.builder()
                .id(2L)
                .folderId(1L)
                .groupId(2L)
                .fileName("test2.txt")
                .fileLoc("/test-group/folder1/test2.txt")
                .regDt(LocalDateTime.now())
                .build();

            fakeFileStoragePort.database.add(file1);
            fakeFileStoragePort.database.add(file2);

            Account account = Account.builder()
                .email("user@test.com")
                .groups(List.of(Group.of(1L)))
                .build();

            DownloadFilesCommand command = DownloadFilesCommand.builder()
                .account(account)
                .fileIds(List.of(1L, 2L))
                .build();

            // when & then
            assertThatThrownBy(() -> downloadFilesService.download(command))
                .isInstanceOf(CustomAuthorizationException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCESS_DENIED);
        }
    }
}
