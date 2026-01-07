package com.odcloud.application.group.service.delete_notice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.odcloud.application.port.in.command.DeleteNoticeCommand;
import com.odcloud.domain.model.Account;
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

class DeleteNoticeServiceTest {

    private FakeGroupStoragePort fakeGroupStoragePort;
    private FakeNoticeStoragePort fakeNoticeStoragePort;
    private DeleteNoticeService deleteNoticeService;

    @BeforeEach
    void setUp() {
        fakeGroupStoragePort = new FakeGroupStoragePort();
        fakeNoticeStoragePort = new FakeNoticeStoragePort();
        deleteNoticeService = new DeleteNoticeService(fakeGroupStoragePort,
            fakeNoticeStoragePort);
    }

    @Nested
    @DisplayName("[delete] 공지사항 삭제")
    class Describe_delete {

        @Test
        @DisplayName("[success] 그룹장이 공지사항을 삭제한다")
        void success() {
            // given
            Long groupId = 1L;
            String ownerEmail = "owner@example.com";
            LocalDateTime now = LocalDateTime.now();

            Group group = Group.builder()
                .id(groupId)
                .ownerEmail(ownerEmail)
                .name("Test Group")
                .regDt(now)
                .build();

            GroupAccount owner = GroupAccount.builder()
                .id(1L)
                .groupId(groupId)
                .accountId(1L)
                .email(ownerEmail)
                .nickName("Owner")
                .status("ACTIVE")
                .build();

            group.updateGroupMembers(List.of(owner));
            fakeGroupStoragePort.groupDatabase.add(group);

            Notice notice = Notice.builder()
                .id(1L)
                .groupId(groupId)
                .title("공지사항")
                .content("내용")
                .writerEmail(ownerEmail)
                .regDt(now)
                .build();
            fakeNoticeStoragePort.database.add(notice);

            Account account = Account.builder()
                .id(1L)
                .email(ownerEmail)
                .build();

            DeleteNoticeCommand command = DeleteNoticeCommand.builder()
                .groupId(groupId)
                .noticeId(1L)
                .account(account)
                .build();

            // when
            DeleteNoticeServiceResponse response = deleteNoticeService.delete(command);

            // then
            assertThat(response.result()).isTrue();
            assertThat(fakeNoticeStoragePort.database).isEmpty();
        }

        @Test
        @DisplayName("[error] 그룹장이 아닌 사용자가 삭제를 시도하면 예외가 발생한다")
        void error_notGroupOwner() {
            // given
            Long groupId = 1L;
            String ownerEmail = "owner@example.com";
            String memberEmail = "member@example.com";
            LocalDateTime now = LocalDateTime.now();

            Group group = Group.builder()
                .id(groupId)
                .ownerEmail(ownerEmail)
                .name("Test Group")
                .regDt(now)
                .build();

            GroupAccount owner = GroupAccount.builder()
                .id(1L)
                .groupId(groupId)
                .accountId(1L)
                .email(ownerEmail)
                .nickName("Owner")
                .status("ACTIVE")
                .build();

            group.updateGroupMembers(List.of(owner));
            fakeGroupStoragePort.groupDatabase.add(group);

            Notice notice = Notice.builder()
                .id(1L)
                .groupId(groupId)
                .title("공지사항")
                .content("내용")
                .writerEmail(ownerEmail)
                .regDt(now)
                .build();
            fakeNoticeStoragePort.database.add(notice);

            Account account = Account.builder()
                .id(2L)
                .email(memberEmail)
                .build();

            DeleteNoticeCommand command = DeleteNoticeCommand.builder()
                .groupId(groupId)
                .noticeId(1L)
                .account(account)
                .build();

            // when & then
            assertThrows(CustomBusinessException.class,
                () -> deleteNoticeService.delete(command));
            assertThat(fakeNoticeStoragePort.database).hasSize(1);
        }

        @Test
        @DisplayName("[error] 존재하지 않는 공지사항 ID인 경우 예외가 발생한다")
        void error_noticeNotFound() {
            // given
            Long groupId = 1L;
            String ownerEmail = "owner@example.com";
            LocalDateTime now = LocalDateTime.now();

            Group group = Group.builder()
                .id(groupId)
                .ownerEmail(ownerEmail)
                .name("Test Group")
                .regDt(now)
                .build();

            GroupAccount owner = GroupAccount.builder()
                .id(1L)
                .groupId(groupId)
                .accountId(1L)
                .email(ownerEmail)
                .nickName("Owner")
                .status("ACTIVE")
                .build();

            group.updateGroupMembers(List.of(owner));
            fakeGroupStoragePort.groupDatabase.add(group);

            Account account = Account.builder()
                .id(1L)
                .email(ownerEmail)
                .build();

            DeleteNoticeCommand command = DeleteNoticeCommand.builder()
                .groupId(groupId)
                .noticeId(999L)
                .account(account)
                .build();

            // when & then
            assertThrows(CustomBusinessException.class,
                () -> deleteNoticeService.delete(command));
        }

        @Test
        @DisplayName("[error] 다른 그룹의 공지사항을 삭제하려고 하면 예외가 발생한다")
        void error_noticeFromDifferentGroup() {
            // given
            Long groupId = 1L;
            Long otherGroupId = 2L;
            String ownerEmail = "owner@example.com";
            LocalDateTime now = LocalDateTime.now();

            Group group = Group.builder()
                .id(groupId)
                .ownerEmail(ownerEmail)
                .name("Test Group")
                .regDt(now)
                .build();

            GroupAccount owner = GroupAccount.builder()
                .id(1L)
                .groupId(groupId)
                .accountId(1L)
                .email(ownerEmail)
                .nickName("Owner")
                .status("ACTIVE")
                .build();

            group.updateGroupMembers(List.of(owner));
            fakeGroupStoragePort.groupDatabase.add(group);

            Notice notice = Notice.builder()
                .id(1L)
                .groupId(otherGroupId)
                .title("다른 그룹 공지사항")
                .content("내용")
                .writerEmail("other@example.com")
                .regDt(now)
                .build();
            fakeNoticeStoragePort.database.add(notice);

            Account account = Account.builder()
                .id(1L)
                .email(ownerEmail)
                .build();

            DeleteNoticeCommand command = DeleteNoticeCommand.builder()
                .groupId(groupId)
                .noticeId(1L)
                .account(account)
                .build();

            // when & then
            assertThrows(CustomBusinessException.class,
                () -> deleteNoticeService.delete(command));
            assertThat(fakeNoticeStoragePort.database).hasSize(1);
        }

        @Test
        @DisplayName("[error] 존재하지 않는 그룹 ID인 경우 예외가 발생한다")
        void error_groupNotFound() {
            // given
            Long nonExistentGroupId = 10L;
            String ownerEmail = "owner@example.com";

            Account account = Account.builder()
                .id(1L)
                .email(ownerEmail)
                .build();

            DeleteNoticeCommand command = DeleteNoticeCommand.builder()
                .groupId(nonExistentGroupId)
                .noticeId(1L)
                .account(account)
                .build();

            // when & then
            assertThrows(CustomBusinessException.class,
                () -> deleteNoticeService.delete(command));
        }
    }
}
