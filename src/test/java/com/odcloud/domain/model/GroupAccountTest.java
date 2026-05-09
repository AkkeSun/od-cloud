package com.odcloud.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class GroupAccountTest {

    @Nested
    @DisplayName("[ofGroupOwner] Group과 Account로부터 소유자 GroupAccount를 생성하는 정적 팩토리 메서드")
    class Describe_ofGroupOwner {

        @Test
        @DisplayName("[success] Group과 Account로부터 소유자 GroupAccount를 생성한다")
        void success() {
            // given
            Group group = Group.builder()
                .id(123L)
                .build();
            Account account = Account.builder()
                .id(100L)
                .build();

            LocalDateTime before = LocalDateTime.now().minusSeconds(1);

            // when
            GroupAccount groupAccount = GroupAccount.ofGroupOwner(group, account);

            // then
            LocalDateTime after = LocalDateTime.now().plusSeconds(1);

            assertThat(groupAccount).isNotNull();
            assertThat(groupAccount.getGroupId()).isEqualTo(123L);
            assertThat(groupAccount.getAccountId()).isEqualTo(100L);
            assertThat(groupAccount.getStatus()).isEqualTo("ACTIVE");
            assertThat(groupAccount.getModDt()).isAfter(before);
            assertThat(groupAccount.getModDt()).isBefore(after);
            assertThat(groupAccount.getRegDt()).isAfter(before);
            assertThat(groupAccount.getRegDt()).isBefore(after);
        }

        @Test
        @DisplayName("[success] null 값을 가진 Group과 Account로부터 소유자 GroupAccount를 생성한다")
        void success_nullValues() {
            // given
            Group group = Group.builder()
                .id(null)
                .build();
            Account account = Account.builder()
                .id(null)
                .build();

            // when
            GroupAccount groupAccount = GroupAccount.ofGroupOwner(group, account);

            // then
            assertThat(groupAccount).isNotNull();
            assertThat(groupAccount.getGroupId()).isNull();
            assertThat(groupAccount.getAccountId()).isNull();
            assertThat(groupAccount.getStatus()).isEqualTo("ACTIVE");
        }
    }

    @Nested
    @DisplayName("[updateStatus] status를 업데이트하는 메서드")
    class Describe_updateStatus {

        @Test
        @DisplayName("[success] status를 업데이트하고 modDt를 설정한다")
        void success() {
            // given
            LocalDateTime initialModDt = LocalDateTime.now().minusDays(1);
            GroupAccount groupAccount = GroupAccount.builder()
                .status("PENDING")
                .modDt(initialModDt)
                .build();

            LocalDateTime before = LocalDateTime.now().minusSeconds(1);

            // when
            groupAccount.updateStatus("ACTIVE", null);

            // then
            LocalDateTime after = LocalDateTime.now().plusSeconds(1);

            assertThat(groupAccount.getStatus()).isEqualTo("ACTIVE");
            assertThat(groupAccount.getModDt()).isAfter(before);
            assertThat(groupAccount.getModDt()).isBefore(after);
            assertThat(groupAccount.getModDt()).isAfter(initialModDt);
        }
    }

    @Nested
    @DisplayName("[isActive] status가 ACTIVE 인지 확인하는 메서드")
    class Describe_isActive {

        @Test
        @DisplayName("[success] status가 ACTIVE면 true를 반환한다")
        void success_active() {
            // given
            GroupAccount groupAccount = GroupAccount.builder()
                .status("ACTIVE")
                .build();

            // when
            boolean result = groupAccount.isActive();

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("[success] status가 PENDING이면 false를 반환한다")
        void success_pending() {
            // given
            GroupAccount groupAccount = GroupAccount.builder()
                .status("PENDING")
                .build();

            // when
            boolean result = groupAccount.isActive();

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("[success] status가 빈 문자열이면 false를 반환한다")
        void success_emptyString() {
            // given
            GroupAccount groupAccount = GroupAccount.builder()
                .status("")
                .build();

            // when
            boolean result = groupAccount.isActive();

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("[success] status가 approved(소문자)이면 false를 반환한다")
        void success_lowercase() {
            // given
            GroupAccount groupAccount = GroupAccount.builder()
                .status("approved")
                .build();

            // when
            boolean result = groupAccount.isActive();

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("[getter] Getter 메서드 테스트")
    class Describe_getter {

        @Test
        @DisplayName("[success] getId()로 id를 조회한다")
        void success_getId() {
            // given
            GroupAccount groupAccount = GroupAccount.builder()
                .id(1L)
                .build();

            // when
            Long id = groupAccount.getId();

            // then
            assertThat(id).isEqualTo(1L);
        }

        @Test
        @DisplayName("[success] getGroupId()로 groupId를 조회한다")
        void success_getGroupId() {
            // given
            GroupAccount groupAccount = GroupAccount.builder()
                .groupId(123L)
                .build();

            // when
            Long groupId = groupAccount.getGroupId();

            // then
            assertThat(groupId).isEqualTo(123L);
        }

        @Test
        @DisplayName("[success] getAccountId()로 accountId를 조회한다")
        void success_getAccountId() {
            // given
            GroupAccount groupAccount = GroupAccount.builder()
                .accountId(100L)
                .build();

            // when
            Long accountId = groupAccount.getAccountId();

            // then
            assertThat(accountId).isEqualTo(100L);
        }

        @Test
        @DisplayName("[success] getGroupName()으로 groupName을 조회한다")
        void success_getGroupName() {
            // given
            GroupAccount groupAccount = GroupAccount.builder()
                .groupName("테스트 그룹")
                .build();

            // when
            String groupName = groupAccount.getGroupName();

            // then
            assertThat(groupName).isEqualTo("테스트 그룹");
        }

        @Test
        @DisplayName("[success] getNickName()으로 nickName을 조회한다")
        void success_getNickName() {
            // given
            GroupAccount groupAccount = GroupAccount.builder()
                .nickName("테스터")
                .build();

            // when
            String nickName = groupAccount.getNickName();

            // then
            assertThat(nickName).isEqualTo("테스터");
        }

        @Test
        @DisplayName("[success] getEmail()로 email을 조회한다")
        void success_getEmail() {
            // given
            GroupAccount groupAccount = GroupAccount.builder()
                .email("test@example.com")
                .build();

            // when
            String email = groupAccount.getEmail();

            // then
            assertThat(email).isEqualTo("test@example.com");
        }

        @Test
        @DisplayName("[success] getPicture()로 picture를 조회한다")
        void success_getPicture() {
            // given
            GroupAccount groupAccount = GroupAccount.builder()
                .picture("https://example.com/profile.jpg")
                .build();

            // when
            String picture = groupAccount.getPicture();

            // then
            assertThat(picture).isEqualTo("https://example.com/profile.jpg");
        }

        @Test
        @DisplayName("[success] getStatus()로 status를 조회한다")
        void success_getStatus() {
            // given
            GroupAccount groupAccount = GroupAccount.builder()
                .status("ACTIVE")
                .build();

            // when
            String status = groupAccount.getStatus();

            // then
            assertThat(status).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("[success] getModDt()로 modDt를 조회한다")
        void success_getModDt() {
            // given
            LocalDateTime now = LocalDateTime.now();
            GroupAccount groupAccount = GroupAccount.builder()
                .modDt(now)
                .build();

            // when
            LocalDateTime modDt = groupAccount.getModDt();

            // then
            assertThat(modDt).isEqualTo(now);
        }

        @Test
        @DisplayName("[success] getRegDt()로 regDt를 조회한다")
        void success_getRegDt() {
            // given
            LocalDateTime now = LocalDateTime.now();
            GroupAccount groupAccount = GroupAccount.builder()
                .regDt(now)
                .build();

            // when
            LocalDateTime regDt = groupAccount.getRegDt();

            // then
            assertThat(regDt).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("[noArgsConstructor] NoArgsConstructor 테스트")
    class Describe_noArgsConstructor {

        @Test
        @DisplayName("[success] NoArgsConstructor로 GroupAccount를 생성한다")
        void success() {
            // when
            GroupAccount groupAccount = new GroupAccount();

            // then
            assertThat(groupAccount).isNotNull();
        }
    }

    @Nested
    @DisplayName("[customConstructor] Custom Constructor 테스트")
    class Describe_customConstructor {

        @Test
        @DisplayName("[success] Custom Constructor로 GroupAccount를 생성한다")
        void success() {
            // given
            LocalDateTime now = LocalDateTime.now();

            // when
            GroupAccount groupAccount = new GroupAccount(
                1L,
                123L,
                100L,
                "테스터",
                "test@example.com",
                "https://example.com/test.jpg",
                "ACTIVE",
                null,
                now,
                now
            );

            // then
            assertThat(groupAccount).isNotNull();
            assertThat(groupAccount.getId()).isEqualTo(1L);
            assertThat(groupAccount.getGroupId()).isEqualTo(123L);
            assertThat(groupAccount.getAccountId()).isEqualTo(100L);
            assertThat(groupAccount.getNickName()).isEqualTo("테스터");
            assertThat(groupAccount.getEmail()).isEqualTo("test@example.com");
            assertThat(groupAccount.getPicture()).isEqualTo("https://example.com/test.jpg");
            assertThat(groupAccount.getStatus()).isEqualTo("ACTIVE");
            assertThat(groupAccount.getMemo()).isNull();
            assertThat(groupAccount.getModDt()).isEqualTo(now);
            assertThat(groupAccount.getRegDt()).isEqualTo(now);
        }

        @Test
        @DisplayName("[success] null 값으로 Custom Constructor로 GroupAccount를 생성한다")
        void success_nullValues() {
            // when
            GroupAccount groupAccount = new GroupAccount(
                null, null, null, null, null, null, null, null, null, null
            );

            // then
            assertThat(groupAccount).isNotNull();
            assertThat(groupAccount.getId()).isNull();
            assertThat(groupAccount.getGroupId()).isNull();
            assertThat(groupAccount.getAccountId()).isNull();
            assertThat(groupAccount.getNickName()).isNull();
            assertThat(groupAccount.getEmail()).isNull();
            assertThat(groupAccount.getPicture()).isNull();
            assertThat(groupAccount.getStatus()).isNull();
            assertThat(groupAccount.getMemo()).isNull();
            assertThat(groupAccount.getModDt()).isNull();
            assertThat(groupAccount.getRegDt()).isNull();
        }
    }
}
