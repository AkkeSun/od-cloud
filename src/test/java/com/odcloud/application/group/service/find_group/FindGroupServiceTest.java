package com.odcloud.application.group.service.find_group;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.odcloud.domain.model.Group;
import com.odcloud.domain.model.GroupAccount;
import com.odcloud.domain.model.Notice;
import com.odcloud.fakeClass.FakeGroupStoragePort;
import com.odcloud.fakeClass.FakeNoticeStoragePort;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FindGroupServiceTest {

    private FakeGroupStoragePort fakeGroupStoragePort;
    private FakeNoticeStoragePort fakeNoticeStoragePort;
    private FindGroupService findGroupService;

    @BeforeEach
    void setUp() {
        fakeGroupStoragePort = new FakeGroupStoragePort();
        fakeNoticeStoragePort = new FakeNoticeStoragePort();
        findGroupService = new FindGroupService(fakeGroupStoragePort,
            fakeNoticeStoragePort);
    }

    @Nested
    @DisplayName("[findById] 그룹 ID로 그룹 상세 정보 조회")
    class Describe_findById {

        @Test
        @DisplayName("[success] 그룹 상세 정보를 정상적으로 조회한다")
        void success() {
            // given
            String groupId = "test-group";
            LocalDateTime now = LocalDateTime.now();

            Group group = Group.builder()
                .id(groupId)
                .ownerEmail("manager@example.com")
                .name("Development Team")
                .regDt(now)
                .build();

            GroupAccount manager = GroupAccount.builder()
                .id(1L)
                .groupId(groupId)
                .accountId(1L)
                .nickName("Manager")
                .email("manager@example.com")
                .status("ACTIVE")
                .build();

            GroupAccount member1 = GroupAccount.builder()
                .id(2L)
                .groupId(groupId)
                .accountId(2L)
                .nickName("Member1")
                .email("member1@example.com")
                .status("ACTIVE")
                .build();

            GroupAccount member2 = GroupAccount.builder()
                .id(3L)
                .groupId(groupId)
                .accountId(3L)
                .nickName("Member2")
                .email("member2@example.com")
                .status("ACTIVE")
                .build();

            group.updateGroupMembers(List.of(manager, member1, member2));
            fakeGroupStoragePort.groupDatabase.add(group);

            Notice notice1 = Notice.builder()
                .id(1L)
                .groupId(groupId)
                .title("공지사항 1")
                .content("내용 1")
                .regDt(now.minusDays(1))
                .build();

            Notice notice2 = Notice.builder()
                .id(2L)
                .groupId(groupId)
                .title("공지사항 2")
                .content("내용 2")
                .regDt(now)
                .build();

            fakeNoticeStoragePort.database.add(notice1);
            fakeNoticeStoragePort.database.add(notice2);

            // when
            FindGroupServiceResponse response = findGroupService.findById(groupId);

            // then
            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo(groupId);
            assertThat(response.name()).isEqualTo("Development Team");

            assertThat(response.manager()).isNotNull();
            assertThat(response.manager().nickname()).isEqualTo("Manager");
            assertThat(response.manager().email()).isEqualTo("manager@example.com");

            assertThat(response.members()).hasSize(2);
            assertThat(response.members())
                .extracting(FindGroupServiceResponse.MemberInfo::nickname)
                .containsExactlyInAnyOrder("Member1", "Member2");

            assertThat(response.activeMemberCount()).isEqualTo(3);

            assertThat(response.notices()).hasSize(2);
            assertThat(response.notices().get(0).title()).isEqualTo("공지사항 2");
            assertThat(response.notices().get(1).title()).isEqualTo("공지사항 1");
        }

        @Test
        @DisplayName("[success] 멤버가 그룹장만 있는 경우 빈 members 리스트를 반환한다")
        void success_onlyManager() {
            // given
            String groupId = "test-group";
            LocalDateTime now = LocalDateTime.now();

            Group group = Group.builder()
                .id(groupId)
                .ownerEmail("manager@example.com")
                .name("Small Team")
                .regDt(now)
                .build();

            GroupAccount manager = GroupAccount.builder()
                .id(1L)
                .groupId(groupId)
                .accountId(1L)
                .nickName("Manager")
                .email("manager@example.com")
                .status("ACTIVE")
                .build();

            group.updateGroupMembers(List.of(manager));
            fakeGroupStoragePort.groupDatabase.add(group);

            // when
            FindGroupServiceResponse response = findGroupService.findById(groupId);

            // then
            assertThat(response.manager()).isNotNull();
            assertThat(response.members()).isEmpty();
            assertThat(response.activeMemberCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("[success] 공지사항이 없는 경우 빈 notices 리스트를 반환한다")
        void success_noNotices() {
            // given
            String groupId = "test-group";
            LocalDateTime now = LocalDateTime.now();

            Group group = Group.builder()
                .id(groupId)
                .ownerEmail("manager@example.com")
                .name("New Team")
                .regDt(now)
                .build();

            GroupAccount manager = GroupAccount.builder()
                .id(1L)
                .groupId(groupId)
                .accountId(1L)
                .nickName("Manager")
                .email("manager@example.com")
                .status("ACTIVE")
                .build();

            group.updateGroupMembers(List.of(manager));
            fakeGroupStoragePort.groupDatabase.add(group);

            // when
            FindGroupServiceResponse response = findGroupService.findById(groupId);

            // then
            assertThat(response.notices()).isEmpty();
        }

        @Test
        @DisplayName("[success] 공지사항이 5개를 초과하는 경우 최근 5개만 반환한다")
        void success_limitNotices() {
            // given
            String groupId = "test-group";
            LocalDateTime now = LocalDateTime.now();

            Group group = Group.builder()
                .id(groupId)
                .ownerEmail("manager@example.com")
                .name("Active Team")
                .regDt(now)
                .build();

            GroupAccount manager = GroupAccount.builder()
                .id(1L)
                .groupId(groupId)
                .accountId(1L)
                .nickName("Manager")
                .email("manager@example.com")
                .status("ACTIVE")
                .build();

            group.updateGroupMembers(List.of(manager));
            fakeGroupStoragePort.groupDatabase.add(group);

            // 7개의 공지사항 생성
            for (int i = 1; i <= 7; i++) {
                Notice notice = Notice.builder()
                    .id((long) i)
                    .groupId(groupId)
                    .title("공지사항 " + i)
                    .content("내용 " + i)
                    .regDt(now.minusDays(7 - i))
                    .build();
                fakeNoticeStoragePort.database.add(notice);
            }

            // when
            FindGroupServiceResponse response = findGroupService.findById(groupId);

            // then
            assertThat(response.notices()).hasSize(5);
            assertThat(response.notices().get(0).title()).isEqualTo("공지사항 7");
        }

        @Test
        @DisplayName("[success] PENDING 상태의 멤버는 members에서 제외되지만 activeMemberCount에는 포함되지 않는다")
        void success_excludePendingMembers() {
            // given
            String groupId = "test-group";
            LocalDateTime now = LocalDateTime.now();

            Group group = Group.builder()
                .id(groupId)
                .ownerEmail("manager@example.com")
                .name("Mixed Team")
                .regDt(now)
                .build();

            GroupAccount manager = GroupAccount.builder()
                .id(1L)
                .groupId(groupId)
                .accountId(1L)
                .nickName("Manager")
                .email("manager@example.com")
                .status("ACTIVE")
                .build();

            GroupAccount activeMember = GroupAccount.builder()
                .id(2L)
                .groupId(groupId)
                .accountId(2L)
                .nickName("ActiveMember")
                .email("active@example.com")
                .status("ACTIVE")
                .build();

            GroupAccount pendingMember = GroupAccount.builder()
                .id(3L)
                .groupId(groupId)
                .accountId(3L)
                .nickName("PendingMember")
                .email("pending@example.com")
                .status("PENDING")
                .build();

            group.updateGroupMembers(List.of(manager, activeMember, pendingMember));
            fakeGroupStoragePort.groupDatabase.add(group);

            // when
            FindGroupServiceResponse response = findGroupService.findById(groupId);

            // then
            assertThat(response.members()).hasSize(1);
            assertThat(response.members().get(0).nickname()).isEqualTo("ActiveMember");
            assertThat(response.activeMemberCount()).isEqualTo(2);
        }

        @Test
        @DisplayName("[error] 존재하지 않는 그룹 ID인 경우 예외를 발생시킨다")
        void error_groupNotFound() {
            // given
            String nonExistentGroupId = "non-existent-group";

            // when & then
            assertThrows(CustomBusinessException.class,
                () -> findGroupService.findById(nonExistentGroupId));
        }
    }
}
