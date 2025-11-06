package com.odcloud.adapter.in.register_group;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.application.port.in.command.RegisterGroupCommand;
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
                .id("test-group")
                .description("테스트 그룹")
                .build();

            // when
            RegisterGroupCommand command = request.toCommand();

            // then
            assertThat(command).isNotNull();
            assertThat(command.id()).isEqualTo("test-group");
            assertThat(command.description()).isEqualTo("테스트 그룹");
        }

        @Test
        @DisplayName("[success] 다양한 ID 형식의 Request를 Command로 변환한다")
        void success_variousIdFormats() {
            // given
            RegisterGroupRequest request = RegisterGroupRequest.builder()
                .id("group-with-dash")
                .description("대시 포함 그룹")
                .build();

            // when
            RegisterGroupCommand command = request.toCommand();

            // then
            assertThat(command.id()).isEqualTo("group-with-dash");
        }

        @Test
        @DisplayName("[success] 긴 설명을 가진 Request를 Command로 변환한다")
        void success_longDescription() {
            // given
            String longDescription = "이것은 매우 긴 그룹 설명입니다. ".repeat(10);
            RegisterGroupRequest request = RegisterGroupRequest.builder()
                .id("long-desc-group")
                .description(longDescription)
                .build();

            // when
            RegisterGroupCommand command = request.toCommand();

            // then
            assertThat(command.description()).isEqualTo(longDescription);
        }
    }

    @Nested
    @DisplayName("[builder] Request 빌더 패턴 테스트")
    class Describe_builder {

        @Test
        @DisplayName("[success] 모든 필드를 설정하여 Request를 생성한다")
        void success() {
            // when
            RegisterGroupRequest request = RegisterGroupRequest.builder()
                .id("test-group")
                .description("테스트 그룹 설명")
                .build();

            // then
            assertThat(request.id()).isEqualTo("test-group");
            assertThat(request.description()).isEqualTo("테스트 그룹 설명");
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

        @Test
        @DisplayName("[success] 빈 문자열로 Request를 생성한다")
        void success_emptyStrings() {
            // when
            RegisterGroupRequest request = RegisterGroupRequest.builder()
                .id("")
                .description("")
                .build();

            // then
            assertThat(request.id()).isEmpty();
            assertThat(request.description()).isEmpty();
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
                .id("test-group")
                .description("테스트 그룹")
                .build();

            RegisterGroupRequest request2 = RegisterGroupRequest.builder()
                .id("test-group")
                .description("테스트 그룹")
                .build();

            // when & then
            assertThat(request1).isEqualTo(request2);
            assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
        }

        @Test
        @DisplayName("[success] 다른 값을 가진 Request는 동일하지 않다")
        void success_notEquals() {
            // given
            RegisterGroupRequest request1 = RegisterGroupRequest.builder()
                .id("group-1")
                .description("그룹 1")
                .build();

            RegisterGroupRequest request2 = RegisterGroupRequest.builder()
                .id("group-2")
                .description("그룹 2")
                .build();

            // when & then
            assertThat(request1).isNotEqualTo(request2);
        }
    }
}
