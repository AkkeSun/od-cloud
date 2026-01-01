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
                "테스트 그룹",
                "owner@example.com"
            );

            LocalDateTime before = LocalDateTime.now().minusSeconds(1);

            // when
            Group group = Group.of(command);

            // then
            LocalDateTime after = LocalDateTime.now().plusSeconds(1);

            assertThat(group).isNotNull();
            assertThat(group.getOwnerEmail()).isEqualTo("owner@example.com");
            assertThat(group.getName()).isEqualTo("테스트 그룹");
            assertThat(group.getStorageUsed()).isEqualTo(0L);
            assertThat(group.getStorageTotal()).isEqualTo(3221225472L);
            assertThat(group.getRegDt()).isAfter(before);
            assertThat(group.getRegDt()).isBefore(after);
        }

        @Test
        @DisplayName("[success] null 값을 포함한 RegisterGroupCommand로부터 Group을 생성한다")
        void success_nullValues() {
            // given
            RegisterGroupCommand command = new RegisterGroupCommand(
                null,
                null
            );

            // when
            Group group = Group.of(command);

            // then
            assertThat(group).isNotNull();
            assertThat(group.getOwnerEmail()).isNull();
            assertThat(group.getName()).isNull();
        }

        @Test
        @DisplayName("[success] 빈 문자열을 포함한 RegisterGroupCommand로부터 Group을 생성한다")
        void success_emptyStrings() {
            // given
            RegisterGroupCommand command = new RegisterGroupCommand(
                "",
                ""
            );

            // when
            Group group = Group.of(command);

            // then
            assertThat(group).isNotNull();
            assertThat(group.getOwnerEmail()).isEmpty();
            assertThat(group.getName()).isEmpty();
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
            assertThat(group.getOwnerEmail()).isNull();
            assertThat(group.getName()).isNull();
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
                .name("테스트 그룹")
                .build();

            // when
            String description = group.getName();

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
            assertThat(group.getName()).isEqualTo("테스트 그룹");
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
            assertThat(group.getName()).isNull();
            assertThat(group.getRegDt()).isNull();
        }
    }

    @Nested
    @DisplayName("[increaseStorageUsed] 스토리지 사용량을 증가시키는 메서드")
    class Describe_increaseStorageUsed {

        @Test
        @DisplayName("[success] 스토리지 사용량을 증가시킨다")
        void success() {
            // given
            Group group = Group.builder()
                .id("group-123")
                .storageUsed(100L)
                .build();

            LocalDateTime before = LocalDateTime.now().minusSeconds(1);

            // when
            group.increaseStorageUsed(50L);

            // then
            LocalDateTime after = LocalDateTime.now().plusSeconds(1);
            assertThat(group.getStorageUsed()).isEqualTo(150L);
            assertThat(group.getModDt()).isAfter(before);
            assertThat(group.getModDt()).isBefore(after);
        }

        @Test
        @DisplayName("[success] storageUsed가 null일 때 스토리지 사용량을 증가시킨다")
        void success_nullStorageUsed() {
            // given
            Group group = Group.builder()
                .id("group-123")
                .storageUsed(null)
                .build();

            // when
            group.increaseStorageUsed(50L);

            // then
            assertThat(group.getStorageUsed()).isEqualTo(50L);
        }
    }

    @Nested
    @DisplayName("[decreaseStorageUsed] 스토리지 사용량을 감소시키는 메서드")
    class Describe_decreaseStorageUsed {

        @Test
        @DisplayName("[success] 스토리지 사용량을 감소시킨다")
        void success() {
            // given
            Group group = Group.builder()
                .id("group-123")
                .storageUsed(100L)
                .build();

            LocalDateTime before = LocalDateTime.now().minusSeconds(1);

            // when
            group.decreaseStorageUsed(30L);

            // then
            LocalDateTime after = LocalDateTime.now().plusSeconds(1);
            assertThat(group.getStorageUsed()).isEqualTo(70L);
            assertThat(group.getModDt()).isAfter(before);
            assertThat(group.getModDt()).isBefore(after);
        }

        @Test
        @DisplayName("[success] 감소값이 현재 사용량보다 클 때 0으로 설정한다")
        void success_negativeResult() {
            // given
            Group group = Group.builder()
                .id("group-123")
                .storageUsed(50L)
                .build();

            // when
            group.decreaseStorageUsed(100L);

            // then
            assertThat(group.getStorageUsed()).isEqualTo(0L);
        }

        @Test
        @DisplayName("[success] storageUsed가 null일 때 0으로 설정한다")
        void success_nullStorageUsed() {
            // given
            Group group = Group.builder()
                .id("group-123")
                .storageUsed(null)
                .build();

            // when
            group.decreaseStorageUsed(50L);

            // then
            assertThat(group.getStorageUsed()).isEqualTo(0L);
        }
    }

    @Nested
    @DisplayName("[canUpload] 파일 업로드 가능 여부를 확인하는 메서드")
    class Describe_canUpload {

        @Test
        @DisplayName("[success] 업로드 가능한 경우 true를 반환한다")
        void success_canUpload() {
            // given
            Group group = Group.builder()
                .id("group-123")
                .storageUsed(1000L)
                .storageTotal(3221225472L)
                .build();

            // when
            boolean result = group.canUpload(500L);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("[success] 업로드 불가능한 경우 false를 반환한다")
        void success_cannotUpload() {
            // given
            Group group = Group.builder()
                .id("group-123")
                .storageUsed(3221225000L)
                .storageTotal(3221225472L)
                .build();

            // when
            boolean result = group.canUpload(1000L);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("[success] 정확히 용량에 맞는 경우 true를 반환한다")
        void success_exactMatch() {
            // given
            Group group = Group.builder()
                .id("group-123")
                .storageUsed(3221225000L)
                .storageTotal(3221225472L)
                .build();

            // when
            boolean result = group.canUpload(472L);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("[success] storageUsed가 null일 때 업로드 가능 여부를 확인한다")
        void success_nullStorageUsed() {
            // given
            Group group = Group.builder()
                .id("group-123")
                .storageUsed(null)
                .storageTotal(3221225472L)
                .build();

            // when
            boolean result = group.canUpload(1000L);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("[success] storageTotal이 null일 때 기본값으로 업로드 가능 여부를 확인한다")
        void success_nullStorageTotal() {
            // given
            Group group = Group.builder()
                .id("group-123")
                .storageUsed(1000L)
                .storageTotal(null)
                .build();

            // when
            boolean result = group.canUpload(500L);

            // then
            assertThat(result).isTrue();
        }
    }
}
