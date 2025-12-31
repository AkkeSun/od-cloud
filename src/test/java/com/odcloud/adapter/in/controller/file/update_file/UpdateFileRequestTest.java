package com.odcloud.adapter.in.controller.file.update_file;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.application.file.port.in.command.UpdateFileCommand;
import com.odcloud.domain.model.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UpdateFileRequestTest {

    @Test
    @DisplayName("UpdateFileRequest를 UpdateFileCommand로 변환한다")
    void toCommand() {
        // given
        Long fileId = 1L;
        UpdateFileRequest request = UpdateFileRequest.builder()
            .fileName("new-name.txt")
            .folderId(2L)
            .build();

        Account account = Account.builder()
            .email("test@example.com")
            .build();

        // when
        UpdateFileCommand command = request.toCommand(fileId, account);

        // then
        assertThat(command.fileId()).isEqualTo(1L);
        assertThat(command.account()).isEqualTo(account);
        assertThat(command.fileName()).isEqualTo("new-name.txt");
        assertThat(command.folderId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("파일명만 있는 UpdateFileRequest를 UpdateFileCommand로 변환한다")
    void toCommand_fileNameOnly() {
        // given
        Long fileId = 1L;
        UpdateFileRequest request = UpdateFileRequest.builder()
            .fileName("new-name.txt")
            .build();

        Account account = Account.builder()
            .email("test@example.com")
            .build();

        // when
        UpdateFileCommand command = request.toCommand(fileId, account);

        // then
        assertThat(command.fileId()).isEqualTo(1L);
        assertThat(command.account()).isEqualTo(account);
        assertThat(command.fileName()).isEqualTo("new-name.txt");
        assertThat(command.folderId()).isNull();
    }

    @Test
    @DisplayName("폴더 ID만 있는 UpdateFileRequest를 UpdateFileCommand로 변환한다")
    void toCommand_folderIdOnly() {
        // given
        Long fileId = 1L;
        UpdateFileRequest request = UpdateFileRequest.builder()
            .folderId(2L)
            .build();

        Account account = Account.builder()
            .email("test@example.com")
            .build();

        // when
        UpdateFileCommand command = request.toCommand(fileId, account);

        // then
        assertThat(command.fileId()).isEqualTo(1L);
        assertThat(command.account()).isEqualTo(account);
        assertThat(command.fileName()).isNull();
        assertThat(command.folderId()).isEqualTo(2L);
    }
}
