package com.odcloud.adapter.in.controller.group.update_group;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.odcloud.application.group.port.in.command.UpdateGroupCommand;
import com.odcloud.domain.model.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UpdateGroupRequestTest {

    @Nested
    @DisplayName("[toCommand] Request를 Command로 변환하는 메서드")
    class Describe_toCommand {

        @Test
        @DisplayName("[success] ownerEmail만 포함된 Request를 Command로 변환한다")
        void success_ownerEmailOnly() {
            // given
            UpdateGroupRequest request = UpdateGroupRequest.builder()
                .ownerEmail("newowner@example.com")
                .build();

            Account account = mock(Account.class);
            given(account.getEmail()).willReturn("owner@example.com");

            // when
            UpdateGroupCommand command = request.toCommand(1L, account);

            // then
            assertThat(command).isNotNull();
            assertThat(command.groupId()).isEqualTo(1L);
            assertThat(command.currentOwnerEmail()).isEqualTo("owner@example.com");
            assertThat(command.newOwnerEmail()).isEqualTo("newowner@example.com");
            assertThat(command.name()).isNull();
        }

        @Test
        @DisplayName("[success] name만 포함된 Request를 Command로 변환한다")
        void success_nameOnly() {
            // given
            UpdateGroupRequest request = UpdateGroupRequest.builder()
                .name("New Group Name")
                .build();

            Account account = mock(Account.class);
            given(account.getEmail()).willReturn("owner@example.com");

            // when
            UpdateGroupCommand command = request.toCommand(1L, account);

            // then
            assertThat(command).isNotNull();
            assertThat(command.groupId()).isEqualTo(1L);
            assertThat(command.currentOwnerEmail()).isEqualTo("owner@example.com");
            assertThat(command.newOwnerEmail()).isNull();
            assertThat(command.name()).isEqualTo("New Group Name");
        }

        @Test
        @DisplayName("[success] ownerEmail과 name 모두 포함된 Request를 Command로 변환한다")
        void success_both() {
            // given
            UpdateGroupRequest request = UpdateGroupRequest.builder()
                .ownerEmail("newowner@example.com")
                .name("New Group Name")
                .build();

            Account account = mock(Account.class);
            given(account.getEmail()).willReturn("owner@example.com");

            // when
            UpdateGroupCommand command = request.toCommand(1L, account);

            // then
            assertThat(command).isNotNull();
            assertThat(command.groupId()).isEqualTo(1L);
            assertThat(command.currentOwnerEmail()).isEqualTo("owner@example.com");
            assertThat(command.newOwnerEmail()).isEqualTo("newowner@example.com");
            assertThat(command.name()).isEqualTo("New Group Name");
        }

        @Test
        @DisplayName("[success] 빈 Request를 Command로 변환한다")
        void success_empty() {
            // given
            UpdateGroupRequest request = UpdateGroupRequest.builder()
                .build();

            Account account = mock(Account.class);
            given(account.getEmail()).willReturn("owner@example.com");

            // when
            UpdateGroupCommand command = request.toCommand(1L, account);

            // then
            assertThat(command).isNotNull();
            assertThat(command.groupId()).isEqualTo(1L);
            assertThat(command.currentOwnerEmail()).isEqualTo("owner@example.com");
            assertThat(command.newOwnerEmail()).isNull();
            assertThat(command.name()).isNull();
        }

        @Test
        @DisplayName("[success] 빈 문자열이 포함된 Request를 Command로 변환한다")
        void success_withEmptyStrings() {
            // given
            UpdateGroupRequest request = UpdateGroupRequest.builder()
                .ownerEmail("")
                .name("")
                .build();

            Account account = mock(Account.class);
            given(account.getEmail()).willReturn("");

            // when
            UpdateGroupCommand command = request.toCommand(0L, account);

            // then
            assertThat(command).isNotNull();
            assertThat(command.groupId()).isEqualTo(0L);
            assertThat(command.currentOwnerEmail()).isEmpty();
            assertThat(command.newOwnerEmail()).isEmpty();
            assertThat(command.name()).isEmpty();
        }
    }

    @Nested
    @DisplayName("[builder] Builder 패턴 테스트")
    class Describe_builder {

        @Test
        @DisplayName("[success] Builder로 모든 필드를 포함한 Request를 생성한다")
        void success_allFields() {
            // when
            UpdateGroupRequest request = UpdateGroupRequest.builder()
                .ownerEmail("newowner@example.com")
                .name("New Group Name")
                .build();

            // then
            assertThat(request).isNotNull();
            assertThat(request.getOwnerEmail()).isEqualTo("newowner@example.com");
            assertThat(request.getName()).isEqualTo("New Group Name");
        }

        @Test
        @DisplayName("[success] Builder로 ownerEmail만 포함한 Request를 생성한다")
        void success_ownerEmailOnly() {
            // when
            UpdateGroupRequest request = UpdateGroupRequest.builder()
                .ownerEmail("newowner@example.com")
                .build();

            // then
            assertThat(request).isNotNull();
            assertThat(request.getOwnerEmail()).isEqualTo("newowner@example.com");
            assertThat(request.getName()).isNull();
        }

        @Test
        @DisplayName("[success] Builder로 name만 포함한 Request를 생성한다")
        void success_nameOnly() {
            // when
            UpdateGroupRequest request = UpdateGroupRequest.builder()
                .name("New Group Name")
                .build();

            // then
            assertThat(request).isNotNull();
            assertThat(request.getOwnerEmail()).isNull();
            assertThat(request.getName()).isEqualTo("New Group Name");
        }

        @Test
        @DisplayName("[success] null 값으로 Request를 생성한다")
        void success_nullValues() {
            // when
            UpdateGroupRequest request = UpdateGroupRequest.builder()
                .ownerEmail(null)
                .name(null)
                .build();

            // then
            assertThat(request.getOwnerEmail()).isNull();
            assertThat(request.getName()).isNull();
        }
    }

    @Nested
    @DisplayName("[accessor] Getter 메서드 테스트")
    class Describe_accessor {

        @Test
        @DisplayName("[success] getOwnerEmail()로 값을 조회한다")
        void success_getOwnerEmail() {
            // given
            UpdateGroupRequest request = UpdateGroupRequest.builder()
                .ownerEmail("newowner@example.com")
                .build();

            // when
            String ownerEmail = request.getOwnerEmail();

            // then
            assertThat(ownerEmail).isEqualTo("newowner@example.com");
        }

        @Test
        @DisplayName("[success] getName()으로 값을 조회한다")
        void success_getName() {
            // given
            UpdateGroupRequest request = UpdateGroupRequest.builder()
                .name("New Group Name")
                .build();

            // when
            String name = request.getName();

            // then
            assertThat(name).isEqualTo("New Group Name");
        }
    }

    @Nested
    @DisplayName("[toString] toString 메서드 테스트")
    class Describe_toString {

        @Test
        @DisplayName("[success] toString()으로 문자열 표현을 반환한다")
        void success() {
            // given
            UpdateGroupRequest request = UpdateGroupRequest.builder()
                .ownerEmail("newowner@example.com")
                .name("New Group Name")
                .build();

            // when
            String result = request.toString();

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("[success] null 값을 포함한 toString()을 반환한다")
        void success_nullValues() {
            // given
            UpdateGroupRequest request = UpdateGroupRequest.builder()
                .build();

            // when
            String result = request.toString();

            // then
            assertThat(result).isNotNull();
        }
    }
}
