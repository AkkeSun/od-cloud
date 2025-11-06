package com.odcloud.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.application.port.in.command.RegisterGroupCommand;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class GroupTest {

    @Nested
    @DisplayName("[of] RegisterGroupCommand로부터 Group을 생성하는 정적 팩토리 메서드")
    class Describe_of_from_command {

        @Test
        @DisplayName("[success] RegisterGroupCommand로 Group을 생성한다")
        void success() {
            // given
            RegisterGroupCommand command = RegisterGroupCommand.builder()
                .id("test-group")
                .description("테스트 그룹")
                .build();

            // when
            Group group = Group.of(command);

            // then
            assertThat(group).isNotNull();
            assertThat(group.id()).isEqualTo("test-group");
            assertThat(group.description()).isEqualTo("테스트 그룹");
            assertThat(group.regDt()).isNotNull();
            assertThat(group.regDt()).isBefore(LocalDateTime.now().plusSeconds(1));
        }

        @Test
        @DisplayName("[success] 등록 시간이 자동으로 설정된다")
        void success_regDateTimeSet() {
            // given
            LocalDateTime beforeCreation = LocalDateTime.now();
            RegisterGroupCommand command = RegisterGroupCommand.builder()
                .id("timestamp-test-group")
                .description("시간 테스트 그룹")
                .build();

            // when
            Group group = Group.of(command);

            // then
            LocalDateTime afterCreation = LocalDateTime.now();
            assertThat(group.regDt()).isNotNull();
            assertThat(group.regDt()).isAfterOrEqualTo(beforeCreation.minusSeconds(1));
            assertThat(group.regDt()).isBeforeOrEqualTo(afterCreation.plusSeconds(1));
        }

        @Test
        @DisplayName("[success] Command의 모든 값이 Group에 복사된다")
        void success_allFieldsCopied() {
            // given
            RegisterGroupCommand command = RegisterGroupCommand.builder()
                .id("complete-group")
                .description("완전한 그룹 설명")
                .build();

            // when
            Group group = Group.of(command);

            // then
            assertThat(group.id()).isEqualTo(command.id());
            assertThat(group.description()).isEqualTo(command.description());
        }
    }

    @Nested
    @DisplayName("[builder] Group 빌더 패턴 테스트")
    class Describe_builder {

        @Test
        @DisplayName("[success] 모든 필드를 설정하여 Group을 생성한다")
        void success_allFields() {
            // given
            LocalDateTime now = LocalDateTime.now();

            // when
            Group group = Group.builder()
                .id("test-group")
                .description("테스트 그룹 설명")
                .regDt(now)
                .build();

            // then
            assertThat(group.id()).isEqualTo("test-group");
            assertThat(group.description()).isEqualTo("테스트 그룹 설명");
            assertThat(group.regDt()).isEqualTo(now);
        }

        @Test
        @DisplayName("[success] 일부 필드만 설정하여 Group을 생성한다")
        void success_partialFields() {
            // when
            Group group = Group.builder()
                .id("partial-group")
                .build();

            // then
            assertThat(group.id()).isEqualTo("partial-group");
            assertThat(group.description()).isNull();
            assertThat(group.regDt()).isNull();
        }

        @Test
        @DisplayName("[success] 다양한 ID 형식으로 Group을 생성할 수 있다")
        void success_variousIdFormats() {
            // when
            Group group1 = Group.builder().id("simple").build();
            Group group2 = Group.builder().id("with-dash").build();
            Group group3 = Group.builder().id("with_underscore").build();
            Group group4 = Group.builder().id("123numeric").build();

            // then
            assertThat(group1.id()).isEqualTo("simple");
            assertThat(group2.id()).isEqualTo("with-dash");
            assertThat(group3.id()).isEqualTo("with_underscore");
            assertThat(group4.id()).isEqualTo("123numeric");
        }

        @Test
        @DisplayName("[success] 긴 설명을 가진 Group을 생성할 수 있다")
        void success_longDescription() {
            // given
            String longDescription = "이것은 매우 긴 그룹 설명입니다. ".repeat(10);

            // when
            Group group = Group.builder()
                .id("long-desc-group")
                .description(longDescription)
                .build();

            // then
            assertThat(group.description()).isEqualTo(longDescription);
            assertThat(group.description().length()).isGreaterThan(100);
        }
    }

    @Nested
    @DisplayName("[record] Group record 불변성 테스트")
    class Describe_record_immutability {

        @Test
        @DisplayName("[success] record로 생성된 Group은 동일한 값으로 equals가 동작한다")
        void success_equals() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Group group1 = Group.builder()
                .id("test-group")
                .description("설명")
                .regDt(now)
                .build();

            Group group2 = Group.builder()
                .id("test-group")
                .description("설명")
                .regDt(now)
                .build();

            // when & then
            assertThat(group1).isEqualTo(group2);
        }

        @Test
        @DisplayName("[success] 다른 값을 가진 Group은 equals에서 false를 반환한다")
        void success_notEquals() {
            // given
            Group group1 = Group.builder()
                .id("group-1")
                .description("설명 1")
                .build();

            Group group2 = Group.builder()
                .id("group-2")
                .description("설명 2")
                .build();

            // when & then
            assertThat(group1).isNotEqualTo(group2);
        }

        @Test
        @DisplayName("[success] hashCode가 일관되게 동작한다")
        void success_hashCode() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Group group = Group.builder()
                .id("test-group")
                .description("설명")
                .regDt(now)
                .build();

            // when
            int hashCode1 = group.hashCode();
            int hashCode2 = group.hashCode();

            // then
            assertThat(hashCode1).isEqualTo(hashCode2);
        }
    }
}
