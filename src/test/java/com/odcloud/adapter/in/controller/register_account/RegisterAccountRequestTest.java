package com.odcloud.adapter.in.controller.register_account;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.application.port.in.command.RegisterAccountCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RegisterAccountRequestTest {

    @Nested
    @DisplayName("[toCommand] Request를 Command로 변환하는 메서드")
    class Describe_toCommand {

        @Test
        @DisplayName("[success] Request를 Command로 변환한다")
        void success() {
            // given
            RegisterAccountRequest request = RegisterAccountRequest.builder()
                .name("홍길동")
                .groupId("group-123")
                .build();

            String googleAuthorization = "Bearer google-token-123";

            // when
            RegisterAccountCommand command = request.toCommand(googleAuthorization);

            // then
            assertThat(command).isNotNull();
            assertThat(command.googleAuthorization()).isEqualTo(googleAuthorization);
            assertThat(command.name()).isEqualTo("홍길동");
            assertThat(command.groupId()).isEqualTo("group-123");
        }

        @Test
        @DisplayName("[success] null 값이 포함된 Request를 Command로 변환한다")
        void success_withNullValues() {
            // given
            RegisterAccountRequest request = RegisterAccountRequest.builder()
                .name(null)
                .groupId(null)
                .build();

            String googleAuthorization = null;

            // when
            RegisterAccountCommand command = request.toCommand(googleAuthorization);

            // then
            assertThat(command).isNotNull();
            assertThat(command.googleAuthorization()).isNull();
            assertThat(command.name()).isNull();
            assertThat(command.groupId()).isNull();
        }

        @Test
        @DisplayName("[success] 빈 문자열이 포함된 Request를 Command로 변환한다")
        void success_withEmptyStrings() {
            // given
            RegisterAccountRequest request = RegisterAccountRequest.builder()
                .name("")
                .groupId("")
                .build();

            String googleAuthorization = "";

            // when
            RegisterAccountCommand command = request.toCommand(googleAuthorization);

            // then
            assertThat(command).isNotNull();
            assertThat(command.googleAuthorization()).isEmpty();
            assertThat(command.name()).isEmpty();
            assertThat(command.groupId()).isEmpty();
        }
    }

    @Nested
    @DisplayName("[builder] Builder 패턴 테스트")
    class Describe_builder {

        @Test
        @DisplayName("[success] Builder로 Request를 생성한다")
        void success() {
            // when
            RegisterAccountRequest request = RegisterAccountRequest.builder()
                .name("홍길동")
                .groupId("group-123")
                .build();

            // then
            assertThat(request).isNotNull();
            assertThat(request.name()).isEqualTo("홍길동");
            assertThat(request.groupId()).isEqualTo("group-123");
        }

        @Test
        @DisplayName("[success] null 값으로 Request를 생성한다")
        void success_nullValues() {
            // when
            RegisterAccountRequest request = RegisterAccountRequest.builder()
                .name(null)
                .groupId(null)
                .build();

            // then
            assertThat(request.name()).isNull();
            assertThat(request.groupId()).isNull();
        }
    }

    @Nested
    @DisplayName("[record] Record 불변성 테스트")
    class Describe_immutability {

        @Test
        @DisplayName("[success] Request는 불변 객체이다")
        void success() {
            // given
            RegisterAccountRequest request1 = RegisterAccountRequest.builder()
                .name("홍길동")
                .groupId("group-123")
                .build();

            RegisterAccountRequest request2 = RegisterAccountRequest.builder()
                .name("홍길동")
                .groupId("group-123")
                .build();

            // when & then
            assertThat(request1).isEqualTo(request2);
            assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
        }

        @Test
        @DisplayName("[success] 다른 값으로 생성된 Request는 동등하지 않다")
        void success_notEqual() {
            // given
            RegisterAccountRequest request1 = RegisterAccountRequest.builder()
                .name("홍길동")
                .groupId("group-123")
                .build();

            RegisterAccountRequest request2 = RegisterAccountRequest.builder()
                .name("김철수")
                .groupId("group-456")
                .build();

            // when & then
            assertThat(request1).isNotEqualTo(request2);
        }
    }

    @Nested
    @DisplayName("[accessor] Request accessor 메서드 테스트")
    class Describe_accessor {

        @Test
        @DisplayName("[success] name()으로 값을 조회한다")
        void success_name() {
            // given
            RegisterAccountRequest request = RegisterAccountRequest.builder()
                .name("홍길동")
                .groupId("group-123")
                .build();

            // when
            String name = request.name();

            // then
            assertThat(name).isEqualTo("홍길동");
        }

        @Test
        @DisplayName("[success] groupId()로 값을 조회한다")
        void success_groupId() {
            // given
            RegisterAccountRequest request = RegisterAccountRequest.builder()
                .name("홍길동")
                .groupId("group-123")
                .build();

            // when
            String groupId = request.groupId();

            // then
            assertThat(groupId).isEqualTo("group-123");
        }
    }

    @Nested
    @DisplayName("[toString] Request toString 메서드 테스트")
    class Describe_toString {

        @Test
        @DisplayName("[success] toString()으로 문자열 표현을 반환한다")
        void success() {
            // given
            RegisterAccountRequest request = RegisterAccountRequest.builder()
                .name("홍길동")
                .groupId("group-123")
                .build();

            // when
            String result = request.toString();

            // then
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
        }
    }
}
