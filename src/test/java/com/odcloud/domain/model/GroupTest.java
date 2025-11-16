package com.odcloud.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.application.port.in.command.RegisterGroupCommand;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class GroupTest {

    @Nested
    @DisplayName("[of] RegisterGroupCommand로부터 Group을 생성하는 정적 팩토리 메서드")
    class Describe_of_fromCommand {

        @Test
        @DisplayName("[success] RegisterGroupCommand로부터 Group을 생성한다")
        void success() {
            // given
            RegisterGroupCommand command = new RegisterGroupCommand(
                "group-123",
                "owner@example.com",
                "테스트 그룹"
            );

            LocalDateTime before = LocalDateTime.now().minusSeconds(1);

            // when
            Group group = Group.of(command);

            // then
            LocalDateTime after = LocalDateTime.now().plusSeconds(1);

            assertThat(group).isNotNull();
            assertThat(group.getId()).isEqualTo("group-123");
            assertThat(group.getOwnerEmail()).isEqualTo("owner@example.com");
            assertThat(group.getDescription()).isEqualTo("테스트 그룹");
            assertThat(group.getRegDt()).isAfter(before);
            assertThat(group.getRegDt()).isBefore(after);
        }

        @Test
        @DisplayName("[success] null 값을 포함한 RegisterGroupCommand로부터 Group을 생성한다")
        void success_nullValues() {
            // given
            RegisterGroupCommand command = new RegisterGroupCommand(
                null,
                null,
                null
            );

            // when
            Group group = Group.of(command);

            // then
            assertThat(group).isNotNull();
            assertThat(group.getId()).isNull();
            assertThat(group.getOwnerEmail()).isNull();
            assertThat(group.getDescription()).isNull();
        }

        @Test
        @DisplayName("[success] 빈 문자열을 포함한 RegisterGroupCommand로부터 Group을 생성한다")
        void success_emptyStrings() {
            // given
            RegisterGroupCommand command = new RegisterGroupCommand(
                "",
                "",
                ""
            );

            // when
            Group group = Group.of(command);

            // then
            assertThat(group).isNotNull();
            assertThat(group.getId()).isEmpty();
            assertThat(group.getOwnerEmail()).isEmpty();
            assertThat(group.getDescription()).isEmpty();
        }
    }

    @Nested
    @DisplayName("[of] String id로부터 Group을 생성하는 정적 팩토리 메서드")
    class Describe_of_fromId {

        @Test
        @DisplayName("[success] String id로부터 Group을 생성한다")
        void success() {
            // given
            String id = "group-123";

            // when
            Group group = Group.of(id);

            // then
            assertThat(group).isNotNull();
            assertThat(group.getId()).isEqualTo("group-123");
            assertThat(group.getOwnerEmail()).isNull();
            assertThat(group.getDescription()).isNull();
            assertThat(group.getGroupMembers()).isNull();
            assertThat(group.getRegDt()).isNull();
        }
        
        @Test
        @DisplayName("[success] 빈 문자열 id로부터 Group을 생성한다")
        void success_emptyId() {
            // when
            Group group = Group.of("");

            // then
            assertThat(group).isNotNull();
            assertThat(group.getId()).isEmpty();
        }
    }

    @Nested
    @DisplayName("[updateGroupMembers] 그룹 멤버 리스트를 업데이트하는 메서드")
    class Describe_updateGroupMembers {

        @Test
        @DisplayName("[success] 그룹 멤버 리스트를 업데이트한다")
        void success() {
            // given
            List<GroupAccount> initialMembers = Arrays.asList(
                GroupAccount.builder().id(1L).build()
            );
            Group group = Group.builder()
                .id("group-123")
                .groupMembers(initialMembers)
                .build();

            List<GroupAccount> newMembers = Arrays.asList(
                GroupAccount.builder().id(2L).build(),
                GroupAccount.builder().id(3L).build()
            );

            // when
            group.updateGroupMembers(newMembers);

            // then
            assertThat(group.getGroupMembers()).hasSize(2);
            assertThat(group.getGroupMembers().get(0).getId()).isEqualTo(2L);
            assertThat(group.getGroupMembers().get(1).getId()).isEqualTo(3L);
        }

        @Test
        @DisplayName("[success] 그룹 멤버 리스트를 빈 리스트로 업데이트한다")
        void success_emptyList() {
            // given
            List<GroupAccount> initialMembers = Arrays.asList(
                GroupAccount.builder().id(1L).build(),
                GroupAccount.builder().id(2L).build()
            );
            Group group = Group.builder()
                .id("group-123")
                .groupMembers(initialMembers)
                .build();

            // when
            group.updateGroupMembers(List.of());

            // then
            assertThat(group.getGroupMembers()).isEmpty();
        }

        @Test
        @DisplayName("[success] 그룹 멤버 리스트를 null로 업데이트한다")
        void success_null() {
            // given
            List<GroupAccount> initialMembers = Arrays.asList(
                GroupAccount.builder().id(1L).build()
            );
            Group group = Group.builder()
                .id("group-123")
                .groupMembers(initialMembers)
                .build();

            // when
            group.updateGroupMembers(null);

            // then
            assertThat(group.getGroupMembers()).isNull();
        }
    }

    @Nested
    @DisplayName("[getter] Getter 메서드 테스트")
    class Describe_getter {

        @Test
        @DisplayName("[success] getId()로 id를 조회한다")
        void success_getId() {
            // given
            Group group = Group.builder()
                .id("group-123")
                .build();

            // when
            String id = group.getId();

            // then
            assertThat(id).isEqualTo("group-123");
        }

        @Test
        @DisplayName("[success] getOwnerEmail()로 ownerEmail을 조회한다")
        void success_getOwnerEmail() {
            // given
            Group group = Group.builder()
                .ownerEmail("owner@example.com")
                .build();

            // when
            String ownerEmail = group.getOwnerEmail();

            // then
            assertThat(ownerEmail).isEqualTo("owner@example.com");
        }

        @Test
        @DisplayName("[success] getDescription()으로 description을 조회한다")
        void success_getDescription() {
            // given
            Group group = Group.builder()
                .description("테스트 그룹")
                .build();

            // when
            String description = group.getDescription();

            // then
            assertThat(description).isEqualTo("테스트 그룹");
        }

        @Test
        @DisplayName("[success] getGroupMembers()로 groupMembers를 조회한다")
        void success_getGroupMembers() {
            // given
            List<GroupAccount> groupMembers = Arrays.asList(
                GroupAccount.builder().id(1L).build()
            );
            Group group = Group.builder()
                .groupMembers(groupMembers)
                .build();

            // when
            List<GroupAccount> result = group.getGroupMembers();

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("[success] getRegDt()로 regDt를 조회한다")
        void success_getRegDt() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Group group = Group.builder()
                .regDt(now)
                .build();

            // when
            LocalDateTime regDt = group.getRegDt();

            // then
            assertThat(regDt).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("[noArgsConstructor] NoArgsConstructor 테스트")
    class Describe_noArgsConstructor {

        @Test
        @DisplayName("[success] NoArgsConstructor로 Group을 생성한다")
        void success() {
            // when
            Group group = new Group();

            // then
            assertThat(group).isNotNull();
        }
    }

    @Nested
    @DisplayName("[allArgsConstructor] AllArgsConstructor 테스트")
    class Describe_allArgsConstructor {

        @Test
        @DisplayName("[success] AllArgsConstructor로 Group을 생성한다")
        void success() {
            // given
            LocalDateTime now = LocalDateTime.now();
            List<GroupAccount> groupMembers = Arrays.asList(
                GroupAccount.builder().id(1L).build()
            );

            // when
            Group group = new Group(
                "group-123",
                "owner@example.com",
                "테스트 그룹",
                groupMembers,
                now
            );

            // then
            assertThat(group).isNotNull();
            assertThat(group.getId()).isEqualTo("group-123");
            assertThat(group.getOwnerEmail()).isEqualTo("owner@example.com");
            assertThat(group.getDescription()).isEqualTo("테스트 그룹");
            assertThat(group.getGroupMembers()).hasSize(1);
            assertThat(group.getRegDt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("[customConstructor] Custom Constructor 테스트")
    class Describe_customConstructor {

        @Test
        @DisplayName("[success] Custom Constructor로 Group을 생성한다")
        void success() {
            // given
            LocalDateTime now = LocalDateTime.now();

            // when
            Group group = new Group(
                "group-123",
                "owner@example.com",
                "테스트 그룹",
                now
            );

            // then
            assertThat(group).isNotNull();
            assertThat(group.getId()).isEqualTo("group-123");
            assertThat(group.getOwnerEmail()).isEqualTo("owner@example.com");
            assertThat(group.getDescription()).isEqualTo("테스트 그룹");
            assertThat(group.getRegDt()).isEqualTo(now);
            assertThat(group.getGroupMembers()).isNull();
        }

        @Test
        @DisplayName("[success] null 값으로 Custom Constructor로 Group을 생성한다")
        void success_nullValues() {
            // when
            Group group = new Group(null, null, null, null);

            // then
            assertThat(group).isNotNull();
            assertThat(group.getId()).isNull();
            assertThat(group.getOwnerEmail()).isNull();
            assertThat(group.getDescription()).isNull();
            assertThat(group.getRegDt()).isNull();
        }
    }
}
