package com.odcloud.adapter.in.controller.group.update_group_account_status;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.odcloud.application.group.port.in.command.UpdateGroupAccountStatusCommand;
import com.odcloud.domain.model.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UpdateGroupAccountStatusRequestTest {

    @Nested
    @DisplayName("[toCommand] Request를 Command로 변환하는 메서드")
    class Describe_toCommand {

        @Test
        @DisplayName("[success] Request를 Command로 변환한다")
        void success() {
            // given
            UpdateGroupAccountStatusRequest request = UpdateGroupAccountStatusRequest.builder()
                .status("APPROVED")
                .build();

            Long groupId = 1L;
            Long accountId = 1L;
            Account account = mock(Account.class);
            given(account.getEmail()).willReturn("owner@example.com");

            // when
            UpdateGroupAccountStatusCommand command = request.toCommand(groupId, accountId,
                account);

            // then
            assertThat(command).isNotNull();
            assertThat(command.groupId()).isEqualTo(groupId);
            assertThat(command.accountId()).isEqualTo(accountId);
            assertThat(command.groupOwnerEmail()).isEqualTo("owner@example.com");
            assertThat(command.status()).isEqualTo("APPROVED");
        }

        @Test
        @DisplayName("[success] null 값이 포함된 Request를 Command로 변환한다")
        void success_withNullValues() {
            // given
            UpdateGroupAccountStatusRequest request = UpdateGroupAccountStatusRequest.builder()
                .status(null)
                .build();

            Long groupId = null;
            Long accountId = null;
            Account account = mock(Account.class);
            given(account.getEmail()).willReturn(null);

            // when
            UpdateGroupAccountStatusCommand command = request.toCommand(groupId, accountId,
                account);

            // then
            assertThat(command).isNotNull();
            assertThat(command.groupId()).isNull();
            assertThat(command.accountId()).isNull();
            assertThat(command.groupOwnerEmail()).isNull();
            assertThat(command.status()).isNull();
        }

        @Test
        @DisplayName("[success] 빈 문자열이 포함된 Request를 Command로 변환한다")
        void success_withEmptyStrings() {
            // given
            UpdateGroupAccountStatusRequest request = UpdateGroupAccountStatusRequest.builder()
                .status("")
                .build();

            Long groupId = 1L;
            Long accountId = 1L;
            Account account = mock(Account.class);
            given(account.getEmail()).willReturn("");

            // when
            UpdateGroupAccountStatusCommand command = request.toCommand(groupId, accountId,
                account);

            // then
            assertThat(command).isNotNull();
            assertThat(command.groupId()).isEqualTo(1L);
            assertThat(command.accountId()).isEqualTo(1L);
            assertThat(command.groupOwnerEmail()).isEmpty();
            assertThat(command.status()).isEmpty();
        }
    }

    @Nested
    @DisplayName("[builder] Builder 패턴 테스트")
    class Describe_builder {

        @Test
        @DisplayName("[success] Builder로 Request를 생성한다")
        void success() {
            // when
            UpdateGroupAccountStatusRequest request = UpdateGroupAccountStatusRequest.builder()
                .status("APPROVED")
                .build();

            // then
            assertThat(request).isNotNull();
            assertThat(request.status()).isEqualTo("APPROVED");
        }

        @Test
        @DisplayName("[success] null 값으로 Request를 생성한다")
        void success_nullValues() {
            // when
            UpdateGroupAccountStatusRequest request = UpdateGroupAccountStatusRequest.builder()
                .status(null)
                .build();

            // then
            assertThat(request.status()).isNull();
        }
    }

    @Nested
    @DisplayName("[record] Record 불변성 테스트")
    class Describe_immutability {

        @Test
        @DisplayName("[success] Request는 불변 객체이다")
        void success() {
            // given
            UpdateGroupAccountStatusRequest request1 = UpdateGroupAccountStatusRequest.builder()
                .status("APPROVED")
                .build();

            UpdateGroupAccountStatusRequest request2 = UpdateGroupAccountStatusRequest.builder()
                .status("APPROVED")
                .build();

            // when & then
            assertThat(request1).isEqualTo(request2);
            assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
        }

        @Test
        @DisplayName("[success] 다른 값으로 생성된 Request는 동등하지 않다")
        void success_notEqual() {
            // given
            UpdateGroupAccountStatusRequest request1 = UpdateGroupAccountStatusRequest.builder()
                .status("APPROVED")
                .build();

            UpdateGroupAccountStatusRequest request2 = UpdateGroupAccountStatusRequest.builder()
                .status("BLOCK")
                .build();

            // when & then
            assertThat(request1).isNotEqualTo(request2);
        }
    }

    @Nested
    @DisplayName("[accessor] Request accessor 메서드 테스트")
    class Describe_accessor {

        @Test
        @DisplayName("[success] status()로 값을 조회한다")
        void success() {
            // given
            UpdateGroupAccountStatusRequest request = UpdateGroupAccountStatusRequest.builder()
                .status("APPROVED")
                .build();

            // when
            String status = request.status();

            // then
            assertThat(status).isEqualTo("APPROVED");
        }
    }

    @Nested
    @DisplayName("[toString] Request toString 메서드 테스트")
    class Describe_toString {

        @Test
        @DisplayName("[success] toString()으로 문자열 표현을 반환한다")
        void success() {
            // given
            UpdateGroupAccountStatusRequest request = UpdateGroupAccountStatusRequest.builder()
                .status("APPROVED")
                .build();

            // when
            String result = request.toString();

            // then
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
        }
    }
}
