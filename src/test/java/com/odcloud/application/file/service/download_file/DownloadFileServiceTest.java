package com.odcloud.application.file.service.download_file;

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

class DownloadFileServiceTest {

    private FakeFileStoragePort fakeFileStoragePort;
    private DownloadFileService downloadFileService;

    @BeforeEach
    void setUp() {
        fakeFileStoragePort = new FakeFileStoragePort();
        downloadFileService = new DownloadFileService(fakeFileStoragePort,
            FakeProfileConstant.create());
    }

    @Nested
    @DisplayName("[downloadFile] 단일 파일 다운로드")
    class Describe_downloadFile {

        @Test
        @DisplayName("[success] 접근 권한이 있으면 파일 URL을 응답한다")
        void success() {
            // given
            Long groupId = 1L;
            FileInfo file = FileInfo.builder()
                .id(1L)
                .folderId(1L)
                .groupId(groupId)
                .fileName("test.txt")
                .fileLoc("/test-group/folder1/test.txt")
                .regDt(LocalDateTime.now())
                .build();
            fakeFileStoragePort.database.add(file);

            Account account = Account.builder()
                .email("user@test.com")
                .groups(List.of(Group.of(groupId)))
                .build();

            DownloadFileCommand command = DownloadFileCommand.builder()
                .account(account)
                .fileId(1L)
                .build();

            // when
            DownloadFileResponse response = downloadFileService.downloadFile(command);

            // then
            assertThat(response).isNotNull();
            assertThat(response.fileName()).isEqualTo("test.txt");
            assertThat(response.fileUrl()).isEqualTo(
                "http://localhost:8080/test-group/folder1/test.txt");
        }

        @Test
        @DisplayName("[failure] 존재하지 않는 파일 다운로드 시도 시 예외가 발생한다")
        void failure_fileNotFound() {
            // given
            Account account = Account.builder()
                .email("user@test.com")
                .groups(List.of())
                .build();

            DownloadFileCommand command = DownloadFileCommand.builder()
                .account(account)
                .fileId(999L)
                .build();

            // when & then
            assertThatThrownBy(() -> downloadFileService.downloadFile(command))
                .isInstanceOf(CustomBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.Business_DoesNotExists_FILE);
        }

        @Test
        @DisplayName("[failure] 그룹에 속하지 않은 사용자가 다운로드 시도하면 접근 권한 예외가 발생한다")
        void failure_accessDenied() {
            // given
            FileInfo file = FileInfo.builder()
                .id(1L)
                .folderId(1L)
                .groupId(1L)
                .fileName("test.txt")
                .fileLoc("/test-group/folder1/test.txt")
                .regDt(LocalDateTime.now())
                .build();
            fakeFileStoragePort.database.add(file);

            Account account = Account.builder()
                .email("other@test.com")
                .groups(List.of(Group.of(2L)))
                .build();

            DownloadFileCommand command = DownloadFileCommand.builder()
                .account(account)
                .fileId(1L)
                .build();

            // when & then
            assertThatThrownBy(() -> downloadFileService.downloadFile(command))
                .isInstanceOf(CustomAuthorizationException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCESS_DENIED);
        }
    }
}
