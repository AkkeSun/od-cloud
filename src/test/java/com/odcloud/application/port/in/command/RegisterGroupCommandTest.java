package com.odcloud.application.port.in.command;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RegisterGroupCommandTest {

    @Nested
    @DisplayName("[builder] Command 빌더 패턴 테스트")
    class Describe_builder {

        @Test
        @DisplayName("[success] 모든 필드를 설정하여 Command를 생성한다")
        void success() {
            // when
            RegisterGroupCommand command = RegisterGroupCommand.builder()
                .id("test-group")
                .description("테스트 그룹 설명")
                .build();

            // then
            assertThat(command).isNotNull();
            assertThat(command.id()).isEqualTo("test-group");
            assertThat(command.description()).isEqualTo("테스트 그룹 설명");
        }

        @Test
        @DisplayName("[success] 다양한 ID 형식으로 Command를 생성한다")
        void success_variousIdFormats() {
            // when
            RegisterGroupCommand command1 = RegisterGroupCommand.builder()
                .id("simple-id")
                .description("Simple ID")
                .build();

            RegisterGroupCommand command2 = RegisterGroupCommand.builder()
                .id("with_underscore")
                .description("Underscore ID")
                .build();

            RegisterGroupCommand command3 = RegisterGroupCommand.builder()
                .id("123numeric")
                .description("Numeric Start ID")
                .build();

            // then
            assertThat(command1.id()).isEqualTo("simple-id");
            assertThat(command2.id()).isEqualTo("with_underscore");
            assertThat(command3.id()).isEqualTo("123numeric");
        }

        @Test
        @DisplayName("[success] null 값으로 Command를 생성한다")
        void success_nullValues() {
            // when
            RegisterGroupCommand command = RegisterGroupCommand.builder()
                .id(null)
                .description(null)
                .build();

            // then
            assertThat(command.id()).isNull();
            assertThat(command.description()).isNull();
        }

        @Test
        @DisplayName("[success] 긴 설명으로 Command를 생성한다")
        void success_longDescription() {
            // given
            String longDescription = "이것은 매우 긴 그룹 설명입니다. ".repeat(20);

            // when
            RegisterGroupCommand command = RegisterGroupCommand.builder()
                .id("long-desc")
                .description(longDescription)
                .build();

            // then
            assertThat(command.description()).isEqualTo(longDescription);
            assertThat(command.description().length()).isGreaterThan(100);
        }
    }

    @Nested
    @DisplayName("[record] Record 불변성 테스트")
    class Describe_immutability {

        @Test
        @DisplayName("[success] Command는 불변 객체이다")
        void success() {
            // given
            RegisterGroupCommand command1 = RegisterGroupCommand.builder()
                .id("test-group")
                .description("테스트 그룹")
                .build();

            RegisterGroupCommand command2 = RegisterGroupCommand.builder()
                .id("test-group")
                .description("테스트 그룹")
                .build();

            // when & then
            assertThat(command1).isEqualTo(command2);
            assertThat(command1.hashCode()).isEqualTo(command2.hashCode());
        }

        @Test
        @DisplayName("[success] 다른 값을 가진 Command는 동일하지 않다")
        void success_notEquals() {
            // given
            RegisterGroupCommand command1 = RegisterGroupCommand.builder()
                .id("group-1")
                .description("그룹 1")
                .build();

            RegisterGroupCommand command2 = RegisterGroupCommand.builder()
                .id("group-2")
                .description("그룹 2")
                .build();

            // when & then
            assertThat(command1).isNotEqualTo(command2);
        }

        @Test
        @DisplayName("[success] ID만 다른 Command는 동일하지 않다")
        void success_notEquals_differentId() {
            // given
            RegisterGroupCommand command1 = RegisterGroupCommand.builder()
                .id("group-1")
                .description("동일한 설명")
                .build();

            RegisterGroupCommand command2 = RegisterGroupCommand.builder()
                .id("group-2")
                .description("동일한 설명")
                .build();

            // when & then
            assertThat(command1).isNotEqualTo(command2);
        }

        @Test
        @DisplayName("[success] 설명만 다른 Command는 동일하지 않다")
        void success_notEquals_differentDescription() {
            // given
            RegisterGroupCommand command1 = RegisterGroupCommand.builder()
                .id("same-group")
                .description("설명 1")
                .build();

            RegisterGroupCommand command2 = RegisterGroupCommand.builder()
                .id("same-group")
                .description("설명 2")
                .build();

            // when & then
            assertThat(command1).isNotEqualTo(command2);
        }
    }
}
