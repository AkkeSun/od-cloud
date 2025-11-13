package com.odcloud.adapter.out.persistence.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.IntegrationTestSupport;
import com.odcloud.domain.model.GroupAccount;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class GroupStorageAdapterTest extends IntegrationTestSupport {

    @Autowired
    GroupStorageAdapter adapter;

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
    @DisplayName("[findGroupAccountsByGroupId] 그룹 ID로 그룹 계정 목록을 조회하는 메소드")
    class Describe_findGroupAccountsByGroupId {

        @Test
        @DisplayName("[success] 그룹 ID로 계정 목록을 정상 조회한다")
        void success() {
            // given
            String groupId = "test-group-id";
            LocalDateTime now = LocalDateTime.now();

            // 그룹 생성
            GroupEntity group = GroupEntity.builder()
                .id(groupId)
                .ownerEmail("owner@example.com")
                .description("테스트 그룹")
                .regDt(now)
                .build();
            entityManager.persist(group);

            // 계정 생성
            AccountEntity account1 = AccountEntity.builder()
                .email("hong@example.com")
                .name("홍길동")
                .nickname("gildong")
                .picture("https://example.com/hong.jpg")
                .isAdminApproved(true)
                .updateDt(now)
                .regDt(now.minusDays(10))
                .build();

            AccountEntity account2 = AccountEntity.builder()
                .email("kim@example.com")
                .name("김철수")
                .nickname("cheolsu")
                .picture("https://example.com/kim.jpg")
                .isAdminApproved(true)
                .updateDt(now)
                .regDt(now.minusDays(5))
                .build();

            entityManager.persist(account1);
            entityManager.persist(account2);

            // 그룹 계정 연결
            GroupAccountEntity groupAccount1 = GroupAccountEntity.builder()
                .groupId(groupId)
                .accountId(account1.getId())
                .status("APPROVED")
                .updateDt(now)
                .regDt(now.minusDays(10))
                .build();

            GroupAccountEntity groupAccount2 = GroupAccountEntity.builder()
                .groupId(groupId)
                .accountId(account2.getId())
                .status("PENDING")
                .updateDt(now)
                .regDt(now.minusDays(5))
                .build();

            entityManager.persist(groupAccount1);
            entityManager.persist(groupAccount2);

            // when
            List<GroupAccount> result = adapter.findGroupAccountsByGroupId(groupId);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getGroupId()).isEqualTo(groupId);
            assertThat(result.get(0).getAccountId()).isEqualTo(account1.getId());
            assertThat(result.get(0).getName()).isEqualTo("홍길동");
            assertThat(result.get(0).getNickName()).isEqualTo("gildong");
            assertThat(result.get(0).getEmail()).isEqualTo("hong@example.com");
            assertThat(result.get(0).getStatus()).isEqualTo("APPROVED");

            assertThat(result.get(1).getGroupId()).isEqualTo(groupId);
            assertThat(result.get(1).getAccountId()).isEqualTo(account2.getId());
            assertThat(result.get(1).getName()).isEqualTo("김철수");
            assertThat(result.get(1).getNickName()).isEqualTo("cheolsu");
            assertThat(result.get(1).getEmail()).isEqualTo("kim@example.com");
            assertThat(result.get(1).getStatus()).isEqualTo("PENDING");
        }

        @Test
        @DisplayName("[success] 그룹에 계정이 없으면 빈 리스트를 반환한다")
        void success_emptyList() {
            // given
            String groupId = "empty-group";

            GroupEntity group = GroupEntity.builder()
                .id(groupId)
                .ownerEmail("owner@example.com")
                .description("빈 그룹")
                .regDt(LocalDateTime.now())
                .build();
            entityManager.persist(group);

            // when
            List<GroupAccount> result = adapter.findGroupAccountsByGroupId(groupId);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("[success] 존재하지 않는 그룹 ID로 조회 시 빈 리스트를 반환한다")
        void success_nonExistentGroup() {
            // given
            String nonExistentGroupId = "non-existent-group";

            // when
            List<GroupAccount> result = adapter.findGroupAccountsByGroupId(nonExistentGroupId);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("[success] 여러 그룹이 있을 때 특정 그룹의 계정만 조회한다")
        void success_multipleGroups() {
            // given
            LocalDateTime now = LocalDateTime.now();
            String targetGroupId = "group-1";
            String otherGroupId = "group-2";

            // 그룹 생성
            GroupEntity group1 = GroupEntity.builder()
                .id(targetGroupId)
                .ownerEmail("owner1@example.com")
                .description("그룹 1")
                .regDt(now)
                .build();

            GroupEntity group2 = GroupEntity.builder()
                .id(otherGroupId)
                .ownerEmail("owner2@example.com")
                .description("그룹 2")
                .regDt(now)
                .build();

            entityManager.persist(group1);
            entityManager.persist(group2);

            // 계정 생성
            AccountEntity account1 = AccountEntity.builder()
                .email("user1@example.com")
                .name("사용자1")
                .nickname("user1")
                .picture("https://example.com/user1.jpg")
                .isAdminApproved(true)
                .updateDt(now)
                .regDt(now)
                .build();

            AccountEntity account2 = AccountEntity.builder()
                .email("user2@example.com")
                .name("사용자2")
                .nickname("user2")
                .picture("https://example.com/user2.jpg")
                .isAdminApproved(true)
                .updateDt(now)
                .regDt(now)
                .build();

            AccountEntity account3 = AccountEntity.builder()
                .email("user3@example.com")
                .name("사용자3")
                .nickname("user3")
                .picture("https://example.com/user3.jpg")
                .isAdminApproved(true)
                .updateDt(now)
                .regDt(now)
                .build();

            entityManager.persist(account1);
            entityManager.persist(account2);
            entityManager.persist(account3);

            // 그룹-계정 연결: group-1에 account1, account2 / group-2에 account3
            GroupAccountEntity ga1 = GroupAccountEntity.builder()
                .groupId(targetGroupId)
                .accountId(account1.getId())
                .status("APPROVED")
                .updateDt(now)
                .regDt(now)
                .build();

            GroupAccountEntity ga2 = GroupAccountEntity.builder()
                .groupId(targetGroupId)
                .accountId(account2.getId())
                .status("PENDING")
                .updateDt(now)
                .regDt(now)
                .build();

            GroupAccountEntity ga3 = GroupAccountEntity.builder()
                .groupId(otherGroupId)
                .accountId(account3.getId())
                .status("APPROVED")
                .updateDt(now)
                .regDt(now)
                .build();

            entityManager.persist(ga1);
            entityManager.persist(ga2);
            entityManager.persist(ga3);

            // when
            List<GroupAccount> result = adapter.findGroupAccountsByGroupId(targetGroupId);

            // then
            assertThat(result).hasSize(2);
            assertThat(result)
                .extracting(GroupAccount::getGroupId)
                .containsOnly(targetGroupId);
            assertThat(result)
                .extracting(GroupAccount::getAccountId)
                .containsExactlyInAnyOrder(account1.getId(), account2.getId());
        }

        @Test
        @DisplayName("[success] 다양한 상태의 계정들을 모두 조회한다")
        void success_multipleStatuses() {
            // given
            String groupId = "test-group";
            LocalDateTime now = LocalDateTime.now();

            GroupEntity group = GroupEntity.builder()
                .id(groupId)
                .ownerEmail("owner@example.com")
                .description("테스트 그룹")
                .regDt(now)
                .build();
            entityManager.persist(group);

            // 계정 생성
            AccountEntity approvedUser = AccountEntity.builder()
                .email("approved@example.com")
                .name("승인된 사용자")
                .nickname("approved")
                .picture("https://example.com/approved.jpg")
                .isAdminApproved(true)
                .updateDt(now)
                .regDt(now)
                .build();

            AccountEntity pendingUser = AccountEntity.builder()
                .email("pending@example.com")
                .name("대기 중인 사용자")
                .nickname("pending")
                .picture("https://example.com/pending.jpg")
                .isAdminApproved(true)
                .updateDt(now)
                .regDt(now)
                .build();

            AccountEntity rejectedUser = AccountEntity.builder()
                .email("rejected@example.com")
                .name("거절된 사용자")
                .nickname("rejected")
                .picture("https://example.com/rejected.jpg")
                .isAdminApproved(true)
                .updateDt(now)
                .regDt(now)
                .build();

            entityManager.persist(approvedUser);
            entityManager.persist(pendingUser);
            entityManager.persist(rejectedUser);

            // 그룹-계정 연결
            entityManager.persist(GroupAccountEntity.builder()
                .groupId(groupId)
                .accountId(approvedUser.getId())
                .status("APPROVED")
                .updateDt(now)
                .regDt(now)
                .build());

            entityManager.persist(GroupAccountEntity.builder()
                .groupId(groupId)
                .accountId(pendingUser.getId())
                .status("PENDING")
                .updateDt(now)
                .regDt(now)
                .build());

            entityManager.persist(GroupAccountEntity.builder()
                .groupId(groupId)
                .accountId(rejectedUser.getId())
                .status("REJECTED")
                .updateDt(now)
                .regDt(now)
                .build());

            // when
            List<GroupAccount> result = adapter.findGroupAccountsByGroupId(groupId);

            // then
            assertThat(result).hasSize(3);
            assertThat(result)
                .extracting(GroupAccount::getStatus)
                .containsExactlyInAnyOrder("APPROVED", "PENDING", "REJECTED");
        }

        @Test
        @DisplayName("[success] 조회 결과가 ID 순으로 정렬된다")
        void success_orderedById() {
            // given
            String groupId = "test-group";
            LocalDateTime now = LocalDateTime.now();

            GroupEntity group = GroupEntity.builder()
                .id(groupId)
                .ownerEmail("owner@example.com")
                .description("테스트 그룹")
                .regDt(now)
                .build();
            entityManager.persist(group);

            // 역순으로 계정 생성
            for (int i = 5; i >= 1; i--) {
                AccountEntity account = AccountEntity.builder()
                    .email("user" + i + "@example.com")
                    .name("사용자" + i)
                    .nickname("user" + i)
                    .picture("https://example.com/user" + i + ".jpg")
                    .isAdminApproved(true)
                    .updateDt(now)
                    .regDt(now)
                    .build();
                entityManager.persist(account);

                GroupAccountEntity groupAccount = GroupAccountEntity.builder()
                    .groupId(groupId)
                    .accountId(account.getId())
                    .status("APPROVED")
                    .updateDt(now)
                    .regDt(now.minusDays(i))
                    .build();
                entityManager.persist(groupAccount);
            }

            // when
            List<GroupAccount> result = adapter.findGroupAccountsByGroupId(groupId);

            // then
            assertThat(result).hasSize(5);
            // ID 오름차순 정렬 확인
            for (int i = 0; i < result.size() - 1; i++) {
                assertThat(result.get(i).getId()).isLessThan(result.get(i + 1).getId());
            }
        }

        @Test
        @DisplayName("[success] 이름이 암호화된 경우 복호화하여 반환한다")
        void success_decryptName() {
            // given
            String groupId = "test-group";
            LocalDateTime now = LocalDateTime.now();

            GroupEntity group = GroupEntity.builder()
                .id(groupId)
                .ownerEmail("owner@example.com")
                .description("테스트 그룹")
                .regDt(now)
                .build();
            entityManager.persist(group);

            // 계정 생성 (이름은 자동으로 암호화됨)
            AccountEntity account = AccountEntity.builder()
                .email("test@example.com")
                .name("홍길동")  // 암호화될 이름
                .nickname("gildong")
                .picture("https://example.com/gildong.jpg")
                .isAdminApproved(true)
                .updateDt(now)
                .regDt(now)
                .build();
            entityManager.persist(account);

            GroupAccountEntity groupAccount = GroupAccountEntity.builder()
                .groupId(groupId)
                .accountId(account.getId())
                .status("APPROVED")
                .updateDt(now)
                .regDt(now)
                .build();
            entityManager.persist(groupAccount);

            // when
            List<GroupAccount> result = adapter.findGroupAccountsByGroupId(groupId);

            // then
            assertThat(result).hasSize(1);
            // 복호화된 이름이 반환되어야 함
            assertThat(result.get(0).getName()).isEqualTo("홍길동");
        }
    }

    @Nested
    @DisplayName("[save(Group)] 그룹을 저장하는 메소드")
    class Describe_saveGroup {

        @Test
        @DisplayName("[success] 신규 그룹을 저장한다")
        void success() {
            // given
            LocalDateTime now = LocalDateTime.now();
            com.odcloud.domain.model.Group group = com.odcloud.domain.model.Group.builder()
                .id("new-group-id")
                .ownerEmail("owner@example.com")
                .description("새로운 그룹")
                .regDt(now)
                .build();

            // when
            adapter.save(group);
            entityManager.flush();
            entityManager.clear();

            // then
            GroupEntity savedEntity = entityManager.find(GroupEntity.class, "new-group-id");
            assertThat(savedEntity).isNotNull();
            assertThat(savedEntity.getId()).isEqualTo("new-group-id");
            assertThat(savedEntity.getOwnerEmail()).isEqualTo("owner@example.com");
            assertThat(savedEntity.getDescription()).isEqualTo("새로운 그룹");
        }

        @Test
        @DisplayName("[success] 기존 그룹을 업데이트한다")
        void success_update() {
            // given
            LocalDateTime now = LocalDateTime.now();
            GroupEntity existingGroup = GroupEntity.builder()
                .id("existing-group")
                .ownerEmail("old@example.com")
                .description("기존 설명")
                .regDt(now)
                .build();
            entityManager.persist(existingGroup);
            entityManager.flush();
            entityManager.clear();

            com.odcloud.domain.model.Group updatedGroup = com.odcloud.domain.model.Group.builder()
                .id("existing-group")
                .ownerEmail("new@example.com")
                .description("새로운 설명")
                .regDt(now)
                .build();

            // when
            adapter.save(updatedGroup);
            entityManager.flush();
            entityManager.clear();

            // then
            GroupEntity savedEntity = entityManager.find(GroupEntity.class, "existing-group");
            assertThat(savedEntity).isNotNull();
            assertThat(savedEntity.getOwnerEmail()).isEqualTo("new@example.com");
            assertThat(savedEntity.getDescription()).isEqualTo("새로운 설명");
        }

        @Test
        @DisplayName("[success] description이 null인 그룹을 저장한다")
        void success_nullDescription() {
            // given
            LocalDateTime now = LocalDateTime.now();
            com.odcloud.domain.model.Group group = com.odcloud.domain.model.Group.builder()
                .id("null-desc-group")
                .ownerEmail("owner@example.com")
                .description(null)
                .regDt(now)
                .build();

            // when
            adapter.save(group);
            entityManager.flush();
            entityManager.clear();

            // then
            GroupEntity savedEntity = entityManager.find(GroupEntity.class, "null-desc-group");
            assertThat(savedEntity).isNotNull();
            assertThat(savedEntity.getDescription()).isNull();
        }
    }

    @Nested
    @DisplayName("[save(GroupAccount)] 그룹 계정을 저장하는 메소드")
    class Describe_saveGroupAccount {

        @Test
        @DisplayName("[success] 신규 그룹 계정을 저장한다")
        void success() {
            // given
            LocalDateTime now = LocalDateTime.now();

            // 그룹 생성
            GroupEntity group = GroupEntity.builder()
                .id("test-group")
                .ownerEmail("owner@example.com")
                .description("테스트 그룹")
                .regDt(now)
                .build();
            entityManager.persist(group);

            // 계정 생성
            AccountEntity account = AccountEntity.builder()
                .email("test@example.com")
                .name("테스트 사용자")
                .nickname("tester")
                .picture("https://example.com/pic.jpg")
                .isAdminApproved(true)
                .updateDt(now)
                .regDt(now)
                .build();
            entityManager.persist(account);
            entityManager.flush();

            com.odcloud.domain.model.GroupAccount groupAccount = com.odcloud.domain.model.GroupAccount.builder()
                .groupId("test-group")
                .accountId(account.getId())
                .status("PENDING")
                .updateDt(now)
                .regDt(now)
                .build();

            // when
            adapter.save(groupAccount);
            entityManager.flush();
            entityManager.clear();

            // then
            List<GroupAccountEntity> savedEntities = entityManager
                .createQuery("SELECT ga FROM GroupAccountEntity ga WHERE ga.groupId = :groupId",
                    GroupAccountEntity.class)
                .setParameter("groupId", "test-group")
                .getResultList();

            assertThat(savedEntities).hasSize(1);
            assertThat(savedEntities.get(0).getGroupId()).isEqualTo("test-group");
            assertThat(savedEntities.get(0).getAccountId()).isEqualTo(account.getId());
            assertThat(savedEntities.get(0).getStatus()).isEqualTo("PENDING");
        }

        @Test
        @DisplayName("[success] 기존 그룹 계정의 상태를 업데이트한다")
        void success_updateStatus() {
            // given
            LocalDateTime now = LocalDateTime.now();

            GroupEntity group = GroupEntity.builder()
                .id("test-group")
                .ownerEmail("owner@example.com")
                .description("테스트 그룹")
                .regDt(now)
                .build();
            entityManager.persist(group);

            AccountEntity account = AccountEntity.builder()
                .email("test@example.com")
                .name("테스트 사용자")
                .nickname("tester")
                .picture("https://example.com/pic.jpg")
                .isAdminApproved(true)
                .updateDt(now)
                .regDt(now)
                .build();
            entityManager.persist(account);

            GroupAccountEntity existingGroupAccount = GroupAccountEntity.builder()
                .groupId("test-group")
                .accountId(account.getId())
                .status("PENDING")
                .updateDt(now)
                .regDt(now)
                .build();
            entityManager.persist(existingGroupAccount);
            entityManager.flush();
            entityManager.clear();

            com.odcloud.domain.model.GroupAccount updatedGroupAccount = com.odcloud.domain.model.GroupAccount.builder()
                .id(existingGroupAccount.getId())
                .groupId("test-group")
                .accountId(account.getId())
                .status("APPROVED")
                .updateDt(now.plusHours(1))
                .regDt(now)
                .build();

            // when
            adapter.save(updatedGroupAccount);
            entityManager.flush();
            entityManager.clear();

            // then
            GroupAccountEntity savedEntity = entityManager.find(GroupAccountEntity.class,
                existingGroupAccount.getId());
            assertThat(savedEntity).isNotNull();
            assertThat(savedEntity.getStatus()).isEqualTo("APPROVED");
        }
    }

    @Nested
    @DisplayName("[existsById] ID로 그룹 존재 여부를 확인하는 메소드")
    class Describe_existsById {

        @Test
        @DisplayName("[success] 존재하는 그룹 ID인 경우 true를 반환한다")
        void success_exists() {
            // given
            LocalDateTime now = LocalDateTime.now();
            GroupEntity group = GroupEntity.builder()
                .id("existing-group")
                .ownerEmail("owner@example.com")
                .description("존재하는 그룹")
                .regDt(now)
                .build();
            entityManager.persist(group);
            entityManager.flush();
            entityManager.clear();

            // when
            boolean result = adapter.existsById("existing-group");

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("[success] 존재하지 않는 그룹 ID인 경우 false를 반환한다")
        void success_notExists() {
            // when
            boolean result = adapter.existsById("non-existent-group");

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("[success] 여러 그룹 중 특정 ID만 확인한다")
        void success_multipleGroups() {
            // given
            LocalDateTime now = LocalDateTime.now();
            entityManager.persist(GroupEntity.builder()
                .id("group-1")
                .ownerEmail("owner1@example.com")
                .description("그룹 1")
                .regDt(now)
                .build());

            entityManager.persist(GroupEntity.builder()
                .id("group-2")
                .ownerEmail("owner2@example.com")
                .description("그룹 2")
                .regDt(now)
                .build());

            entityManager.flush();
            entityManager.clear();

            // when & then
            assertThat(adapter.existsById("group-1")).isTrue();
            assertThat(adapter.existsById("group-2")).isTrue();
            assertThat(adapter.existsById("group-3")).isFalse();
        }
    }

    @Nested
    @DisplayName("[findAll] 모든 그룹을 조회하는 메소드")
    class Describe_findAll {

        @Test
        @DisplayName("[success] 모든 그룹을 조회한다")
        void success() {
            // given
            LocalDateTime now = LocalDateTime.now();
            entityManager.persist(GroupEntity.builder()
                .id("group-1")
                .ownerEmail("owner1@example.com")
                .description("그룹 1")
                .regDt(now.minusDays(2))
                .build());

            entityManager.persist(GroupEntity.builder()
                .id("group-2")
                .ownerEmail("owner2@example.com")
                .description("그룹 2")
                .regDt(now.minusDays(1))
                .build());

            entityManager.persist(GroupEntity.builder()
                .id("group-3")
                .ownerEmail("owner3@example.com")
                .description("그룹 3")
                .regDt(now)
                .build());

            entityManager.flush();
            entityManager.clear();

            // when
            List<com.odcloud.domain.model.Group> result = adapter.findAll();

            // then
            assertThat(result).hasSize(3);
            assertThat(result)
                .extracting(com.odcloud.domain.model.Group::getId)
                .containsExactlyInAnyOrder("group-1", "group-2", "group-3");
        }

        @Test
        @DisplayName("[success] 그룹이 없는 경우 빈 리스트를 반환한다")
        void success_empty() {
            // when
            List<com.odcloud.domain.model.Group> result = adapter.findAll();

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("[success] 그룹 기본 정보만 조회한다 (멤버 정보 제외)")
        void success_basicInfoOnly() {
            // given
            LocalDateTime now = LocalDateTime.now();
            GroupEntity group = GroupEntity.builder()
                .id("test-group")
                .ownerEmail("owner@example.com")
                .description("테스트 그룹")
                .regDt(now)
                .build();
            entityManager.persist(group);

            // 계정과 그룹 멤버도 추가
            AccountEntity account = AccountEntity.builder()
                .email("member@example.com")
                .name("멤버")
                .nickname("member")
                .picture("https://example.com/pic.jpg")
                .isAdminApproved(true)
                .updateDt(now)
                .regDt(now)
                .build();
            entityManager.persist(account);

            GroupAccountEntity groupAccount = GroupAccountEntity.builder()
                .groupId("test-group")
                .accountId(account.getId())
                .status("APPROVED")
                .updateDt(now)
                .regDt(now)
                .build();
            entityManager.persist(groupAccount);

            entityManager.flush();
            entityManager.clear();

            // when
            List<com.odcloud.domain.model.Group> result = adapter.findAll();

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo("test-group");
            assertThat(result.get(0).getOwnerEmail()).isEqualTo("owner@example.com");
            assertThat(result.get(0).getDescription()).isEqualTo("테스트 그룹");
            // findAll은 멤버 정보를 포함하지 않음
            assertThat(result.get(0).getGroupMembers()).isNull();
        }
    }

    @Nested
    @DisplayName("[findById] ID로 그룹을 조회하는 메소드")
    class Describe_findById {

        @Test
        @DisplayName("[success] 그룹과 멤버 정보를 함께 조회한다")
        void success() {
            // given
            LocalDateTime now = LocalDateTime.now();
            String groupId = "test-group";

            GroupEntity group = GroupEntity.builder()
                .id(groupId)
                .ownerEmail("owner@example.com")
                .description("테스트 그룹")
                .regDt(now)
                .build();
            entityManager.persist(group);

            AccountEntity account1 = AccountEntity.builder()
                .email("member1@example.com")
                .name("멤버1")
                .nickname("member1")
                .picture("https://example.com/pic1.jpg")
                .isAdminApproved(true)
                .updateDt(now)
                .regDt(now)
                .build();
            entityManager.persist(account1);

            AccountEntity account2 = AccountEntity.builder()
                .email("member2@example.com")
                .name("멤버2")
                .nickname("member2")
                .picture("https://example.com/pic2.jpg")
                .isAdminApproved(true)
                .updateDt(now)
                .regDt(now)
                .build();
            entityManager.persist(account2);

            entityManager.persist(GroupAccountEntity.builder()
                .groupId(groupId)
                .accountId(account1.getId())
                .status("APPROVED")
                .updateDt(now)
                .regDt(now)
                .build());

            entityManager.persist(GroupAccountEntity.builder()
                .groupId(groupId)
                .accountId(account2.getId())
                .status("PENDING")
                .updateDt(now)
                .regDt(now)
                .build());

            entityManager.flush();
            entityManager.clear();

            // when
            com.odcloud.domain.model.Group result = adapter.findById(groupId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(groupId);
            assertThat(result.getOwnerEmail()).isEqualTo("owner@example.com");
            assertThat(result.getDescription()).isEqualTo("테스트 그룹");
            assertThat(result.getGroupMembers()).hasSize(2);
            assertThat(result.getGroupMembers())
                .extracting(com.odcloud.domain.model.GroupAccount::getName)
                .containsExactlyInAnyOrder("멤버1", "멤버2");
        }

        @Test
        @DisplayName("[success] 멤버가 없는 그룹도 조회한다")
        void success_noMembers() {
            // given
            LocalDateTime now = LocalDateTime.now();
            String groupId = "empty-group";

            GroupEntity group = GroupEntity.builder()
                .id(groupId)
                .ownerEmail("owner@example.com")
                .description("빈 그룹")
                .regDt(now)
                .build();
            entityManager.persist(group);
            entityManager.flush();
            entityManager.clear();

            // when
            com.odcloud.domain.model.Group result = adapter.findById(groupId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(groupId);
            assertThat(result.getGroupMembers()).isEmpty();
        }

        @Test
        @DisplayName("[error] 존재하지 않는 그룹 ID로 조회 시 예외를 발생시킨다")
        void error_notFound() {
            // when & then
            org.junit.jupiter.api.Assertions.assertThrows(
                com.odcloud.infrastructure.exception.CustomBusinessException.class,
                () -> adapter.findById("non-existent-group")
            );
        }

        @Test
        @DisplayName("[success] 멤버 이름이 복호화되어 반환된다")
        void success_decryptedNames() {
            // given
            LocalDateTime now = LocalDateTime.now();
            String groupId = "test-group";

            GroupEntity group = GroupEntity.builder()
                .id(groupId)
                .ownerEmail("owner@example.com")
                .description("테스트 그룹")
                .regDt(now)
                .build();
            entityManager.persist(group);

            AccountEntity account = AccountEntity.builder()
                .email("test@example.com")
                .name("홍길동")  // 암호화될 이름
                .nickname("gildong")
                .picture("https://example.com/pic.jpg")
                .isAdminApproved(true)
                .updateDt(now)
                .regDt(now)
                .build();
            entityManager.persist(account);

            entityManager.persist(GroupAccountEntity.builder()
                .groupId(groupId)
                .accountId(account.getId())
                .status("APPROVED")
                .updateDt(now)
                .regDt(now)
                .build());

            entityManager.flush();
            entityManager.clear();

            // when
            com.odcloud.domain.model.Group result = adapter.findById(groupId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getGroupMembers()).hasSize(1);
            assertThat(result.getGroupMembers().get(0).getName()).isEqualTo("홍길동");
        }
    }

    @Nested
    @DisplayName("[findGroupAccountByGroupIdAndAccountId] 그룹 ID와 계정 ID로 그룹 계정을 조회하는 메소드")
    class Describe_findGroupAccountByGroupIdAndAccountId {

        @Test
        @DisplayName("[success] 그룹 ID와 계정 ID로 그룹 계정을 조회한다")
        void success() {
            // given
            LocalDateTime now = LocalDateTime.now();
            String groupId = "test-group";

            GroupEntity group = GroupEntity.builder()
                .id(groupId)
                .ownerEmail("owner@example.com")
                .description("테스트 그룹")
                .regDt(now)
                .build();
            entityManager.persist(group);

            AccountEntity account = AccountEntity.builder()
                .email("test@example.com")
                .name("테스트 사용자")
                .nickname("tester")
                .picture("https://example.com/pic.jpg")
                .isAdminApproved(true)
                .updateDt(now)
                .regDt(now)
                .build();
            entityManager.persist(account);

            GroupAccountEntity groupAccount = GroupAccountEntity.builder()
                .groupId(groupId)
                .accountId(account.getId())
                .status("APPROVED")
                .updateDt(now)
                .regDt(now)
                .build();
            entityManager.persist(groupAccount);

            entityManager.flush();
            entityManager.clear();

            com.odcloud.application.port.in.command.UpdateGroupAccountStatusCommand command =
                com.odcloud.application.port.in.command.UpdateGroupAccountStatusCommand.builder()
                    .groupId(groupId)
                    .accountId(account.getId())
                    .status("REJECTED")
                    .build();

            // when
            com.odcloud.domain.model.GroupAccount result =
                adapter.findGroupAccountByGroupIdAndAccountId(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getGroupId()).isEqualTo(groupId);
            assertThat(result.getAccountId()).isEqualTo(account.getId());
            assertThat(result.getName()).isEqualTo("테스트 사용자");
            assertThat(result.getNickName()).isEqualTo("tester");
            assertThat(result.getEmail()).isEqualTo("test@example.com");
            assertThat(result.getStatus()).isEqualTo("APPROVED");
        }

        @Test
        @DisplayName("[success] 그룹 설명(description)도 함께 조회한다")
        void success_withGroupDescription() {
            // given
            LocalDateTime now = LocalDateTime.now();
            String groupId = "test-group";

            GroupEntity group = GroupEntity.builder()
                .id(groupId)
                .ownerEmail("owner@example.com")
                .description("그룹 설명")
                .regDt(now)
                .build();
            entityManager.persist(group);

            AccountEntity account = AccountEntity.builder()
                .email("test@example.com")
                .name("테스트 사용자")
                .nickname("tester")
                .picture("https://example.com/pic.jpg")
                .isAdminApproved(true)
                .updateDt(now)
                .regDt(now)
                .build();
            entityManager.persist(account);

            GroupAccountEntity groupAccount = GroupAccountEntity.builder()
                .groupId(groupId)
                .accountId(account.getId())
                .status("PENDING")
                .updateDt(now)
                .regDt(now)
                .build();
            entityManager.persist(groupAccount);

            entityManager.flush();
            entityManager.clear();

            com.odcloud.application.port.in.command.UpdateGroupAccountStatusCommand command =
                com.odcloud.application.port.in.command.UpdateGroupAccountStatusCommand.builder()
                    .groupId(groupId)
                    .accountId(account.getId())
                    .status("APPROVED")
                    .build();

            // when
            com.odcloud.domain.model.GroupAccount result =
                adapter.findGroupAccountByGroupIdAndAccountId(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getGroupName()).isEqualTo("그룹 설명");
        }

        @Test
        @DisplayName("[error] 존재하지 않는 그룹 ID인 경우 예외를 발생시킨다")
        void error_groupNotFound() {
            // given
            LocalDateTime now = LocalDateTime.now();

            AccountEntity account = AccountEntity.builder()
                .email("test@example.com")
                .name("테스트 사용자")
                .nickname("tester")
                .picture("https://example.com/pic.jpg")
                .isAdminApproved(true)
                .updateDt(now)
                .regDt(now)
                .build();
            entityManager.persist(account);
            entityManager.flush();

            com.odcloud.application.port.in.command.UpdateGroupAccountStatusCommand command =
                com.odcloud.application.port.in.command.UpdateGroupAccountStatusCommand.builder()
                    .groupId("non-existent-group")
                    .accountId(account.getId())
                    .status("APPROVED")
                    .build();

            // when & then
            org.junit.jupiter.api.Assertions.assertThrows(
                com.odcloud.infrastructure.exception.CustomBusinessException.class,
                () -> adapter.findGroupAccountByGroupIdAndAccountId(command)
            );
        }

        @Test
        @DisplayName("[error] 존재하지 않는 계정 ID인 경우 예외를 발생시킨다")
        void error_accountNotFound() {
            // given
            LocalDateTime now = LocalDateTime.now();
            String groupId = "test-group";

            GroupEntity group = GroupEntity.builder()
                .id(groupId)
                .ownerEmail("owner@example.com")
                .description("테스트 그룹")
                .regDt(now)
                .build();
            entityManager.persist(group);
            entityManager.flush();

            com.odcloud.application.port.in.command.UpdateGroupAccountStatusCommand command =
                com.odcloud.application.port.in.command.UpdateGroupAccountStatusCommand.builder()
                    .groupId(groupId)
                    .accountId(999999L)
                    .status("APPROVED")
                    .build();

            // when & then
            org.junit.jupiter.api.Assertions.assertThrows(
                com.odcloud.infrastructure.exception.CustomBusinessException.class,
                () -> adapter.findGroupAccountByGroupIdAndAccountId(command)
            );
        }

        @Test
        @DisplayName("[error] 그룹에 속하지 않은 계정인 경우 예외를 발생시킨다")
        void error_notMember() {
            // given
            LocalDateTime now = LocalDateTime.now();
            String groupId = "test-group";

            GroupEntity group = GroupEntity.builder()
                .id(groupId)
                .ownerEmail("owner@example.com")
                .description("테스트 그룹")
                .regDt(now)
                .build();
            entityManager.persist(group);

            AccountEntity account = AccountEntity.builder()
                .email("test@example.com")
                .name("테스트 사용자")
                .nickname("tester")
                .picture("https://example.com/pic.jpg")
                .isAdminApproved(true)
                .updateDt(now)
                .regDt(now)
                .build();
            entityManager.persist(account);

            // GroupAccountEntity는 생성하지 않음

            entityManager.flush();
            entityManager.clear();

            com.odcloud.application.port.in.command.UpdateGroupAccountStatusCommand command =
                com.odcloud.application.port.in.command.UpdateGroupAccountStatusCommand.builder()
                    .groupId(groupId)
                    .accountId(account.getId())
                    .status("APPROVED")
                    .build();

            // when & then
            org.junit.jupiter.api.Assertions.assertThrows(
                com.odcloud.infrastructure.exception.CustomBusinessException.class,
                () -> adapter.findGroupAccountByGroupIdAndAccountId(command)
            );
        }

        @Test
        @DisplayName("[success] 이름이 복호화되어 반환된다")
        void success_decryptedName() {
            // given
            LocalDateTime now = LocalDateTime.now();
            String groupId = "test-group";

            GroupEntity group = GroupEntity.builder()
                .id(groupId)
                .ownerEmail("owner@example.com")
                .description("테스트 그룹")
                .regDt(now)
                .build();
            entityManager.persist(group);

            AccountEntity account = AccountEntity.builder()
                .email("test@example.com")
                .name("홍길동")  // 암호화될 이름
                .nickname("gildong")
                .picture("https://example.com/pic.jpg")
                .isAdminApproved(true)
                .updateDt(now)
                .regDt(now)
                .build();
            entityManager.persist(account);

            GroupAccountEntity groupAccount = GroupAccountEntity.builder()
                .groupId(groupId)
                .accountId(account.getId())
                .status("APPROVED")
                .updateDt(now)
                .regDt(now)
                .build();
            entityManager.persist(groupAccount);

            entityManager.flush();
            entityManager.clear();

            com.odcloud.application.port.in.command.UpdateGroupAccountStatusCommand command =
                com.odcloud.application.port.in.command.UpdateGroupAccountStatusCommand.builder()
                    .groupId(groupId)
                    .accountId(account.getId())
                    .status("APPROVED")
                    .build();

            // when
            com.odcloud.domain.model.GroupAccount result =
                adapter.findGroupAccountByGroupIdAndAccountId(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("홍길동");
        }
    }
}
