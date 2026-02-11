package com.odcloud.application.account.service.delete_account;

import static org.assertj.core.api.Assertions.assertThat;
import static com.odcloud.infrastructure.constant.CommonConstant.DEFAULT_STORAGE_TOTAL;

import com.odcloud.application.group.port.in.DeleteGroupUseCase;
import com.odcloud.application.group.port.in.command.DeleteGroupCommand;
import com.odcloud.application.group.service.delete_group.DeleteGroupServiceResponse;
import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.AccountDevice;
import com.odcloud.domain.model.FileInfo;
import com.odcloud.domain.model.FolderInfo;
import com.odcloud.domain.model.Group;
import com.odcloud.domain.model.GroupAccount;
import com.odcloud.domain.model.Notice;
import com.odcloud.domain.model.Schedule;
import com.odcloud.fakeClass.FakeAccountDeviceStoragePort;
import com.odcloud.fakeClass.FakeAccountStoragePort;
import com.odcloud.fakeClass.FakeFilePort;
import com.odcloud.fakeClass.FakeFileStoragePort;
import com.odcloud.fakeClass.FakeFolderStoragePort;
import com.odcloud.fakeClass.FakeGroupStoragePort;
import com.odcloud.fakeClass.FakeNoticeStoragePort;
import com.odcloud.fakeClass.FakeScheduleStoragePort;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class DeleteAccountServiceTest {

    private DeleteAccountService service;
    private FakeAccountStoragePort accountStoragePort;
    private FakeAccountDeviceStoragePort accountDeviceStoragePort;
    private FakeScheduleStoragePort scheduleStoragePort;
    private FakeGroupStoragePort groupStoragePort;
    private FakeFolderStoragePort folderInfoStoragePort;
    private FakeFileStoragePort fileInfoStoragePort;
    private FakeFilePort filePort;
    private FakeNoticeStoragePort noticeStoragePort;

    @BeforeEach
    void setUp() {
        accountStoragePort = new FakeAccountStoragePort();
        accountDeviceStoragePort = new FakeAccountDeviceStoragePort();
        scheduleStoragePort = new FakeScheduleStoragePort();
        groupStoragePort = new FakeGroupStoragePort();
        folderInfoStoragePort = new FakeFolderStoragePort();
        fileInfoStoragePort = new FakeFileStoragePort();
        filePort = new FakeFilePort();
        noticeStoragePort = new FakeNoticeStoragePort();

        DeleteGroupUseCase deleteGroupUseCase = new FakeDeleteGroupUseCase(
            groupStoragePort,
            folderInfoStoragePort,
            fileInfoStoragePort,
            filePort,
            scheduleStoragePort,
            noticeStoragePort
        );

        service = new DeleteAccountService(
            accountStoragePort,
            accountDeviceStoragePort,
            scheduleStoragePort,
            groupStoragePort,
            deleteGroupUseCase
        );
    }

    // Fake implementation of DeleteGroupUseCase for testing
    private static class FakeDeleteGroupUseCase implements DeleteGroupUseCase {
        private final FakeGroupStoragePort groupStoragePort;
        private final FakeFolderStoragePort folderInfoStoragePort;
        private final FakeFileStoragePort fileInfoStoragePort;
        private final FakeFilePort filePort;
        private final FakeScheduleStoragePort scheduleStoragePort;
        private final FakeNoticeStoragePort noticeStoragePort;

        public FakeDeleteGroupUseCase(
            FakeGroupStoragePort groupStoragePort,
            FakeFolderStoragePort folderInfoStoragePort,
            FakeFileStoragePort fileInfoStoragePort,
            FakeFilePort filePort,
            FakeScheduleStoragePort scheduleStoragePort,
            FakeNoticeStoragePort noticeStoragePort
        ) {
            this.groupStoragePort = groupStoragePort;
            this.folderInfoStoragePort = folderInfoStoragePort;
            this.fileInfoStoragePort = fileInfoStoragePort;
            this.filePort = filePort;
            this.scheduleStoragePort = scheduleStoragePort;
            this.noticeStoragePort = noticeStoragePort;
        }

        @Override
        public DeleteGroupServiceResponse delete(DeleteGroupCommand command) {
            Group group = groupStoragePort.findById(command.groupId());

            List<FolderInfo> folders = folderInfoStoragePort.findByGroupId(command.groupId());
            for (FolderInfo folder : folders) {
                List<FileInfo> files = fileInfoStoragePort.findByFolderId(folder.getId());
                for (FileInfo file : files) {
                    filePort.deleteFile(file.getFileLoc());
                    fileInfoStoragePort.delete(file);
                }
                folderInfoStoragePort.delete(folder);
            }

            List<Schedule> schedules = scheduleStoragePort.findByGroupId(command.groupId());
            for (Schedule schedule : schedules) {
                scheduleStoragePort.delete(schedule);
            }

            List<Notice> notices = noticeStoragePort.findByGroupId(command.groupId(), Integer.MAX_VALUE);
            for (Notice notice : notices) {
                noticeStoragePort.delete(notice);
            }

            groupStoragePort.deleteGroupAccountsByGroupId(command.groupId());
            groupStoragePort.delete(group);

            return DeleteGroupServiceResponse.ofSuccess();
        }
    }

    @Nested
    @DisplayName("[delete] 계정 삭제 메서드")
    class Describe_delete {

        @Test
        @DisplayName("[success] 계정과 관련된 모든 데이터를 삭제한다")
        void success_deleteAccount() {
            // given
            Account account = Account.builder()
                .id(1L)
                .email("test@example.com")
                .nickname("test")
                .name("Test User")
                .picture("picture.jpg")
                .regDt(LocalDateTime.now())
                .build();
            accountStoragePort.save(account);

            // Create devices
            AccountDevice device1 = AccountDevice.builder()
                .id(1L)
                .accountId(account.getId())
                .osType("ANDROID")
                .deviceId("device-1")
                .fcmToken("token-1")
                .pushYn("Y")
                .regDt(LocalDateTime.now())
                .build();
            accountDeviceStoragePort.save(device1);

            AccountDevice device2 = AccountDevice.builder()
                .id(2L)
                .accountId(account.getId())
                .osType("IOS")
                .deviceId("device-2")
                .fcmToken("token-2")
                .pushYn("Y")
                .regDt(LocalDateTime.now())
                .build();
            accountDeviceStoragePort.save(device2);

            // Create personal schedules
            Schedule personalSchedule = Schedule.builder()
                .id(1L)
                .writerEmail(account.getEmail())
                .groupId(null)
                .content("Personal schedule")
                .startDt(LocalDateTime.now().plusDays(1))
                .notificationYn("N")
                .regDt(LocalDateTime.now())
                .build();
            scheduleStoragePort.save(personalSchedule);

            // Create owned group
            Group group = Group.builder()
                .id(1L)
                .name("Test Group")
                .ownerEmail(account.getEmail())
                .storageUsed(0L)
                .storageTotal(DEFAULT_STORAGE_TOTAL)
                .regDt(LocalDateTime.now())
                .build();
            groupStoragePort.save(group);

            // Create GroupAccount for the owner
            GroupAccount groupAccount = GroupAccount.builder()
                .id(1L)
                .groupId(group.getId())
                .accountId(account.getId())
                .groupName(group.getName())
                .groupOwner(group.getOwnerEmail())
                .email(account.getEmail())
                .status("ACTIVE")
                .showYn("Y")
                .regDt(LocalDateTime.now())
                .build();
            groupStoragePort.save(groupAccount);

            // Create folder for group
            FolderInfo folder = FolderInfo.builder()
                .id(1L)
                .groupId(group.getId())
                .name("Test Folder")
                .owner(account.getEmail())
                .regDt(LocalDateTime.now())
                .build();
            folderInfoStoragePort.save(folder);

            // Create file in folder
            FileInfo file = FileInfo.builder()
                .id(1L)
                .folderId(folder.getId())
                .fileName("test.txt")
                .fileLoc("/group-1/test.txt")
                .fileSize(1024L)
                .regDt(LocalDateTime.now())
                .build();
            fileInfoStoragePort.save(file);

            // Create group schedule
            Schedule groupSchedule = Schedule.builder()
                .id(2L)
                .writerEmail(account.getEmail())
                .groupId(group.getId())
                .content("Group schedule")
                .startDt(LocalDateTime.now().plusDays(1))
                .notificationYn("N")
                .regDt(LocalDateTime.now())
                .build();
            scheduleStoragePort.save(groupSchedule);

            // when
            DeleteAccountServiceResponse response = service.delete(account);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();

            // Verify devices are deleted
            assertThat(accountDeviceStoragePort.database).isEmpty();

            // Verify personal schedules are deleted
            assertThat(scheduleStoragePort.database).isEmpty();

            // Verify files are deleted
            assertThat(fileInfoStoragePort.database).isEmpty();
            assertThat(filePort.deletedFiles).containsExactly("/group-1/test.txt");

            // Verify folders are deleted
            assertThat(folderInfoStoragePort.database).isEmpty();

            // Verify group is deleted
            assertThat(groupStoragePort.groupDatabase).isEmpty();

            // Verify account is deleted
            assertThat(accountStoragePort.database).isEmpty();
        }

        @Test
        @DisplayName("[success] 여러 그룹을 소유한 계정을 삭제한다")
        void success_deleteAccountWithMultipleGroups() {
            // given
            Account account = Account.builder()
                .id(1L)
                .email("test@example.com")
                .nickname("test")
                .name("Test User")
                .regDt(LocalDateTime.now())
                .build();
            accountStoragePort.save(account);

            // Create two groups
            Group group1 = Group.builder()
                .id(1L)
                .name("Group 1")
                .ownerEmail(account.getEmail())
                .storageUsed(0L)
                .storageTotal(DEFAULT_STORAGE_TOTAL)
                .regDt(LocalDateTime.now())
                .build();
            groupStoragePort.save(group1);

            // Create GroupAccount for group1
            GroupAccount groupAccount1 = GroupAccount.builder()
                .id(1L)
                .groupId(group1.getId())
                .accountId(account.getId())
                .groupName(group1.getName())
                .groupOwner(group1.getOwnerEmail())
                .email(account.getEmail())
                .status("ACTIVE")
                .showYn("Y")
                .regDt(LocalDateTime.now())
                .build();
            groupStoragePort.save(groupAccount1);

            Group group2 = Group.builder()
                .id(2L)
                .name("Group 2")
                .ownerEmail(account.getEmail())
                .storageUsed(0L)
                .storageTotal(DEFAULT_STORAGE_TOTAL)
                .regDt(LocalDateTime.now())
                .build();
            groupStoragePort.save(group2);

            // Create GroupAccount for group2
            GroupAccount groupAccount2 = GroupAccount.builder()
                .id(2L)
                .groupId(group2.getId())
                .accountId(account.getId())
                .groupName(group2.getName())
                .groupOwner(group2.getOwnerEmail())
                .email(account.getEmail())
                .status("ACTIVE")
                .showYn("Y")
                .regDt(LocalDateTime.now())
                .build();
            groupStoragePort.save(groupAccount2);

            // Create folders for each group
            FolderInfo folder1 = FolderInfo.builder()
                .id(1L)
                .groupId(group1.getId())
                .name("Folder 1")
                .regDt(LocalDateTime.now())
                .build();
            folderInfoStoragePort.save(folder1);

            FolderInfo folder2 = FolderInfo.builder()
                .id(2L)
                .groupId(group2.getId())
                .name("Folder 2")
                .regDt(LocalDateTime.now())
                .build();
            folderInfoStoragePort.save(folder2);

            // when
            DeleteAccountServiceResponse response = service.delete(account);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();

            // Verify both groups are deleted
            assertThat(groupStoragePort.groupDatabase).isEmpty();

            // Verify all folders are deleted
            assertThat(folderInfoStoragePort.database).isEmpty();

            // Verify account is deleted
            assertThat(accountStoragePort.database).isEmpty();
        }

        @Test
        @DisplayName("[success] 디바이스가 없는 계정을 삭제한다")
        void success_deleteAccountWithoutDevices() {
            // given
            Account account = Account.builder()
                .id(1L)
                .email("test@example.com")
                .nickname("test")
                .name("Test User")
                .regDt(LocalDateTime.now())
                .build();
            accountStoragePort.save(account);

            // when
            DeleteAccountServiceResponse response = service.delete(account);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();

            // Verify account is deleted
            assertThat(accountStoragePort.database).isEmpty();
        }

        @Test
        @DisplayName("[success] 개인 스케줄이 없는 계정을 삭제한다")
        void success_deleteAccountWithoutPersonalSchedules() {
            // given
            Account account = Account.builder()
                .id(1L)
                .email("test@example.com")
                .nickname("test")
                .name("Test User")
                .regDt(LocalDateTime.now())
                .build();
            accountStoragePort.save(account);

            // when
            DeleteAccountServiceResponse response = service.delete(account);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();

            // Verify account is deleted
            assertThat(accountStoragePort.database).isEmpty();
        }

        @Test
        @DisplayName("[success] 소유 그룹이 없는 계정을 삭제한다")
        void success_deleteAccountWithoutOwnedGroups() {
            // given
            Account account = Account.builder()
                .id(1L)
                .email("test@example.com")
                .nickname("test")
                .name("Test User")
                .regDt(LocalDateTime.now())
                .build();
            accountStoragePort.save(account);

            // when
            DeleteAccountServiceResponse response = service.delete(account);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();

            // Verify account is deleted
            assertThat(accountStoragePort.database).isEmpty();
        }
    }
}
