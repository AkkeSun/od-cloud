package com.odcloud.adapter.in.find_groups;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.application.port.in.command.FindGroupsCommand;
import com.odcloud.domain.model.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FindGroupsRequestTest {

    @Nested
    @DisplayName("[toCommand] Request를 Command로 변환")
    class Describe_toCommand {

        @Test
        @DisplayName("[success] Account를 포함한 Command를 생성한다")
        void success() {
            // given
            Account account = Account.builder()
                .id(1L)
                .email("user@example.com")
                .nickname("User")
                .name("사용자")
                .build();

            FindGroupsRequest request = new FindGroupsRequest();

            // when
            FindGroupsCommand command = request.toCommand(account);

            // then
            assertThat(command).isNotNull();
            assertThat(command.account()).isEqualTo(account);
            assertThat(command.account().getId()).isEqualTo(1L);
            assertThat(command.account().getEmail()).isEqualTo("user@example.com");
        }

        @Test
        @DisplayName("[success] 다양한 Account로 Command를 생성한다")
        void success_variousAccounts() {
            // given
            FindGroupsRequest request = new FindGroupsRequest();

            Account account1 = Account.builder()
                .id(1L)
                .email("user1@example.com")
                .build();

            Account account2 = Account.builder()
                .id(999L)
                .email("admin@example.com")
                .build();

            // when
            FindGroupsCommand command1 = request.toCommand(account1);
            FindGroupsCommand command2 = request.toCommand(account2);

            // then
            assertThat(command1.account()).isEqualTo(account1);
            assertThat(command1.account().getId()).isEqualTo(1L);

            assertThat(command2.account()).isEqualTo(account2);
            assertThat(command2.account().getId()).isEqualTo(999L);
        }
    }
}
