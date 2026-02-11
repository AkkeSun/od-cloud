package com.odcloud.application.group.service.delete_group;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static com.odcloud.infrastructure.constant.CommonConstant.DEFAULT_STORAGE_TOTAL;

import com.odcloud.application.group.port.in.command.DeleteGroupCommand;
import com.odcloud.domain.model.FileInfo;
import com.odcloud.domain.model.FolderInfo;
import com.odcloud.domain.model.Group;
import com.odcloud.domain.model.GroupAccount;
import com.odcloud.fakeClass.FakeFilePort;
import com.odcloud.fakeClass.FakeFileStoragePort;
import com.odcloud.fakeClass.FakeFolderStoragePort;
import com.odcloud.fakeClass.FakeGroupStoragePort;
import com.odcloud.fakeClass.FakeNoticeStoragePort;
import com.odcloud.fakeClass.FakeScheduleStoragePort;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class DeleteGroupServiceTest {

    private DeleteGroupService service;
    private FakeGroupStoragePort groupStoragePort;
    private FakeFolderStoragePort folderInfoStoragePort;
    private FakeFileStoragePort fileInfoStoragePort;
    private FakeFilePort filePort;
    private FakeScheduleStoragePort scheduleStoragePort;
    private FakeNoticeStoragePort noticeStoragePort;

    @BeforeEach
    void setUp() {
        groupStoragePort = new FakeGroupStoragePort();
        folderInfoStoragePort = new FakeFolderStoragePort();
        fileInfoStoragePort = new FakeFileStoragePort();
        filePort = new FakeFilePort();
        scheduleStoragePort = new FakeScheduleStoragePort();
        noticeStoragePort = new FakeNoticeStoragePort();
        service = new DeleteGroupService(
            groupStoragePort,
            folderInfoStoragePort,
            fileInfoStoragePort,
            filePort,
            scheduleStoragePort,
            noticeStoragePort
        );
    }

    @Nested
    @DisplayName("[delete] 그룹 삭제 메서드")
    class Describe_delete {

        @Test
        @DisplayName("[failure] 그룹 소유자가 아닌 사용자가 삭제 시도하면 Business_INVALID_GROUP_OWNER 예외 발생")
        void failure_notGroupOwner() {
            // given
            Group group = Group.builder()
                .id(1L)
                .name("Test Group")
                .ownerEmail("owner@example.com")
                .storageUsed(0L)
                .storageTotal(DEFAULT_STORAGE_TOTAL)
                .regDt(LocalDateTime.now())
                .build();
            groupStoragePort.save(group);

            DeleteGroupCommand command = DeleteGroupCommand.builder()
                .groupId(1L)
                .currentOwnerEmail("other@example.com")
                .build();

            // when & then
            assertThatThrownBy(() -> service.delete(command))
                .isInstanceOf(CustomBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.Business_INVALID_GROUP_OWNER);
        }

        @Test
        @DisplayName("[success] 그룹과 관련된 모든 데이터를 삭제한다 (파일, 폴더, GroupAccount, 그룹)")
        void success_deleteGroup() {
            // given
            Group group = Group.builder()
                .id(1L)
                .name("Test Group")
                .ownerEmail("owner@example.com")
                .storageUsed(0L)
                .storageTotal(DEFAULT_STORAGE_TOTAL)
                .regDt(LocalDateTime.now())
                .build();
            groupStoragePort.save(group);

            // 루트 폴더 생성
            FolderInfo rootFolder = FolderInfo.builder()
                .id(1L)
                .groupId(1L)
                .name("Test Group")
                .owner("owner@example.com")
                .parentId(null)
                .regDt(LocalDateTime.now())
                .build();
            folderInfoStoragePort.save(rootFolder);

            // 서브 폴더 생성
            FolderInfo subFolder = FolderInfo.builder()
                .id(2L)
                .groupId(1L)
                .name("Sub Folder")
                .owner("owner@example.com")
                .parentId(1L)
                .regDt(LocalDateTime.now())
                .build();
            folderInfoStoragePort.save(subFolder);

            // 파일 생성
            FileInfo file1 = FileInfo.builder()
                .id(1L)
                .groupId(1L)
                .folderId(1L)
                .fileName("file1.txt")
                .fileLoc("/group-1/file1.txt")
                .fileSize(1024L)
                .regDt(LocalDateTime.now())
                .build();
            fileInfoStoragePort.save(file1);

            FileInfo file2 = FileInfo.builder()
                .id(2L)
                .groupId(1L)
                .folderId(2L)
                .fileName("file2.txt")
                .fileLoc("/group-1/sub/file2.txt")
                .fileSize(2048L)
                .regDt(LocalDateTime.now())
                .build();
            fileInfoStoragePort.save(file2);

            // GroupAccount 생성
            GroupAccount groupAccount = GroupAccount.builder()
                .id(1L)
                .groupId(1L)
                .accountId(100L)
                .status("ACTIVE")
                .regDt(LocalDateTime.now())
                .build();
            groupStoragePort.save(groupAccount);

            DeleteGroupCommand command = DeleteGroupCommand.builder()
                .groupId(1L)
                .currentOwnerEmail("owner@example.com")
                .build();

            // when
            DeleteGroupServiceResponse response = service.delete(command);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();

            // 파일이 삭제되었는지 확인
            assertThat(fileInfoStoragePort.database).isEmpty();
            assertThat(filePort.deletedFiles).containsExactlyInAnyOrder(
                "/group-1/file1.txt",
                "/group-1/sub/file2.txt"
            );

            // 폴더가 삭제되었는지 확인
            assertThat(folderInfoStoragePort.database).isEmpty();

            // GroupAccount가 삭제되었는지 확인
            assertThat(groupStoragePort.groupAccountDatabase).isEmpty();

            // 그룹이 삭제되었는지 확인
            assertThat(groupStoragePort.groupDatabase).isEmpty();
        }

        @Test
        @DisplayName("[success] 파일이 없는 그룹을 삭제한다")
        void success_deleteGroupWithoutFiles() {
            // given
            Group group = Group.builder()
                .id(1L)
                .name("Test Group")
                .ownerEmail("owner@example.com")
                .storageUsed(0L)
                .storageTotal(DEFAULT_STORAGE_TOTAL)
                .regDt(LocalDateTime.now())
                .build();
            groupStoragePort.save(group);

            // 루트 폴더만 생성
            FolderInfo rootFolder = FolderInfo.builder()
                .id(1L)
                .groupId(1L)
                .name("Test Group")
                .owner("owner@example.com")
                .parentId(null)
                .regDt(LocalDateTime.now())
                .build();
            folderInfoStoragePort.save(rootFolder);

            DeleteGroupCommand command = DeleteGroupCommand.builder()
                .groupId(1L)
                .currentOwnerEmail("owner@example.com")
                .build();

            // when
            DeleteGroupServiceResponse response = service.delete(command);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();

            // 폴더가 삭제되었는지 확인
            assertThat(folderInfoStoragePort.database).isEmpty();

            // 그룹이 삭제되었는지 확인
            assertThat(groupStoragePort.groupDatabase).isEmpty();
        }

        @Test
        @DisplayName("[success] GroupAccount가 없는 그룹을 삭제한다")
        void success_deleteGroupWithoutGroupAccounts() {
            // given
            Group group = Group.builder()
                .id(1L)
                .name("Test Group")
                .ownerEmail("owner@example.com")
                .storageUsed(0L)
                .storageTotal(DEFAULT_STORAGE_TOTAL)
                .regDt(LocalDateTime.now())
                .build();
            groupStoragePort.save(group);

            // 루트 폴더만 생성
            FolderInfo rootFolder = FolderInfo.builder()
                .id(1L)
                .groupId(1L)
                .name("Test Group")
                .owner("owner@example.com")
                .parentId(null)
                .regDt(LocalDateTime.now())
                .build();
            folderInfoStoragePort.save(rootFolder);

            DeleteGroupCommand command = DeleteGroupCommand.builder()
                .groupId(1L)
                .currentOwnerEmail("owner@example.com")
                .build();

            // when
            DeleteGroupServiceResponse response = service.delete(command);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();

            // GroupAccount가 비어있는지 확인
            assertThat(groupStoragePort.groupAccountDatabase).isEmpty();

            // 그룹이 삭제되었는지 확인
            assertThat(groupStoragePort.groupDatabase).isEmpty();
        }
    }
}
