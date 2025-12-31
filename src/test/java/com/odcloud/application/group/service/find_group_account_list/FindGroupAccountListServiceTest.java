package com.odcloud.application.group.service.find_group_account_list;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.domain.model.GroupAccount;
import com.odcloud.fakeClass.FakeGroupStoragePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FindGroupAccountListServiceTest {

    private FakeGroupStoragePort fakeGroupStoragePort;
    private FindGroupAccountListService findGroupAccountListService;

    @BeforeEach
    void setUp() {
        fakeGroupStoragePort = new FakeGroupStoragePort();
        findGroupAccountListService = new FindGroupAccountListService(fakeGroupStoragePort);
    }

    @Nested
    @DisplayName("[findGroupAccountList] 그룹 계정 목록 조회")
    class Describe_findGroupAccountList {

        @Test
        @DisplayName("[success] 정상적으로 그룹 계정 목록을 조회한다")
        void success() {
            // given
            String groupId = "test-group";

            GroupAccount account1 = GroupAccount.builder()
                .id(1L)
                .groupId(groupId)
                .accountId(1L)
                .email("user1@example.com")
                .name("사용자1")
                .nickName("닉네임1")
                .status("ACTIVE")
                .build();

            GroupAccount account2 = GroupAccount.builder()
                .id(2L)
                .groupId(groupId)
                .accountId(2L)
                .email("user2@example.com")
                .name("사용자2")
                .nickName("닉네임2")
                .status("PENDING")
                .build();

            fakeGroupStoragePort.groupAccountDatabase.add(account1);
            fakeGroupStoragePort.groupAccountDatabase.add(account2);

            // when
            FindGroupAccountListServiceResponse response = findGroupAccountListService
                .findGroupAccountList(groupId);

            // then
            assertThat(response).isNotNull();
            assertThat(response.groupAccounts()).hasSize(2);
            assertThat(response.groupAccounts().get(0).getEmail()).isEqualTo("user1@example.com");
            assertThat(response.groupAccounts().get(1).getEmail()).isEqualTo("user2@example.com");
        }

        @Test
        @DisplayName("[success] 그룹에 계정이 없으면 빈 리스트를 반환한다")
        void success_emptyList() {
            // given
            String groupId = "empty-group";

            // when
            FindGroupAccountListServiceResponse response = findGroupAccountListService
                .findGroupAccountList(groupId);

            // then
            assertThat(response).isNotNull();
            assertThat(response.groupAccounts()).isEmpty();
        }

        @Test
        @DisplayName("[success] 특정 그룹의 계정만 조회한다")
        void success_specificGroup() {
            // given
            String targetGroupId = "target-group";
            String otherGroupId = "other-group";

            GroupAccount targetAccount1 = GroupAccount.builder()
                .id(1L)
                .groupId(targetGroupId)
                .accountId(1L)
                .email("target1@example.com")
                .status("ACTIVE")
                .build();

            GroupAccount targetAccount2 = GroupAccount.builder()
                .id(2L)
                .groupId(targetGroupId)
                .accountId(2L)
                .email("target2@example.com")
                .status("ACTIVE")
                .build();

            GroupAccount otherAccount = GroupAccount.builder()
                .id(3L)
                .groupId(otherGroupId)
                .accountId(3L)
                .email("other@example.com")
                .status("ACTIVE")
                .build();

            fakeGroupStoragePort.groupAccountDatabase.add(targetAccount1);
            fakeGroupStoragePort.groupAccountDatabase.add(targetAccount2);
            fakeGroupStoragePort.groupAccountDatabase.add(otherAccount);

            // when
            FindGroupAccountListServiceResponse response = findGroupAccountListService
                .findGroupAccountList(targetGroupId);

            // then
            assertThat(response).isNotNull();
            assertThat(response.groupAccounts()).hasSize(2);
            assertThat(response.groupAccounts())
                .extracting("email")
                .containsExactly("target1@example.com", "target2@example.com");
        }

        @Test
        @DisplayName("[success] 여러 상태의 계정을 모두 조회한다")
        void success_multipleStatuses() {
            // given
            String groupId = "test-group";

            GroupAccount activeAccount = GroupAccount.builder()
                .id(1L)
                .groupId(groupId)
                .accountId(1L)
                .email("active@example.com")
                .status("ACTIVE")
                .build();

            GroupAccount pendingAccount = GroupAccount.builder()
                .id(2L)
                .groupId(groupId)
                .accountId(2L)
                .email("pending@example.com")
                .status("PENDING")
                .build();

            GroupAccount rejectedAccount = GroupAccount.builder()
                .id(3L)
                .groupId(groupId)
                .accountId(3L)
                .email("rejected@example.com")
                .status("REJECTED")
                .build();

            fakeGroupStoragePort.groupAccountDatabase.add(activeAccount);
            fakeGroupStoragePort.groupAccountDatabase.add(pendingAccount);
            fakeGroupStoragePort.groupAccountDatabase.add(rejectedAccount);

            // when
            FindGroupAccountListServiceResponse response = findGroupAccountListService
                .findGroupAccountList(groupId);

            // then
            assertThat(response).isNotNull();
            assertThat(response.groupAccounts()).hasSize(3);
            assertThat(response.groupAccounts())
                .extracting("status")
                .containsExactly("ACTIVE", "PENDING", "REJECTED");
        }

        @Test
        @DisplayName("[success] 대량의 계정 목록을 조회할 수 있다")
        void success_largeList() {
            // given
            String groupId = "large-group";

            for (int i = 1; i <= 100; i++) {
                GroupAccount account = GroupAccount.builder()
                    .id((long) i)
                    .groupId(groupId)
                    .accountId((long) i)
                    .email("user" + i + "@example.com")
                    .status("ACTIVE")
                    .build();
                fakeGroupStoragePort.groupAccountDatabase.add(account);
            }

            // when
            FindGroupAccountListServiceResponse response = findGroupAccountListService
                .findGroupAccountList(groupId);

            // then
            assertThat(response).isNotNull();
            assertThat(response.groupAccounts()).hasSize(100);
        }

        @Test
        @DisplayName("[success] null이 아닌 빈 문자열 그룹 ID로 조회하면 빈 리스트를 반환한다")
        void success_emptyStringGroupId() {
            // given
            String groupId = "";

            GroupAccount account = GroupAccount.builder()
                .id(1L)
                .groupId("actual-group")
                .accountId(1L)
                .email("user@example.com")
                .status("ACTIVE")
                .build();
            fakeGroupStoragePort.groupAccountDatabase.add(account);

            // when
            FindGroupAccountListServiceResponse response = findGroupAccountListService
                .findGroupAccountList(groupId);

            // then
            assertThat(response).isNotNull();
            assertThat(response.groupAccounts()).isEmpty();
        }
    }
}
