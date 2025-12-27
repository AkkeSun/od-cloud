package com.odcloud.application.service.update_notice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.odcloud.application.port.in.command.UpdateNoticeCommand;
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

class UpdateNoticeServiceTest {

    private FakeGroupStoragePort fakeGroupStoragePort;
    private FakeNoticeStoragePort fakeNoticeStoragePort;
    private UpdateNoticeService updateNoticeService;

    @BeforeEach
    void setUp() {
        fakeGroupStoragePort = new FakeGroupStoragePort();
        fakeNoticeStoragePort = new FakeNoticeStoragePort();
        updateNoticeService = new UpdateNoticeService(fakeGroupStoragePort,
            fakeNoticeStoragePort);
    }

    @Nested
    @DisplayName("[update] 공지사항 수정")
    class Describe_update {

        @Test
        @DisplayName("[success] 그룹장이 공지사항을 수정한다")
        void success() {
            // given
            String groupId = "test-group";
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
                .title("원본 제목")
                .content("원본 내용")
                .writerEmail(ownerEmail)
                .regDt(now)
                .build();
            fakeNoticeStoragePort.database.add(notice);

            Account account = Account.builder()
                .id(1L)
                .email(ownerEmail)
                .build();

            UpdateNoticeCommand command = UpdateNoticeCommand.builder()
                .groupId(groupId)
                .noticeId(1L)
                .account(account)
                .title("수정된 제목")
                .content("수정된 내용")
                .build();

            // when
            UpdateNoticeServiceResponse response = updateNoticeService.update(command);

            // then
            assertThat(response.result()).isTrue();
            Notice updatedNotice = fakeNoticeStoragePort.findById(1L);
            assertThat(updatedNotice.getTitle()).isEqualTo("수정된 제목");
            assertThat(updatedNotice.getContent()).isEqualTo("수정된 내용");
            assertThat(updatedNotice.getGroupId()).isEqualTo(groupId);
            assertThat(updatedNotice.getWriterEmail()).isEqualTo(ownerEmail);
        }

        @Test
        @DisplayName("[error] 그룹장이 아닌 사용자가 수정을 시도하면 예외가 발생한다")
        void error_notGroupOwner() {
            // given
            String groupId = "test-group";
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
                .title("원본 제목")
                .content("원본 내용")
                .writerEmail(ownerEmail)
                .regDt(now)
                .build();
            fakeNoticeStoragePort.database.add(notice);

            Account account = Account.builder()
                .id(2L)
                .email(memberEmail)
                .build();

            UpdateNoticeCommand command = UpdateNoticeCommand.builder()
                .groupId(groupId)
                .noticeId(1L)
                .account(account)
                .title("수정된 제목")
                .content("수정된 내용")
                .build();

            // when & then
            assertThrows(CustomBusinessException.class,
                () -> updateNoticeService.update(command));
            Notice originalNotice = fakeNoticeStoragePort.findById(1L);
            assertThat(originalNotice.getTitle()).isEqualTo("원본 제목");
            assertThat(originalNotice.getContent()).isEqualTo("원본 내용");
        }

        @Test
        @DisplayName("[error] 존재하지 않는 공지사항 ID인 경우 예외가 발생한다")
        void error_noticeNotFound() {
            // given
            String groupId = "test-group";
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

            UpdateNoticeCommand command = UpdateNoticeCommand.builder()
                .groupId(groupId)
                .noticeId(999L)
                .account(account)
                .title("수정된 제목")
                .content("수정된 내용")
                .build();

            // when & then
            assertThrows(CustomBusinessException.class,
                () -> updateNoticeService.update(command));
        }

        @Test
        @DisplayName("[error] 다른 그룹의 공지사항을 수정하려고 하면 예외가 발생한다")
        void error_noticeFromDifferentGroup() {
            // given
            String groupId = "test-group";
            String otherGroupId = "other-group";
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
                .content("원본 내용")
                .writerEmail("other@example.com")
                .regDt(now)
                .build();
            fakeNoticeStoragePort.database.add(notice);

            Account account = Account.builder()
                .id(1L)
                .email(ownerEmail)
                .build();

            UpdateNoticeCommand command = UpdateNoticeCommand.builder()
                .groupId(groupId)
                .noticeId(1L)
                .account(account)
                .title("수정된 제목")
                .content("수정된 내용")
                .build();

            // when & then
            assertThrows(CustomBusinessException.class,
                () -> updateNoticeService.update(command));
            Notice originalNotice = fakeNoticeStoragePort.findById(1L);
            assertThat(originalNotice.getTitle()).isEqualTo("다른 그룹 공지사항");
            assertThat(originalNotice.getContent()).isEqualTo("원본 내용");
        }

        @Test
        @DisplayName("[error] 존재하지 않는 그룹 ID인 경우 예외가 발생한다")
        void error_groupNotFound() {
            // given
            String nonExistentGroupId = "non-existent-group";
            String ownerEmail = "owner@example.com";

            Account account = Account.builder()
                .id(1L)
                .email(ownerEmail)
                .build();

            UpdateNoticeCommand command = UpdateNoticeCommand.builder()
                .groupId(nonExistentGroupId)
                .noticeId(1L)
                .account(account)
                .title("수정된 제목")
                .content("수정된 내용")
                .build();

            // when & then
            assertThrows(CustomBusinessException.class,
                () -> updateNoticeService.update(command));
        }
    }
}
