package com.odcloud.adapter.out.persistence.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.IntegrationTestSupport;
import com.odcloud.domain.model.Account;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class AccountStorageAdapterTest extends IntegrationTestSupport {

    @Autowired
    AccountStorageAdapter adapter;

    @Autowired
    EntityManager entityManager;

    @AfterEach
    void tearDown() {
        entityManager.createQuery("DELETE FROM GroupAccountEntity").executeUpdate();
        entityManager.createQuery("DELETE FROM AccountEntity").executeUpdate();
        entityManager.createQuery("DELETE FROM GroupEntity").executeUpdate();
        entityManager.flush();
        entityManager.clear();
    }

    @Nested
    @DisplayName("[save] 계정을 저장하는 메소드")
    class Describe_save {

        @Test
        @DisplayName("[success] 신규 계정을 저장한다")
        void success() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Account account = Account.builder()
                .email("test@example.com")
                .nickname("tester")
                .name("홍길동")
                .picture("https://example.com/pic.jpg")
                .regDt(now)
                .build();

            // when
            Account result = adapter.save(account);
            entityManager.flush();
            entityManager.clear();

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isNotNull();
            assertThat(result.getEmail()).isEqualTo("test@example.com");
            assertThat(result.getNickname()).isEqualTo("tester");
            assertThat(result.getName()).isEqualTo("홍길동");
            assertThat(result.getPicture()).isEqualTo("https://example.com/pic.jpg");

            AccountEntity savedEntity = entityManager.find(AccountEntity.class, result.getId());
            assertThat(savedEntity).isNotNull();
            assertThat(savedEntity.getEmail()).isEqualTo("test@example.com");
        }

        @Test
        @DisplayName("[success] 기존 계정을 업데이트한다")
        void success_update() {
            // given
            LocalDateTime now = LocalDateTime.now();
            AccountEntity existingAccount = AccountEntity.builder()
                .email("test@example.com")
                .nickname("oldnick")
                .name("김철수")
                .picture("https://example.com/old.jpg")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(existingAccount);
            entityManager.flush();
            entityManager.clear();

            Account updatedAccount = Account.builder()
                .id(existingAccount.getId())
                .email("test@example.com")
                .nickname("newnick")
                .name("김영희")
                .picture("https://example.com/new.jpg")
                .regDt(now)
                .modDt(now.plusHours(1))
                .build();

            // when
            Account result = adapter.save(updatedAccount);
            entityManager.flush();
            entityManager.clear();

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(existingAccount.getId());
            assertThat(result.getNickname()).isEqualTo("newnick");
            assertThat(result.getName()).isEqualTo("김영희");
            assertThat(result.getPicture()).isEqualTo("https://example.com/new.jpg");

            AccountEntity savedEntity = entityManager.find(AccountEntity.class,
                existingAccount.getId());
            assertThat(savedEntity).isNotNull();
            assertThat(savedEntity.getNickname()).isEqualTo("newnick");
        }

        @Test
        @DisplayName("[success] 이름이 암호화되어 저장된다")
        void success_encryptedName() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Account account = Account.builder()
                .email("test@example.com")
                .nickname("tester")
                .name("홍길동")
                .picture("https://example.com/pic.jpg")
                .regDt(now)
                .build();

            // when
            Account result = adapter.save(account);
            entityManager.flush();
            entityManager.clear();

            // then
            AccountEntity savedEntity = entityManager.find(AccountEntity.class, result.getId());
            assertThat(savedEntity).isNotNull();
            // 저장된 이름은 암호화되어 있어야 함 (원본과 다름)
            assertThat(savedEntity.getName()).isNotEqualTo("홍길동");
            // 하지만 반환된 도메인 객체의 이름은 복호화되어 있어야 함
            assertThat(result.getName()).isEqualTo("홍길동");
        }

        @Test
        @DisplayName("[success] null 값을 포함한 계정을 저장한다")
        void success_withNullValues() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Account account = Account.builder()
                .email("test@example.com")
                .nickname("tester")
                .name("테스트")
                .picture(null)
                .regDt(now)
                .modDt(null)
                .build();

            // when
            Account result = adapter.save(account);
            entityManager.flush();
            entityManager.clear();

            // then
            assertThat(result).isNotNull();
            assertThat(result.getPicture()).isNull();
            assertThat(result.getModDt()).isNull();
        }
    }

    @Nested
    @DisplayName("[existsByEmail] 이메일로 계정 존재 여부를 확인하는 메소드")
    class Describe_existsByEmail {

        @Test
        @DisplayName("[success] 존재하는 이메일인 경우 true를 반환한다")
        void success_exists() {
            // given
            LocalDateTime now = LocalDateTime.now();
            AccountEntity account = AccountEntity.builder()
                .email("existing@example.com")
                .nickname("existing")
                .name("기존사용자")
                .picture("https://example.com/pic.jpg")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(account);
            entityManager.flush();
            entityManager.clear();

            // when
            boolean result = adapter.existsByEmail("existing@example.com");

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("[success] 존재하지 않는 이메일인 경우 false를 반환한다")
        void success_notExists() {
            // when
            boolean result = adapter.existsByEmail("notexist@example.com");

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("[success] 여러 계정 중 특정 이메일만 확인한다")
        void success_multipleAccounts() {
            // given
            LocalDateTime now = LocalDateTime.now();
            entityManager.persist(AccountEntity.builder()
                .email("user1@example.com")
                .nickname("user1")
                .name("사용자1")
                .picture("https://example.com/pic1.jpg")
                .regDt(now)
                .modDt(now)
                .build());

            entityManager.persist(AccountEntity.builder()
                .email("user2@example.com")
                .nickname("user2")
                .name("사용자2")
                .picture("https://example.com/pic2.jpg")
                .regDt(now)
                .modDt(now)
                .build());

            entityManager.flush();
            entityManager.clear();

            // when & then
            assertThat(adapter.existsByEmail("user1@example.com")).isTrue();
            assertThat(adapter.existsByEmail("user2@example.com")).isTrue();
            assertThat(adapter.existsByEmail("user3@example.com")).isFalse();
        }

        @Test
        @DisplayName("[success] 대소문자를 구분하여 확인한다")
        void success_caseSensitive() {
            // given
            LocalDateTime now = LocalDateTime.now();
            AccountEntity account = AccountEntity.builder()
                .email("Test@Example.com")
                .nickname("test")
                .name("테스트")
                .picture("https://example.com/pic.jpg")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(account);
            entityManager.flush();
            entityManager.clear();

            // when & then
            assertThat(adapter.existsByEmail("Test@Example.com")).isTrue();
            assertThat(adapter.existsByEmail("test@example.com")).isFalse();
        }
    }

    @Nested
    @DisplayName("[findByEmail] 이메일로 계정을 조회하는 메소드")
    class Describe_findByEmail {

        @Test
        @DisplayName("[success] 이메일로 계정을 조회한다")
        void success() {
            // given
            LocalDateTime now = LocalDateTime.now();
            AccountEntity account = AccountEntity.builder()
                .email("test@example.com")
                .nickname("tester")
                .name("홍길동")
                .picture("https://example.com/pic.jpg")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(account);
            entityManager.flush();
            entityManager.clear();

            // when
            Account result = adapter.findByEmail("test@example.com");

            // then
            assertThat(result).isNotNull();
            assertThat(result.getEmail()).isEqualTo("test@example.com");
            assertThat(result.getNickname()).isEqualTo("tester");
            assertThat(result.getName()).isEqualTo("홍길동");
            assertThat(result.getPicture()).isEqualTo("https://example.com/pic.jpg");
        }

        @Test
        @DisplayName("[success] 이름이 복호화되어 반환된다")
        void success_decryptedName() {
            // given
            LocalDateTime now = LocalDateTime.now();
            AccountEntity account = AccountEntity.builder()
                .email("test@example.com")
                .nickname("tester")
                .name("홍길동")
                .picture("https://example.com/pic.jpg")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(account);
            entityManager.flush();
            entityManager.clear();

            // when
            Account result = adapter.findByEmail("test@example.com");

            // then
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("홍길동");
        }

        @Test
        @DisplayName("[success] 그룹 정보를 함께 조회한다")
        void success_withGroups() {
            // given
            LocalDateTime now = LocalDateTime.now();

            // 그룹 생성
            GroupEntity group1 = GroupEntity.builder()
                .ownerEmail("owner@example.com")
                .name("그룹 1")
                .regDt(now)
                .build();

            GroupEntity group2 = GroupEntity.builder()
                .ownerEmail("owner@example.com")
                .name("그룹 2")
                .regDt(now)
                .build();

            entityManager.persist(group1);
            entityManager.persist(group2);
            entityManager.flush();

            // 계정 생성
            AccountEntity account = AccountEntity.builder()
                .email("test@example.com")
                .nickname("tester")
                .name("홍길동")
                .picture("https://example.com/pic.jpg")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(account);
            entityManager.flush();

            // 그룹-계정 연결 (ACTIVE 상태만 조회됨)
            GroupAccountEntity ga1 = GroupAccountEntity.builder()
                .groupId(group1.getId())
                .accountId(account.getId())
                .status("ACTIVE")
                .regDt(now)
                .modDt(now)
                .build();

            GroupAccountEntity ga2 = GroupAccountEntity.builder()
                .groupId(group2.getId())
                .accountId(account.getId())
                .status("ACTIVE")
                .regDt(now)
                .modDt(now)
                .build();

            entityManager.persist(ga1);
            entityManager.persist(ga2);

            entityManager.flush();
            entityManager.clear();

            // when
            Account result = adapter.findByEmail("test@example.com");

            // then
            assertThat(result).isNotNull();
            assertThat(result.getGroups()).hasSize(2);
            assertThat(result.getGroups())
                .extracting(com.odcloud.domain.model.Group::getId)
                .containsExactlyInAnyOrder(group1.getId(), group2.getId());
        }

        @Test
        @DisplayName("[success] ACTIVE 상태의 그룹만 조회한다")
        void success_onlyActiveGroups() {
            // given
            LocalDateTime now = LocalDateTime.now();

            // 그룹 생성
            GroupEntity activeGroup = GroupEntity.builder()
                .ownerEmail("owner@example.com")
                .name("활성 그룹")
                .regDt(now)
                .build();

            GroupEntity pendingGroup = GroupEntity.builder()
                .ownerEmail("owner@example.com")
                .name("대기 그룹")
                .regDt(now)
                .build();

            entityManager.persist(activeGroup);
            entityManager.persist(pendingGroup);
            entityManager.flush();

            // 계정 생성
            AccountEntity account = AccountEntity.builder()
                .email("test@example.com")
                .nickname("tester")
                .name("홍길동")
                .picture("https://example.com/pic.jpg")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(account);
            entityManager.flush();

            // 그룹-계정 연결
            entityManager.persist(GroupAccountEntity.builder()
                .groupId(activeGroup.getId())
                .accountId(account.getId())
                .status("ACTIVE")
                .regDt(now)
                .modDt(now)
                .build());

            entityManager.persist(GroupAccountEntity.builder()
                .groupId(pendingGroup.getId())
                .accountId(account.getId())
                .status("PENDING")
                .regDt(now)
                .modDt(now)
                .build());

            entityManager.flush();
            entityManager.clear();

            // when
            Account result = adapter.findByEmail("test@example.com");

            // then
            assertThat(result).isNotNull();
            assertThat(result.getGroups()).hasSize(1);
            assertThat(result.getGroups().get(0).getId()).isEqualTo(activeGroup.getId());
        }

        @Test
        @DisplayName("[success] 그룹이 없는 계정도 조회한다")
        void success_noGroups() {
            // given
            LocalDateTime now = LocalDateTime.now();
            AccountEntity account = AccountEntity.builder()
                .email("test@example.com")
                .nickname("tester")
                .name("홍길동")
                .picture("https://example.com/pic.jpg")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(account);
            entityManager.flush();
            entityManager.clear();

            // when
            Account result = adapter.findByEmail("test@example.com");

            // then
            assertThat(result).isNotNull();
            assertThat(result.getGroups()).isEmpty();
        }

        @Test
        @DisplayName("[error] 존재하지 않는 이메일로 조회 시 예외를 발생시킨다")
        void error_notFound() {
            // when & then
            org.junit.jupiter.api.Assertions.assertThrows(
                com.odcloud.infrastructure.exception.CustomBusinessException.class,
                () -> adapter.findByEmail("notexist@example.com")
            );
        }

        @Test
        @DisplayName("[success] 여러 계정 중 특정 이메일만 조회한다")
        void success_specificAccount() {
            // given
            LocalDateTime now = LocalDateTime.now();
            entityManager.persist(AccountEntity.builder()
                .email("user1@example.com")
                .nickname("user1")
                .name("사용자1")
                .picture("https://example.com/pic1.jpg")
                .regDt(now)
                .modDt(now)
                .build());

            entityManager.persist(AccountEntity.builder()
                .email("user2@example.com")
                .nickname("user2")
                .name("사용자2")
                .picture("https://example.com/pic2.jpg")
                .regDt(now)
                .modDt(now)
                .build());

            entityManager.flush();
            entityManager.clear();

            // when
            Account result = adapter.findByEmail("user1@example.com");

            // then
            assertThat(result).isNotNull();
            assertThat(result.getEmail()).isEqualTo("user1@example.com");
            assertThat(result.getNickname()).isEqualTo("user1");
            assertThat(result.getName()).isEqualTo("사용자1");
        }
    }
}
