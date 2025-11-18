package com.odcloud.application.service.find_groups;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.application.port.in.command.FindGroupsCommand;
import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.Group;
import com.odcloud.domain.model.GroupAccount;
import com.odcloud.fakeClass.FakeGroupStoragePort;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FindGroupsServiceTest {

    private FakeGroupStoragePort fakeGroupStoragePort;
    private FindGroupsService findGroupsService;

    @BeforeEach
    void setUp() {
        fakeGroupStoragePort = new FakeGroupStoragePort();
        findGroupsService = new FindGroupsService(fakeGroupStoragePort);
    }

    @Nested
    @DisplayName("[findAll] 그룹 목록 조회")
    class Describe_findAll {

        @Test
        @DisplayName("[success] 모든 그룹과 사용자의 그룹 가입 상태를 조회한다")
        void success_findAllWithStatus() {
            // given
            Account account = Account.builder()
                .id(1L)
                .email("user@example.com")
                .build();

            Group group1 = Group.builder()
                .id("group-1")
                .ownerEmail("owner1@example.com")
                .description("Development Team")
                .regDt(LocalDateTime.of(2024, 1, 1, 12, 0))
                .build();

            Group group2 = Group.builder()
                .id("group-2")
                .ownerEmail("owner2@example.com")
                .description("Marketing Team")
                .regDt(LocalDateTime.of(2024, 1, 2, 12, 0))
                .build();

            Group group3 = Group.builder()
                .id("group-3")
                .ownerEmail("owner3@example.com")
                .description("Sales Team")
                .regDt(LocalDateTime.of(2024, 1, 3, 12, 0))
                .build();

            fakeGroupStoragePort.groupDatabase.add(group1);
            fakeGroupStoragePort.groupDatabase.add(group2);
            fakeGroupStoragePort.groupDatabase.add(group3);

            GroupAccount groupAccount1 = GroupAccount.builder()
                .id(1L)
                .groupId("group-1")
                .accountId(1L)
                .status("ACTIVE")
                .build();

            GroupAccount groupAccount2 = GroupAccount.builder()
                .id(2L)
                .groupId("group-2")
                .accountId(1L)
                .status("PENDING")
                .build();

            GroupAccount groupAccount3 = GroupAccount.builder()
                .id(3L)
                .groupId("group-3")
                .accountId(1L)
                .status("ACTIVE")
                .build();

            fakeGroupStoragePort.groupAccountDatabase.add(groupAccount1);
            fakeGroupStoragePort.groupAccountDatabase.add(groupAccount2);
            fakeGroupStoragePort.groupAccountDatabase.add(groupAccount3);

            FindGroupsCommand command = FindGroupsCommand.builder()
                .account(account)
                .build();

            // when
            FindGroupsServiceResponse response = findGroupsService.findAll(command);

            // then
            assertThat(response.groups()).hasSize(3);

            // group-1 검증
            FindGroupsServiceResponse.GroupResponseItem responseGroup1 = response.groups().get(0);
            assertThat(responseGroup1.id()).isEqualTo("group-1");
            assertThat(responseGroup1.ownerEmail()).isEqualTo("owner1@example.com");
            assertThat(responseGroup1.description()).isEqualTo("Development Team");
            assertThat(responseGroup1.regDt()).isEqualTo("2024-01-01T12:00");
            assertThat(responseGroup1.status()).isEqualTo("ACTIVE");

            // group-2 검증
            FindGroupsServiceResponse.GroupResponseItem responseGroup2 = response.groups().get(1);
            assertThat(responseGroup2.id()).isEqualTo("group-2");
            assertThat(responseGroup2.ownerEmail()).isEqualTo("owner2@example.com");
            assertThat(responseGroup2.description()).isEqualTo("Marketing Team");
            assertThat(responseGroup2.regDt()).isEqualTo("2024-01-02T12:00");
            assertThat(responseGroup2.status()).isEqualTo("PENDING");

            // group-3 검증
            FindGroupsServiceResponse.GroupResponseItem responseGroup3 = response.groups().get(2);
            assertThat(responseGroup3.id()).isEqualTo("group-3");
            assertThat(responseGroup3.ownerEmail()).isEqualTo("owner3@example.com");
            assertThat(responseGroup3.description()).isEqualTo("Sales Team");
            assertThat(responseGroup3.regDt()).isEqualTo("2024-01-03T12:00");
            assertThat(responseGroup3.status()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("[success] 그룹이 없는 경우 빈 목록을 반환한다")
        void success_emptyGroups() {
            // given
            Account account = Account.builder()
                .id(1L)
                .email("user@example.com")
                .build();

            FindGroupsCommand command = FindGroupsCommand.builder()
                .account(account)
                .build();

            // when
            FindGroupsServiceResponse response = findGroupsService.findAll(command);

            // then
            assertThat(response.groups()).isEmpty();
        }

        @Test
        @DisplayName("[success] 사용자가 속하지 않은 그룹은 status가 null이다")
        void success_groupWithoutUserStatus() {
            // given
            Account account = Account.builder()
                .id(1L)
                .email("user@example.com")
                .build();

            Group group1 = Group.builder()
                .id("group-1")
                .ownerEmail("owner1@example.com")
                .description("Development Team")
                .regDt(LocalDateTime.of(2024, 1, 1, 12, 0))
                .build();

            Group group2 = Group.builder()
                .id("group-2")
                .ownerEmail("owner2@example.com")
                .description("Marketing Team")
                .regDt(LocalDateTime.of(2024, 1, 2, 12, 0))
                .build();

            fakeGroupStoragePort.groupDatabase.add(group1);
            fakeGroupStoragePort.groupDatabase.add(group2);

            // group-1에만 사용자 추가
            GroupAccount groupAccount1 = GroupAccount.builder()
                .id(1L)
                .groupId("group-1")
                .accountId(1L)
                .status("ACTIVE")
                .build();

            fakeGroupStoragePort.groupAccountDatabase.add(groupAccount1);

            FindGroupsCommand command = FindGroupsCommand.builder()
                .account(account)
                .build();

            // when
            FindGroupsServiceResponse response = findGroupsService.findAll(command);

            // then
            assertThat(response.groups()).hasSize(2);

            // group-1은 status가 있음
            FindGroupsServiceResponse.GroupResponseItem responseGroup1 = response.groups().get(0);
            assertThat(responseGroup1.id()).isEqualTo("group-1");
            assertThat(responseGroup1.status()).isEqualTo("ACTIVE");

            // group-2는 status가 null
            FindGroupsServiceResponse.GroupResponseItem responseGroup2 = response.groups().get(1);
            assertThat(responseGroup2.id()).isEqualTo("group-2");
            assertThat(responseGroup2.status()).isNull();
        }

        @Test
        @DisplayName("[success] 중복된 그룹 가입 정보가 있을 경우 첫 번째 status를 사용한다")
        void success_duplicateGroupAccountStatus() {
            // given
            Account account = Account.builder()
                .id(1L)
                .email("user@example.com")
                .build();

            Group group1 = Group.builder()
                .id("group-1")
                .ownerEmail("owner1@example.com")
                .description("Development Team")
                .regDt(LocalDateTime.of(2024, 1, 1, 12, 0))
                .build();

            fakeGroupStoragePort.groupDatabase.add(group1);

            // 동일한 그룹에 대해 중복된 가입 정보 추가
            GroupAccount groupAccount1 = GroupAccount.builder()
                .id(1L)
                .groupId("group-1")
                .accountId(1L)
                .status("ACTIVE")
                .build();

            GroupAccount groupAccount2 = GroupAccount.builder()
                .id(2L)
                .groupId("group-1")
                .accountId(1L)
                .status("PENDING")
                .build();

            fakeGroupStoragePort.groupAccountDatabase.add(groupAccount1);
            fakeGroupStoragePort.groupAccountDatabase.add(groupAccount2);

            FindGroupsCommand command = FindGroupsCommand.builder()
                .account(account)
                .build();

            // when
            FindGroupsServiceResponse response = findGroupsService.findAll(command);

            // then
            assertThat(response.groups()).hasSize(1);
            FindGroupsServiceResponse.GroupResponseItem responseGroup = response.groups().get(0);
            assertThat(responseGroup.id()).isEqualTo("group-1");
            // 첫 번째 status가 사용됨 (ACTIVE)
            assertThat(responseGroup.status()).isEqualTo("ACTIVE");
        }
    }
}
