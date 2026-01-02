package com.odcloud.application.group.port.in.command;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class DeleteGroupCommandTest {

    @Nested
    @DisplayName("[builder] Builder 패턴 테스트")
    class Describe_builder {

        @Test
        @DisplayName("[success] Builder로 모든 필드를 포함한 Command를 생성한다")
        void success_allFields() {
            // when
            DeleteGroupCommand command = DeleteGroupCommand.builder()
                .groupId("group-1")
                .currentOwnerEmail("owner@example.com")
                .build();

            // then
            assertThat(command).isNotNull();
            assertThat(command.groupId()).isEqualTo("group-1");
            assertThat(command.currentOwnerEmail()).isEqualTo("owner@example.com");
        }

        @Test
        @DisplayName("[success] null 값으로 Command를 생성한다")
        void success_nullValues() {
            // when
            DeleteGroupCommand command = DeleteGroupCommand.builder()
                .groupId(null)
                .currentOwnerEmail(null)
                .build();

            // then
            assertThat(command.groupId()).isNull();
            assertThat(command.currentOwnerEmail()).isNull();
        }
    }

    @Nested
    @DisplayName("[accessor] Record accessor 메서드 테스트")
    class Describe_accessor {

        @Test
        @DisplayName("[success] groupId()로 값을 조회한다")
        void success_groupId() {
            // given
            DeleteGroupCommand command = DeleteGroupCommand.builder()
                .groupId("group-1")
                .currentOwnerEmail("owner@example.com")
                .build();

            // when
            String groupId = command.groupId();

            // then
            assertThat(groupId).isEqualTo("group-1");
        }

        @Test
        @DisplayName("[success] currentOwnerEmail()로 값을 조회한다")
        void success_currentOwnerEmail() {
            // given
            DeleteGroupCommand command = DeleteGroupCommand.builder()
                .groupId("group-1")
                .currentOwnerEmail("owner@example.com")
                .build();

            // when
            String currentOwnerEmail = command.currentOwnerEmail();

            // then
            assertThat(currentOwnerEmail).isEqualTo("owner@example.com");
        }
    }

    @Nested
    @DisplayName("[record] Record 불변성 테스트")
    class Describe_immutability {

        @Test
        @DisplayName("[success] DeleteGroupCommand는 불변 객체이다")
        void success() {
            // given
            DeleteGroupCommand command1 = DeleteGroupCommand.builder()
                .groupId("group-1")
                .currentOwnerEmail("owner@example.com")
                .build();
            DeleteGroupCommand command2 = DeleteGroupCommand.builder()
                .groupId("group-1")
                .currentOwnerEmail("owner@example.com")
                .build();

            // when & then
            assertThat(command1).isEqualTo(command2);
            assertThat(command1.hashCode()).isEqualTo(command2.hashCode());
        }

        @Test
        @DisplayName("[success] 다른 값으로 생성된 DeleteGroupCommand는 동등하지 않다")
        void success_notEqual() {
            // given
            DeleteGroupCommand command1 = DeleteGroupCommand.builder()
                .groupId("group-1")
                .currentOwnerEmail("owner@example.com")
                .build();
            DeleteGroupCommand command2 = DeleteGroupCommand.builder()
                .groupId("group-2")
                .currentOwnerEmail("other@example.com")
                .build();

            // when & then
            assertThat(command1).isNotEqualTo(command2);
        }
    }

    @Nested
    @DisplayName("[toString] Record toString 메서드 테스트")
    class Describe_toString {

        @Test
        @DisplayName("[success] toString()으로 문자열 표현을 반환한다")
        void success() {
            // given
            DeleteGroupCommand command = DeleteGroupCommand.builder()
                .groupId("group-1")
                .currentOwnerEmail("owner@example.com")
                .build();

            // when
            String result = command.toString();

            // then
            assertThat(result).isNotNull();
            assertThat(result).contains("DeleteGroupCommand");
            assertThat(result).contains("group-1");
            assertThat(result).contains("owner@example.com");
        }

        @Test
        @DisplayName("[success] null 값을 포함한 toString()을 반환한다")
        void success_nullValues() {
            // given
            DeleteGroupCommand command = DeleteGroupCommand.builder()
                .groupId(null)
                .currentOwnerEmail(null)
                .build();

            // when
            String result = command.toString();

            // then
            assertThat(result).isNotNull();
            assertThat(result).contains("DeleteGroupCommand");
            assertThat(result).contains("null");
        }
    }
}
