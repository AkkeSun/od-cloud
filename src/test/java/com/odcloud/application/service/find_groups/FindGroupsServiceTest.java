package com.odcloud.application.service.find_groups;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.application.port.in.command.FindGroupsCommand;
import com.odcloud.domain.model.Group;
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
        @DisplayName("[success] keyword가 'all'인 경우 모든 그룹을 조회한다")
        void success_findAllGroups() {
            // given
            Group group1 = Group.builder()
                .id("group-1")
                .ownerEmail("owner1@example.com")
                .name("Development Team")
                .regDt(LocalDateTime.of(2024, 1, 1, 12, 0))
                .build();

            Group group2 = Group.builder()
                .id("group-2")
                .ownerEmail("owner2@example.com")
                .name("Marketing Team")
                .regDt(LocalDateTime.of(2024, 1, 2, 12, 0))
                .build();

            Group group3 = Group.builder()
                .id("group-3")
                .ownerEmail("owner3@example.com")
                .name("Sales Team")
                .regDt(LocalDateTime.of(2024, 1, 3, 12, 0))
                .build();

            fakeGroupStoragePort.groupDatabase.add(group1);
            fakeGroupStoragePort.groupDatabase.add(group2);
            fakeGroupStoragePort.groupDatabase.add(group3);

            FindGroupsCommand command = FindGroupsCommand.builder()
                .keyword("all")
                .build();

            // when
            FindGroupsServiceResponse response = findGroupsService.findAll(command);

            // then
            assertThat(response.groups()).hasSize(3);

            FindGroupsServiceResponse.GroupResponseItem responseGroup1 = response.groups().get(0);
            assertThat(responseGroup1.id()).isEqualTo("group-1");
            assertThat(responseGroup1.ownerEmail()).isEqualTo("owner1@example.com");
            assertThat(responseGroup1.name()).isEqualTo("Development Team");
            assertThat(responseGroup1.regDt()).isEqualTo("2024-01-01T12:00");

            FindGroupsServiceResponse.GroupResponseItem responseGroup2 = response.groups().get(1);
            assertThat(responseGroup2.id()).isEqualTo("group-2");
            assertThat(responseGroup2.ownerEmail()).isEqualTo("owner2@example.com");
            assertThat(responseGroup2.name()).isEqualTo("Marketing Team");
            assertThat(responseGroup2.regDt()).isEqualTo("2024-01-02T12:00");

            FindGroupsServiceResponse.GroupResponseItem responseGroup3 = response.groups().get(2);
            assertThat(responseGroup3.id()).isEqualTo("group-3");
            assertThat(responseGroup3.ownerEmail()).isEqualTo("owner3@example.com");
            assertThat(responseGroup3.name()).isEqualTo("Sales Team");
            assertThat(responseGroup3.regDt()).isEqualTo("2024-01-03T12:00");
        }

        @Test
        @DisplayName("[success] keyword로 Description을 LIKE 검색한다")
        void success_searchByKeyword() {
            // given
            Group group1 = Group.builder()
                .id("group-1")
                .ownerEmail("owner1@example.com")
                .name("Development Team")
                .regDt(LocalDateTime.of(2024, 1, 1, 12, 0))
                .build();

            Group group2 = Group.builder()
                .id("group-2")
                .ownerEmail("owner2@example.com")
                .name("Marketing Team")
                .regDt(LocalDateTime.of(2024, 1, 2, 12, 0))
                .build();

            Group group3 = Group.builder()
                .id("group-3")
                .ownerEmail("owner3@example.com")
                .name("Sales Team")
                .regDt(LocalDateTime.of(2024, 1, 3, 12, 0))
                .build();

            fakeGroupStoragePort.groupDatabase.add(group1);
            fakeGroupStoragePort.groupDatabase.add(group2);
            fakeGroupStoragePort.groupDatabase.add(group3);

            FindGroupsCommand command = FindGroupsCommand.builder()
                .keyword("Team")
                .build();

            // when
            FindGroupsServiceResponse response = findGroupsService.findAll(command);

            // then
            assertThat(response.groups()).hasSize(3);
            assertThat(response.groups())
                .extracting(FindGroupsServiceResponse.GroupResponseItem::name)
                .allMatch(desc -> desc.contains("Team"));
        }

        @Test
        @DisplayName("[success] keyword에 매칭되는 그룹만 반환한다")
        void success_onlyMatchingGroups() {
            // given
            Group group1 = Group.builder()
                .id("group-1")
                .ownerEmail("owner1@example.com")
                .name("개발팀")
                .regDt(LocalDateTime.of(2024, 1, 1, 12, 0))
                .build();

            Group group2 = Group.builder()
                .id("group-2")
                .ownerEmail("owner2@example.com")
                .name("마케팅팀")
                .regDt(LocalDateTime.of(2024, 1, 2, 12, 0))
                .build();

            Group group3 = Group.builder()
                .id("group-3")
                .ownerEmail("owner3@example.com")
                .name("개발 지원팀")
                .regDt(LocalDateTime.of(2024, 1, 3, 12, 0))
                .build();

            fakeGroupStoragePort.groupDatabase.add(group1);
            fakeGroupStoragePort.groupDatabase.add(group2);
            fakeGroupStoragePort.groupDatabase.add(group3);

            FindGroupsCommand command = FindGroupsCommand.builder()
                .keyword("개발")
                .build();

            // when
            FindGroupsServiceResponse response = findGroupsService.findAll(command);

            // then
            assertThat(response.groups()).hasSize(2);
            assertThat(response.groups())
                .extracting(FindGroupsServiceResponse.GroupResponseItem::id)
                .containsExactly("group-1", "group-3");
        }

        @Test
        @DisplayName("[success] 그룹이 없는 경우 빈 목록을 반환한다")
        void success_emptyGroups() {
            // given
            FindGroupsCommand command = FindGroupsCommand.builder()
                .keyword("all")
                .build();

            // when
            FindGroupsServiceResponse response = findGroupsService.findAll(command);

            // then
            assertThat(response.groups()).isEmpty();
        }

        @Test
        @DisplayName("[success] keyword에 매칭되는 그룹이 없으면 빈 목록을 반환한다")
        void success_noMatchingGroups() {
            // given
            Group group1 = Group.builder()
                .id("group-1")
                .ownerEmail("owner1@example.com")
                .name("Development Team")
                .regDt(LocalDateTime.of(2024, 1, 1, 12, 0))
                .build();

            fakeGroupStoragePort.groupDatabase.add(group1);

            FindGroupsCommand command = FindGroupsCommand.builder()
                .keyword("마케팅")
                .build();

            // when
            FindGroupsServiceResponse response = findGroupsService.findAll(command);

            // then
            assertThat(response.groups()).isEmpty();
        }

        @Test
        @DisplayName("[success] keyword가 대소문자 구분 없이 'ALL'이어도 모든 그룹을 조회한다")
        void success_allKeywordCaseInsensitive() {
            // given
            Group group1 = Group.builder()
                .id("group-1")
                .ownerEmail("owner1@example.com")
                .name("Development Team")
                .regDt(LocalDateTime.of(2024, 1, 1, 12, 0))
                .build();

            fakeGroupStoragePort.groupDatabase.add(group1);

            FindGroupsCommand command = FindGroupsCommand.builder()
                .keyword("ALL")
                .build();

            // when
            FindGroupsServiceResponse response = findGroupsService.findAll(command);

            // then
            assertThat(response.groups()).hasSize(1);
        }
    }
}
