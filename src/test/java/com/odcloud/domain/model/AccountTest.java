package com.odcloud.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.odcloud.adapter.out.client.google.GoogleUserInfoResponse;
import com.odcloud.application.account.port.in.command.RegisterAccountCommand;
import io.jsonwebtoken.Claims;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class AccountTest {

    @Nested
    @DisplayName("[of] Claims로부터 Account를 생성하는 정적 팩토리 메서드")
    class Describe_of_fromClaims {

        @Test
        @DisplayName("[success] Claims로부터 Account를 생성한다")
        void success() {
            // given
            Claims claims = mock(Claims.class);
            when(claims.getSubject()).thenReturn("test@example.com");
            when(claims.get("id")).thenReturn(1L);
            when(claims.get("groups")).thenReturn(Arrays.asList(
                java.util.Map.of("id", 1L, "name", "그룹1"),
                java.util.Map.of("id", 2L, "name", "그룹2")
            ));
            when(claims.get("nickname")).thenReturn("nickname");
            when(claims.get("picture")).thenReturn("picture");

            // when
            Account account = Account.of(claims);

            // then
            assertThat(account).isNotNull();
            assertThat(account.getEmail()).isEqualTo("test@example.com");
            assertThat(account.getId()).isEqualTo(1L);
            assertThat(account.getGroups()).hasSize(2);
            assertThat(account.getGroups().get(0).getId()).isEqualTo(1L);
            assertThat(account.getGroups().get(0).getName()).isEqualTo("그룹1");
            assertThat(account.getGroups().get(1).getId()).isEqualTo(2L);
            assertThat(account.getGroups().get(1).getName()).isEqualTo("그룹2");
        }

        @Test
        @DisplayName("[success] Integer 타입의 id를 가진 Claims로부터 Account를 생성한다")
        void success_integerId() {
            // given
            Claims claims = mock(Claims.class);
            when(claims.getSubject()).thenReturn("test@example.com");
            when(claims.get("id")).thenReturn(1);
            when(claims.get("groups")).thenReturn(Arrays.asList(
                java.util.Map.of("id", 1, "name", "그룹1")
            ));
            when(claims.get("nickname")).thenReturn("nickname");
            when(claims.get("picture")).thenReturn("picture");

            // when
            Account account = Account.of(claims);

            // then
            assertThat(account).isNotNull();
            assertThat(account.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("[success] 빈 그룹 리스트를 가진 Claims로부터 Account를 생성한다")
        void success_emptyGroups() {
            // given
            Claims claims = mock(Claims.class);
            when(claims.getSubject()).thenReturn("test@example.com");
            when(claims.get("id")).thenReturn(1L);
            when(claims.get("groups")).thenReturn(List.of());
            when(claims.get("nickname")).thenReturn("nickname");
            when(claims.get("picture")).thenReturn("picture");

            // when
            Account account = Account.of(claims);

            // then
            assertThat(account).isNotNull();
            assertThat(account.getGroups()).isEmpty();
        }
    }

    @Nested
    @DisplayName("[of] GoogleUserInfoResponse와 RegisterAccountCommand로부터 Account를 생성하는 정적 팩토리 메서드")
    class Describe_of_fromGoogleUserInfo {

        @Test
        @DisplayName("[success] GoogleUserInfoResponse와 RegisterAccountCommand로부터 Account를 생성한다")
        void success() {
            // given
            GoogleUserInfoResponse userInfo = GoogleUserInfoResponse.builder()
                .email("test@example.com")
                .name("테스터")
                .picture("https://example.com/picture.jpg")
                .build();
            RegisterAccountCommand command = new RegisterAccountCommand(
                "Bearer google-token",
                "홍길동",
                1L,
                null
            );

            LocalDateTime before = LocalDateTime.now().minusSeconds(1);

            // when
            Account account = Account.of(userInfo, command);

            // then
            LocalDateTime after = LocalDateTime.now().plusSeconds(1);

            assertThat(account).isNotNull();
            assertThat(account.getEmail()).isEqualTo("test@example.com");
            assertThat(account.getNickname()).isEqualTo("테스터");
            assertThat(account.getName()).isEqualTo("홍길동");
            assertThat(account.getPicture()).isEqualTo("https://example.com/picture.jpg");
            assertThat(account.getGroups()).hasSize(1);
            assertThat(account.getGroups().get(0).getId()).isEqualTo(1L);
            assertThat(account.getRegDt()).isAfter(before);
            assertThat(account.getRegDt()).isBefore(after);
        }
    }

    @Nested
    @DisplayName("[getGroupIds] 그룹 ID 리스트를 반환하는 메서드")
    class Describe_getGroupIds {

        @Test
        @DisplayName("[success] 그룹 ID 리스트를 반환한다")
        void success() {
            // given
            List<Group> groups = Arrays.asList(
                Group.builder().id(1L).build(),
                Group.builder().id(2L).build(),
                Group.builder().id(3L).build()
            );
            Account account = Account.builder()
                .groups(groups)
                .build();

            // when
            List<Long> groupIds = account.getGroupIds();

            // then
            assertThat(groupIds).hasSize(3);
            assertThat(groupIds).containsExactly(1L, 2L, 3L);
        }

        @Test
        @DisplayName("[success] 빈 그룹 리스트에서 빈 ID 리스트를 반환한다")
        void success_emptyList() {
            // given
            Account account = Account.builder()
                .groups(List.of())
                .build();

            // when
            List<Long> groupIds = account.getGroupIds();

            // then
            assertThat(groupIds).isEmpty();
        }

        @Test
        @DisplayName("[success] null ID를 가진 그룹들의 ID 리스트를 반환한다")
        void success_nullIds() {
            // given
            List<Group> groups = Arrays.asList(
                Group.builder().id(null).build(),
                Group.builder().id(2L).build(),
                Group.builder().id(null).build()
            );
            Account account = Account.builder()
                .groups(groups)
                .build();

            // when
            List<Long> groupIds = account.getGroupIds();

            // then
            assertThat(groupIds).hasSize(3);
            assertThat(groupIds).containsExactly(null, 2L, null);
        }
    }

    @Nested
    @DisplayName("[updateGroups] 그룹 리스트를 업데이트하는 메서드")
    class Describe_updateGroups {

        @Test
        @DisplayName("[success] 그룹 리스트를 업데이트한다")
        void success() {
            // given
            List<Group> initialGroups = Arrays.asList(
                Group.builder().id(1L).build()
            );
            Account account = Account.builder()
                .groups(initialGroups)
                .build();

            List<Group> newGroups = Arrays.asList(
                Group.builder().id(2L).build(),
                Group.builder().id(3L).build()
            );

            // when
            account.updateGroups(newGroups);

            // then
            assertThat(account.getGroups()).hasSize(2);
            assertThat(account.getGroups().get(0).getId()).isEqualTo(2L);
            assertThat(account.getGroups().get(1).getId()).isEqualTo(3L);
        }

        @Test
        @DisplayName("[success] 그룹 리스트를 빈 리스트로 업데이트한다")
        void success_emptyList() {
            // given
            List<Group> initialGroups = Arrays.asList(
                Group.builder().id(1L).build(),
                Group.builder().id(2L).build()
            );
            Account account = Account.builder()
                .groups(initialGroups)
                .build();

            // when
            account.updateGroups(List.of());

            // then
            assertThat(account.getGroups()).isEmpty();
        }

        @Test
        @DisplayName("[success] 그룹 리스트를 null로 업데이트한다")
        void success_null() {
            // given
            List<Group> initialGroups = Arrays.asList(
                Group.builder().id(1L).build()
            );
            Account account = Account.builder()
                .groups(initialGroups)
                .build();

            // when
            account.updateGroups(null);

            // then
            assertThat(account.getGroups()).isNull();
        }
    }

    @Nested
    @DisplayName("[getter] Getter 메서드 테스트")
    class Describe_getter {

        @Test
        @DisplayName("[success] getId()로 id를 조회한다")
        void success_getId() {
            // given
            Account account = Account.builder()
                .id(1L)
                .build();

            // when
            Long id = account.getId();

            // then
            assertThat(id).isEqualTo(1L);
        }

        @Test
        @DisplayName("[success] getEmail()로 email을 조회한다")
        void success_getEmail() {
            // given
            Account account = Account.builder()
                .email("test@example.com")
                .build();

            // when
            String email = account.getEmail();

            // then
            assertThat(email).isEqualTo("test@example.com");
        }

        @Test
        @DisplayName("[success] getNickname()으로 nickname을 조회한다")
        void success_getNickname() {
            // given
            Account account = Account.builder()
                .nickname("테스터")
                .build();

            // when
            String nickname = account.getNickname();

            // then
            assertThat(nickname).isEqualTo("테스터");
        }

        @Test
        @DisplayName("[success] getName()으로 name을 조회한다")
        void success_getName() {
            // given
            Account account = Account.builder()
                .name("홍길동")
                .build();

            // when
            String name = account.getName();

            // then
            assertThat(name).isEqualTo("홍길동");
        }

        @Test
        @DisplayName("[success] getPicture()로 picture를 조회한다")
        void success_getPicture() {
            // given
            Account account = Account.builder()
                .picture("https://example.com/picture.jpg")
                .build();

            // when
            String picture = account.getPicture();

            // then
            assertThat(picture).isEqualTo("https://example.com/picture.jpg");
        }

        @Test
        @DisplayName("[success] getGroups()로 groups를 조회한다")
        void success_getGroups() {
            // given
            List<Group> groups = Arrays.asList(
                Group.builder().id(1L).build()
            );
            Account account = Account.builder()
                .groups(groups)
                .build();

            // when
            List<Group> result = account.getGroups();

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("[success] getModDt()로 modDt를 조회한다")
        void success_getModDt() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Account account = Account.builder()
                .modDt(now)
                .build();

            // when
            LocalDateTime modDt = account.getModDt();

            // then
            assertThat(modDt).isEqualTo(now);
        }

        @Test
        @DisplayName("[success] getRegDt()로 regDt를 조회한다")
        void success_getRegDt() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Account account = Account.builder()
                .regDt(now)
                .build();

            // when
            LocalDateTime regDt = account.getRegDt();

            // then
            assertThat(regDt).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("[noArgsConstructor] NoArgsConstructor 테스트")
    class Describe_noArgsConstructor {

        @Test
        @DisplayName("[success] NoArgsConstructor로 Account를 생성한다")
        void success() {
            // when
            Account account = new Account();

            // then
            assertThat(account).isNotNull();
        }
    }

    @Nested
    @DisplayName("[allArgsConstructor] AllArgsConstructor 테스트")
    class Describe_allArgsConstructor {

        @Test
        @DisplayName("[success] AllArgsConstructor로 Account를 생성한다")
        void success() {
            // given
            LocalDateTime now = LocalDateTime.now();
            List<Group> groups = Arrays.asList(
                Group.builder().id(2L).build()
            );

            // when
            Account account = new Account(
                1L,
                "test@example.com",
                "테스터",
                "홍길동",
                "https://example.com/picture.jpg",
                groups,
                now,
                now
            );

            // then
            assertThat(account).isNotNull();
            assertThat(account.getId()).isEqualTo(1L);
            assertThat(account.getEmail()).isEqualTo("test@example.com");
            assertThat(account.getNickname()).isEqualTo("테스터");
            assertThat(account.getName()).isEqualTo("홍길동");
            assertThat(account.getPicture()).isEqualTo("https://example.com/picture.jpg");
            assertThat(account.getGroups()).hasSize(1);
            assertThat(account.getModDt()).isEqualTo(now);
            assertThat(account.getRegDt()).isEqualTo(now);
        }
    }
}
