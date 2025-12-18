package com.odcloud.application.service.find_pending_group_accounts;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.Group;
import com.odcloud.domain.model.GroupAccount;
import com.odcloud.fakeClass.FakeGroupStoragePort;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FindPendingGroupAccountsServiceTest {

    private FakeGroupStoragePort fakeGroupStoragePort;
    private FindPendingGroupAccountsService service;

    @BeforeEach
    void setUp() {
        fakeGroupStoragePort = new FakeGroupStoragePort();
        service = new FindPendingGroupAccountsService(fakeGroupStoragePort);
    }

    @Nested
    @DisplayName("[findPendingAccounts] 그룹 오너의 PENDING 상태 사용자 목록 조회")
    class Describe_findPendingAccounts {

        @Test
        @DisplayName("[success] 오너인 그룹의 PENDING 상태 사용자만 조회한다")
        void success() {
            // given
            String ownerEmail = "owner@example.com";
            Account owner = Account.builder()
                .id(1L)
                .email(ownerEmail)
                .nickname("Owner")
                .name("오너")
                .groups(new ArrayList<>())
                .build();

            Group ownerGroup1 = Group.builder()
                .id("owner-group-1")
                .name("Owner Group 1")
                .ownerEmail(ownerEmail)
                .build();

            Group ownerGroup2 = Group.builder()
                .id("owner-group-2")
                .name("Owner Group 2")
                .ownerEmail(ownerEmail)
                .build();

            Group otherGroup = Group.builder()
                .id("other-group")
                .name("Other Group")
                .ownerEmail("other@example.com")
                .build();

            fakeGroupStoragePort.groupDatabase.add(ownerGroup1);
            fakeGroupStoragePort.groupDatabase.add(ownerGroup2);
            fakeGroupStoragePort.groupDatabase.add(otherGroup);

            LocalDateTime requestDate1 = LocalDateTime.of(2025, 1, 1, 10, 0);
            LocalDateTime requestDate2 = LocalDateTime.of(2025, 1, 2, 11, 0);
            LocalDateTime requestDate3 = LocalDateTime.of(2025, 1, 3, 12, 0);

            GroupAccount pending1 = GroupAccount.builder()
                .id(1L)
                .groupId("owner-group-1")
                .groupName("Owner Group 1")
                .accountId(10L)
                .nickName("User1")
                .status("PENDING")
                .regDt(requestDate1)
                .build();

            GroupAccount pending2 = GroupAccount.builder()
                .id(2L)
                .groupId("owner-group-1")
                .groupName("Owner Group 1")
                .accountId(11L)
                .nickName("User2")
                .status("PENDING")
                .regDt(requestDate2)
                .build();

            GroupAccount active1 = GroupAccount.builder()
                .id(3L)
                .groupId("owner-group-1")
                .groupName("Owner Group 1")
                .accountId(12L)
                .nickName("User3")
                .status("ACTIVE")
                .regDt(LocalDateTime.now())
                .build();

            GroupAccount pending3 = GroupAccount.builder()
                .id(4L)
                .groupId("owner-group-2")
                .groupName("Owner Group 2")
                .accountId(13L)
                .nickName("User4")
                .status("PENDING")
                .regDt(requestDate3)
                .build();

            GroupAccount pendingOther = GroupAccount.builder()
                .id(5L)
                .groupId("other-group")
                .groupName("Other Group")
                .accountId(14L)
                .nickName("User5")
                .status("PENDING")
                .regDt(LocalDateTime.now())
                .build();

            fakeGroupStoragePort.groupAccountDatabase.add(pending1);
            fakeGroupStoragePort.groupAccountDatabase.add(pending2);
            fakeGroupStoragePort.groupAccountDatabase.add(active1);
            fakeGroupStoragePort.groupAccountDatabase.add(pending3);
            fakeGroupStoragePort.groupAccountDatabase.add(pendingOther);

            // when
            FindPendingGroupAccountsServiceResponse response = service.findPendingAccounts(owner);

            // then
            assertThat(response).isNotNull();
            assertThat(response.groups()).hasSize(2);

            assertThat(response.groups().get(0).groupId()).isEqualTo("owner-group-1");
            assertThat(response.groups().get(0).groupName()).isEqualTo("Owner Group 1");
            assertThat(response.groups().get(0).pendingAccounts()).hasSize(2);
            assertThat(response.groups().get(0).pendingAccounts().get(0).accountId()).isEqualTo(
                10L);
            assertThat(response.groups().get(0).pendingAccounts().get(0).nickname()).isEqualTo(
                "User1");
            assertThat(response.groups().get(0).pendingAccounts().get(0).requestDate()).isEqualTo(
                requestDate1);
            assertThat(response.groups().get(0).pendingAccounts().get(1).accountId()).isEqualTo(
                11L);

            assertThat(response.groups().get(1).groupId()).isEqualTo("owner-group-2");
            assertThat(response.groups().get(1).groupName()).isEqualTo("Owner Group 2");
            assertThat(response.groups().get(1).pendingAccounts()).hasSize(1);
            assertThat(response.groups().get(1).pendingAccounts().get(0).accountId()).isEqualTo(
                13L);
            assertThat(response.groups().get(1).pendingAccounts().get(0).nickname()).isEqualTo(
                "User4");
            assertThat(response.groups().get(1).pendingAccounts().get(0).requestDate()).isEqualTo(
                requestDate3);
        }

        @Test
        @DisplayName("[success] PENDING 상태 사용자가 없으면 빈 목록을 반환한다")
        void success_noPendingAccounts() {
            // given
            String ownerEmail = "owner@example.com";
            Account owner = Account.builder()
                .id(1L)
                .email(ownerEmail)
                .nickname("Owner")
                .name("오너")
                .groups(new ArrayList<>())
                .build();

            Group ownerGroup = Group.builder()
                .id("owner-group")
                .name("Owner Group")
                .ownerEmail(ownerEmail)
                .build();

            fakeGroupStoragePort.groupDatabase.add(ownerGroup);

            GroupAccount active1 = GroupAccount.builder()
                .id(1L)
                .groupId("owner-group")
                .accountId(10L)
                .nickName("User1")
                .status("ACTIVE")
                .regDt(LocalDateTime.now())
                .build();

            fakeGroupStoragePort.groupAccountDatabase.add(active1);

            // when
            FindPendingGroupAccountsServiceResponse response = service.findPendingAccounts(owner);

            // then
            assertThat(response).isNotNull();
            assertThat(response.groups()).isEmpty();
        }

        @Test
        @DisplayName("[success] 오너인 그룹이 없으면 빈 목록을 반환한다")
        void success_noOwnerGroups() {
            // given
            String ownerEmail = "owner@example.com";
            Account owner = Account.builder()
                .id(1L)
                .email(ownerEmail)
                .nickname("Owner")
                .name("오너")
                .groups(new ArrayList<>())
                .build();

            Group otherGroup = Group.builder()
                .id("other-group")
                .name("Other Group")
                .ownerEmail("other@example.com")
                .build();

            fakeGroupStoragePort.groupDatabase.add(otherGroup);

            // when
            FindPendingGroupAccountsServiceResponse response = service.findPendingAccounts(owner);

            // then
            assertThat(response).isNotNull();
            assertThat(response.groups()).isEmpty();
        }
    }
}
