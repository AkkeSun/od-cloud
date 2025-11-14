package com.odcloud.adapter.in.register_folder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.odcloud.application.port.in.command.RegisterFolderCommand;
import com.odcloud.domain.model.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RegisterFolderRequestTest {

    @Nested
    @DisplayName("[toCommand] Request 객체를 Command 로 변환하는 메소드")
    class Describe_toCommand {

        @Test
        @DisplayName("[success] Request 객체가 Command로 잘 변환한다.")
        void success() {
            // given
            RegisterFolderRequest request = RegisterFolderRequest.builder()
                .parentId(1L)
                .groupId("group-123")
                .name("name")
                .accessLevel("PUBLIC")
                .build();

            Account account = mock(Account.class);
            given(account.getEmail()).willReturn("test@example.com");

            // when
            RegisterFolderCommand command = request.toCommand(account);

            // then
            assertThat(command).isNotNull();
            assertThat(command.parentId()).isEqualTo(1L);
            assertThat(command.groupId()).isEqualTo("group-123");
            assertThat(command.name()).isEqualTo("name");
            assertThat(command.owner()).isEqualTo("test@example.com");
        }

        @Test
        @DisplayName("[success] null 인 경우에도 command 로 잘 변환한다.")
        void success_withNullValues() {
            // given
            RegisterFolderRequest request = RegisterFolderRequest.builder()
                .parentId(null)
                .groupId(null)
                .name(null)
                .accessLevel(null)
                .build();

            Account account = mock(Account.class);
            given(account.getEmail()).willReturn(null);

            // when
            RegisterFolderCommand command = request.toCommand(account);

            // then
            assertThat(command).isNotNull();
            assertThat(command.parentId()).isNull();
            assertThat(command.groupId()).isNull();
            assertThat(command.name()).isNull();
            assertThat(command.owner()).isNull();
        }

        @Test
        @DisplayName("[success] 빈 문자열인 경우에도 command 로 잘 변환한다.")
        void success_withEmptyStrings() {
            // given
            RegisterFolderRequest request = RegisterFolderRequest.builder()
                .parentId(0L)
                .groupId("")
                .name("")
                .accessLevel("")
                .build();

            Account account = mock(Account.class);
            given(account.getEmail()).willReturn("");

            // when
            RegisterFolderCommand command = request.toCommand(account);

            // then
            assertThat(command).isNotNull();
            assertThat(command.parentId()).isEqualTo(0L);
            assertThat(command.groupId()).isEmpty();
            assertThat(command.name()).isEmpty();
            assertThat(command.owner()).isEmpty();
        }
    }
}
