package com.odcloud.adapter.in.register_group;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.odcloud.application.port.in.command.RegisterGroupCommand;
import com.odcloud.domain.model.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RegisterGroupRequestTest {

    @Nested
    @DisplayName("[toCommand] Request를 Command로 변환하는 메서드")
    class Describe_toCommand {

        @Test
        @DisplayName("[success] Request를 Command로 변환한다")
        void success() {
            // given
            RegisterGroupRequest request = RegisterGroupRequest.builder()
                .id("group-123")
                .description("테스트 그룹")
                .build();

            Account account = mock(Account.class);
            given(account.getEmail()).willReturn("test@example.com");

            // when
            RegisterGroupCommand command = request.toCommand(account);

            // then
            assertThat(command).isNotNull();
            assertThat(command.id()).isEqualTo("group-123");
            assertThat(command.ownerEmail()).isEqualTo("test@example.com");
            assertThat(command.description()).isEqualTo("테스트 그룹");
        }

        @Test
        @DisplayName("[success] null 값이 포함된 Request를 Command로 변환한다")
        void success_withNullValues() {
            // given
            RegisterGroupRequest request = RegisterGroupRequest.builder()
                .id(null)
                .description(null)
                .build();

            Account account = mock(Account.class);
            given(account.getEmail()).willReturn(null);

            // when
            RegisterGroupCommand command = request.toCommand(account);

            // then
            assertThat(command).isNotNull();
            assertThat(command.id()).isNull();
            assertThat(command.ownerEmail()).isNull();
            assertThat(command.description()).isNull();
        }

        @Test
        @DisplayName("[success] 빈 문자열이 포함된 Request를 Command로 변환한다")
        void success_withEmptyStrings() {
            // given
            RegisterGroupRequest request = RegisterGroupRequest.builder()
                .id("")
                .description("")
                .build();

            Account account = mock(Account.class);
            given(account.getEmail()).willReturn("");

            // when
            RegisterGroupCommand command = request.toCommand(account);

            // then
            assertThat(command).isNotNull();
            assertThat(command.id()).isEmpty();
            assertThat(command.ownerEmail()).isEmpty();
            assertThat(command.description()).isEmpty();
        }
    }

    @Nested
    @DisplayName("[builder] Builder 패턴 테스트")
    class Describe_builder {

        @Test
        @DisplayName("[success] Builder로 Request를 생성한다")
        void success() {
            // when
            RegisterGroupRequest request = RegisterGroupRequest.builder()
                .id("group-123")
                .description("테스트 그룹")
                .build();

            // then
            assertThat(request).isNotNull();
            assertThat(request.id()).isEqualTo("group-123");
            assertThat(request.description()).isEqualTo("테스트 그룹");
        }

        @Test
        @DisplayName("[success] null 값으로 Request를 생성한다")
        void success_nullValues() {
            // when
            RegisterGroupRequest request = RegisterGroupRequest.builder()
                .id(null)
                .description(null)
                .build();

            // then
            assertThat(request.id()).isNull();
            assertThat(request.description()).isNull();
        }
    }

    @Nested
    @DisplayName("[record] Record 불변성 테스트")
    class Describe_immutability {

        @Test
        @DisplayName("[success] Request는 불변 객체이다")
        void success() {
            // given
            RegisterGroupRequest request1 = RegisterGroupRequest.builder()
                .id("group-123")
                .description("테스트 그룹")
                .build();

            RegisterGroupRequest request2 = RegisterGroupRequest.builder()
                .id("group-123")
                .description("테스트 그룹")
                .build();

            // when & then
            assertThat(request1).isEqualTo(request2);
            assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
        }

        @Test
        @DisplayName("[success] 다른 값으로 생성된 Request는 동등하지 않다")
        void success_notEqual() {
            // given
            RegisterGroupRequest request1 = RegisterGroupRequest.builder()
                .id("group-123")
                .description("테스트 그룹")
                .build();

            RegisterGroupRequest request2 = RegisterGroupRequest.builder()
                .id("group-456")
                .description("다른 그룹")
                .build();

            // when & then
            assertThat(request1).isNotEqualTo(request2);
        }
    }

    @Nested
    @DisplayName("[accessor] Request accessor 메서드 테스트")
    class Describe_accessor {

        @Test
        @DisplayName("[success] id()로 값을 조회한다")
        void success_id() {
            // given
            RegisterGroupRequest request = RegisterGroupRequest.builder()
                .id("group-123")
                .description("테스트 그룹")
                .build();

            // when
            String id = request.id();

            // then
            assertThat(id).isEqualTo("group-123");
        }

        @Test
        @DisplayName("[success] description()로 값을 조회한다")
        void success_description() {
            // given
            RegisterGroupRequest request = RegisterGroupRequest.builder()
                .id("group-123")
                .description("테스트 그룹")
                .build();

            // when
            String description = request.description();

            // then
            assertThat(description).isEqualTo("테스트 그룹");
        }
    }

    @Nested
    @DisplayName("[toString] Request toString 메서드 테스트")
    class Describe_toString {

        @Test
        @DisplayName("[success] toString()으로 문자열 표현을 반환한다")
        void success() {
            // given
            RegisterGroupRequest request = RegisterGroupRequest.builder()
                .id("group-123")
                .description("테스트 그룹")
                .build();

            // when
            String result = request.toString();

            // then
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
        }
    }
}
