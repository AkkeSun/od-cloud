package com.odcloud.application.group.service.find_group_self;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.Group;
import com.odcloud.domain.model.GroupAccount;
import com.odcloud.fakeClass.FakeGroupStoragePort;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FindGroupSelfServiceTest {

    private FakeGroupStoragePort fakeGroupStoragePort;
    private FindGroupSelfService findGroupSelfService;

    @BeforeEach
    void setUp() {
        fakeGroupStoragePort = new FakeGroupStoragePort();
        findGroupSelfService = new FindGroupSelfService(fakeGroupStoragePort);
    }

    @Nested
    @DisplayName("[findSelf] 현재 사용자의 그룹 정보 조회")
    class Describe_findSelf {

        @Test
        @DisplayName("[success] 가입된 그룹과 대기중인 그룹을 분리하여 조회한다")
        void success_separateActiveAndPendingGroups() {
            // given
            Long userId = 1L;
            String userEmail = "user1@example.com";

            // ACTIVE 그룹 설정
            Group activeGroup = Group.builder()
                .id("group-1")
                .ownerEmail("manager1@example.com")
                .name("Development Team")
                .regDt(LocalDateTime.of(2024, 1, 1, 12, 0))
                .build();

            GroupAccount userActiveAccount = GroupAccount.builder()
                .id(1L)
                .groupId("group-1")
                .groupName("Development Team")
                .accountId(userId)
                .nickName("User1")
                .email(userEmail)
                .status("ACTIVE")
                .showYn("Y")
                .build();

            GroupAccount otherActiveMember = GroupAccount.builder()
                .id(2L)
                .groupId("group-1")
                .accountId(2L)
                .nickName("User2")
                .email("user2@example.com")
                .status("ACTIVE")
                .build();

            GroupAccount ownerActiveMember = GroupAccount.builder()
                .id(3L)
                .groupId("group-1")
                .accountId(2L)
                .nickName("User2")
                .email("manager1@example.com")
                .status("ACTIVE")
                .build();

            activeGroup.updateGroupMembers(
                List.of(userActiveAccount, otherActiveMember, ownerActiveMember));

            // PENDING 그룹 설정
            Group pendingGroup = Group.builder()
                .id("group-2")
                .ownerEmail("manager2@example.com")
                .name("Marketing Team")
                .regDt(LocalDateTime.of(2024, 1, 2, 12, 0))
                .build();

            GroupAccount userPendingAccount = GroupAccount.builder()
                .id(3L)
                .groupId("group-2")
                .groupName("Marketing Team")
                .accountId(userId)
                .nickName("User1")
                .email(userEmail)
                .status("PENDING")
                .showYn("Y")
                .build();

            GroupAccount managerMember = GroupAccount.builder()
                .id(4L)
                .groupId("group-2")
                .accountId(3L)
                .nickName("Manager2")
                .email("manager2@example.com")
                .status("ACTIVE")
                .build();

            pendingGroup.updateGroupMembers(List.of(userPendingAccount, managerMember));

            fakeGroupStoragePort.groupDatabase.add(activeGroup);
            fakeGroupStoragePort.groupDatabase.add(pendingGroup);
            fakeGroupStoragePort.groupAccountDatabase.add(userActiveAccount);
            fakeGroupStoragePort.groupAccountDatabase.add(userPendingAccount);

            Account account = Account.builder()
                .id(userId)
                .email(userEmail)
                .build();

            // when
            FindGroupSelfServiceResponse response = findGroupSelfService.findSelf(account);

            // then
            assertThat(response.activeGroups()).hasSize(1);
            assertThat(response.pendingGroups()).hasSize(1);
            assertThat(response.deniedGroups()).isEmpty();

            FindGroupSelfServiceResponse.ActiveGroupInfo activeGroupInfo = response.activeGroups()
                .get(0);
            assertThat(activeGroupInfo.name()).isEqualTo("Development Team");

            FindGroupSelfServiceResponse.PendingGroupInfo pendingGroupInfo = response.pendingGroups()
                .get(0);
            assertThat(pendingGroupInfo.name()).isEqualTo("Marketing Team");
            assertThat(pendingGroupInfo.activeMemberCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("[success] 사용자가 속한 그룹이 없으면 빈 목록을 반환한다")
        void success_emptyGroups() {
            // given
            Long userId = 1L;
            String userEmail = "user@example.com";

            Account account = Account.builder()
                .id(userId)
                .email(userEmail)
                .build();

            // when
            FindGroupSelfServiceResponse response = findGroupSelfService.findSelf(account);

            // then
            assertThat(response.activeGroups()).isEmpty();
            assertThat(response.pendingGroups()).isEmpty();
            assertThat(response.deniedGroups()).isEmpty();
        }

        @Test
        @DisplayName("[success] 거부된 그룹도 조회할 수 있다")
        void success_includeDeniedGroups() {
            // given
            Long userId = 1L;
            String userEmail = "user1@example.com";

            // ACTIVE 그룹 설정
            Group activeGroup = Group.builder()
                .id("group-1")
                .ownerEmail("manager1@example.com")
                .name("Development Team")
                .regDt(LocalDateTime.of(2024, 1, 1, 12, 0))
                .build();

            GroupAccount userActiveAccount = GroupAccount.builder()
                .id(1L)
                .groupId("group-1")
                .groupName("Development Team")
                .accountId(userId)
                .nickName("User1")
                .email(userEmail)
                .status("ACTIVE")
                .showYn("Y")
                .build();

            GroupAccount managerMember = GroupAccount.builder()
                .id(2L)
                .groupId("group-1")
                .accountId(2L)
                .nickName("Manager1")
                .email("manager1@example.com")
                .status("ACTIVE")
                .build();

            activeGroup.updateGroupMembers(List.of(userActiveAccount, managerMember));

            // DENIED 그룹 설정
            Group deniedGroup = Group.builder()
                .id("group-2")
                .ownerEmail("manager2@example.com")
                .name("Marketing Team")
                .regDt(LocalDateTime.of(2024, 1, 2, 12, 0))
                .build();

            GroupAccount userDeniedAccount = GroupAccount.builder()
                .id(3L)
                .groupId("group-2")
                .groupName("Marketing Team")
                .accountId(userId)
                .nickName("User1")
                .email(userEmail)
                .status("DENIED")
                .memo("요청이 거부되었습니다")
                .showYn("Y")
                .build();

            GroupAccount managerMember2 = GroupAccount.builder()
                .id(4L)
                .groupId("group-2")
                .accountId(3L)
                .nickName("Manager2")
                .email("manager2@example.com")
                .status("ACTIVE")
                .build();

            deniedGroup.updateGroupMembers(List.of(userDeniedAccount, managerMember2));

            fakeGroupStoragePort.groupDatabase.add(activeGroup);
            fakeGroupStoragePort.groupDatabase.add(deniedGroup);
            fakeGroupStoragePort.groupAccountDatabase.add(userActiveAccount);
            fakeGroupStoragePort.groupAccountDatabase.add(userDeniedAccount);

            Account account = Account.builder()
                .id(userId)
                .email(userEmail)
                .build();

            // when
            FindGroupSelfServiceResponse response = findGroupSelfService.findSelf(account);

            // then
            assertThat(response.activeGroups()).hasSize(1);
            assertThat(response.pendingGroups()).isEmpty();
            assertThat(response.deniedGroups()).hasSize(1);

            FindGroupSelfServiceResponse.ActiveGroupInfo activeGroupInfo = response.activeGroups()
                .get(0);
            assertThat(activeGroupInfo.name()).isEqualTo("Development Team");

            FindGroupSelfServiceResponse.DeniedGroupInfo deniedGroupInfo = response.deniedGroups()
                .get(0);
            assertThat(deniedGroupInfo.id()).isEqualTo("group-2");
            assertThat(deniedGroupInfo.name()).isEqualTo("Marketing Team");
            assertThat(deniedGroupInfo.deniedCause()).isEqualTo("요청이 거부되었습니다");
        }
    }
}
