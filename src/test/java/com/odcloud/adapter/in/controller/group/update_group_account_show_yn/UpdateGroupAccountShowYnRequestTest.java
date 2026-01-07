package com.odcloud.adapter.in.controller.group.update_group_account_show_yn;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.odcloud.application.port.in.command.UpdateGroupAccountUseYnCommand;
import com.odcloud.domain.model.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UpdateGroupAccountShowYnRequestTest {

    @Nested
    @DisplayName("[toCommand] Request를 Command로 변환하는 메서드")
    class Describe_toCommand {

        @Test
        @DisplayName("[success] Request를 Command로 변환한다")
        void success() {
            // given
            UpdateGroupAccountShowYnRequest request = UpdateGroupAccountShowYnRequest.builder()
                .showYn("Y")
                .build();

            Long groupId = 1L;
            Account account = mock(Account.class);
            given(account.getId()).willReturn(1L);
            given(account.getEmail()).willReturn("user@example.com");

            // when
            UpdateGroupAccountUseYnCommand command = request.toCommand(groupId, account);

            // then
            assertThat(command).isNotNull();
            assertThat(command.groupId()).isEqualTo(groupId);
            assertThat(command.account()).isEqualTo(account);
            assertThat(command.showYn()).isEqualTo("Y");
        }

        @Test
        @DisplayName("[success] showYn이 N인 Request를 Command로 변환한다")
        void success_showYnN() {
            // given
            UpdateGroupAccountShowYnRequest request = UpdateGroupAccountShowYnRequest.builder()
                .showYn("N")
                .build();

            Long groupId = 1L;
            Account account = mock(Account.class);

            // when
            UpdateGroupAccountUseYnCommand command = request.toCommand(groupId, account);

            // then
            assertThat(command).isNotNull();
            assertThat(command.groupId()).isEqualTo(groupId);
            assertThat(command.account()).isEqualTo(account);
            assertThat(command.showYn()).isEqualTo("N");
        }

        @Test
        @DisplayName("[success] null 값이 포함된 Request를 Command로 변환한다")
        void success_withNullValues() {
            // given
            UpdateGroupAccountShowYnRequest request = UpdateGroupAccountShowYnRequest.builder()
                .showYn(null)
                .build();

            Long groupId = null;
            Account account = null;

            // when
            UpdateGroupAccountUseYnCommand command = request.toCommand(groupId, account);

            // then
            assertThat(command).isNotNull();
            assertThat(command.groupId()).isNull();
            assertThat(command.account()).isNull();
            assertThat(command.showYn()).isNull();
        }

        @Test
        @DisplayName("[success] 빈 문자열이 포함된 Request를 Command로 변환한다")
        void success_withEmptyStrings() {
            // given
            UpdateGroupAccountShowYnRequest request = UpdateGroupAccountShowYnRequest.builder()
                .showYn("")
                .build();

            Long groupId = 1L;
            Account account = mock(Account.class);

            // when
            UpdateGroupAccountUseYnCommand command = request.toCommand(groupId, account);

            // then
            assertThat(command).isNotNull();
            assertThat(command.account()).isEqualTo(account);
            assertThat(command.showYn()).isEmpty();
        }
    }

    @Nested
    @DisplayName("[builder] Builder 패턴 테스트")
    class Describe_builder {

        @Test
        @DisplayName("[success] Builder로 Request를 생성한다")
        void success() {
            // when
            UpdateGroupAccountShowYnRequest request = UpdateGroupAccountShowYnRequest.builder()
                .showYn("Y")
                .build();

            // then
            assertThat(request).isNotNull();
            assertThat(request.showYn()).isEqualTo("Y");
        }

        @Test
        @DisplayName("[success] null 값으로 Request를 생성한다")
        void success_nullValues() {
            // when
            UpdateGroupAccountShowYnRequest request = UpdateGroupAccountShowYnRequest.builder()
                .showYn(null)
                .build();

            // then
            assertThat(request.showYn()).isNull();
        }
    }

    @Nested
    @DisplayName("[record] Record 불변성 테스트")
    class Describe_immutability {

        @Test
        @DisplayName("[success] Request는 불변 객체이다")
        void success() {
            // given
            UpdateGroupAccountShowYnRequest request1 = UpdateGroupAccountShowYnRequest.builder()
                .showYn("Y")
                .build();

            UpdateGroupAccountShowYnRequest request2 = UpdateGroupAccountShowYnRequest.builder()
                .showYn("Y")
                .build();

            // when & then
            assertThat(request1).isEqualTo(request2);
            assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
        }

        @Test
        @DisplayName("[success] 다른 값으로 생성된 Request는 동등하지 않다")
        void success_notEqual() {
            // given
            UpdateGroupAccountShowYnRequest request1 = UpdateGroupAccountShowYnRequest.builder()
                .showYn("Y")
                .build();

            UpdateGroupAccountShowYnRequest request2 = UpdateGroupAccountShowYnRequest.builder()
                .showYn("N")
                .build();

            // when & then
            assertThat(request1).isNotEqualTo(request2);
        }
    }

    @Nested
    @DisplayName("[accessor] Request accessor 메서드 테스트")
    class Describe_accessor {

        @Test
        @DisplayName("[success] showYn()로 값을 조회한다")
        void success() {
            // given
            UpdateGroupAccountShowYnRequest request = UpdateGroupAccountShowYnRequest.builder()
                .showYn("Y")
                .build();

            // when
            String showYn = request.showYn();

            // then
            assertThat(showYn).isEqualTo("Y");
        }
    }

    @Nested
    @DisplayName("[toString] Request toString 메서드 테스트")
    class Describe_toString {

        @Test
        @DisplayName("[success] toString()으로 문자열 표현을 반환한다")
        void success() {
            // given
            UpdateGroupAccountShowYnRequest request = UpdateGroupAccountShowYnRequest.builder()
                .showYn("Y")
                .build();

            // when
            String result = request.toString();

            // then
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
        }
    }
}
