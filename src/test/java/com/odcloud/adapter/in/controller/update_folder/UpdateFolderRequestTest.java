package com.odcloud.adapter.in.controller.update_folder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.odcloud.application.port.in.command.UpdateFolderCommand;
import com.odcloud.domain.model.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UpdateFolderRequestTest {

    @Nested
    @DisplayName("[toCommand] Request를 Command로 변환하는 메서드")
    class Describe_toCommand {

        @Test
        @DisplayName("[success] 모든 필드가 있는 Request를 Command로 변환한다")
        void success_allFields() {
            // given
            UpdateFolderRequest request = UpdateFolderRequest.builder()
                .name("수정된 폴더명")
                .parentId(2L)
                .build();

            Account account = mock(Account.class);
            given(account.getEmail()).willReturn("test@example.com");

            // when
            UpdateFolderCommand command = request.toCommand(1L, account);

            // then
            assertThat(command).isNotNull();
            assertThat(command.folderId()).isEqualTo(1L);
            assertThat(command.account()).isEqualTo(account);
            assertThat(command.name()).isEqualTo("수정된 폴더명");
            assertThat(command.parentId()).isEqualTo(2L);
        }

        @Test
        @DisplayName("[success] 폴더명만 있는 Request를 Command로 변환한다")
        void success_nameOnly() {
            // given
            UpdateFolderRequest request = UpdateFolderRequest.builder()
                .name("수정된 폴더명")
                .build();

            Account account = mock(Account.class);

            // when
            UpdateFolderCommand command = request.toCommand(1L, account);

            // then
            assertThat(command).isNotNull();
            assertThat(command.name()).isEqualTo("수정된 폴더명");
            assertThat(command.parentId()).isNull();
        }

        @Test
        @DisplayName("[success] 상위 폴더만 있는 Request를 Command로 변환한다")
        void success_parentIdOnly() {
            // given
            UpdateFolderRequest request = UpdateFolderRequest.builder()
                .parentId(2L)
                .build();

            Account account = mock(Account.class);

            // when
            UpdateFolderCommand command = request.toCommand(1L, account);

            // then
            assertThat(command).isNotNull();
            assertThat(command.name()).isNull();
            assertThat(command.parentId()).isEqualTo(2L);
        }
    }

    @Nested
    @DisplayName("[builder] Builder 패턴 테스트")
    class Describe_builder {

        @Test
        @DisplayName("[success] Builder로 Request를 생성한다")
        void success() {
            // when
            UpdateFolderRequest request = UpdateFolderRequest.builder()
                .name("수정된 폴더명")
                .parentId(2L)
                .build();

            // then
            assertThat(request).isNotNull();
            assertThat(request.name()).isEqualTo("수정된 폴더명");
            assertThat(request.parentId()).isEqualTo(2L);
        }

        @Test
        @DisplayName("[success] null 값으로 Request를 생성한다")
        void success_nullValues() {
            // when
            UpdateFolderRequest request = UpdateFolderRequest.builder()
                .name(null)
                .parentId(null)
                .build();

            // then
            assertThat(request.name()).isNull();
            assertThat(request.parentId()).isNull();
        }
    }
}
