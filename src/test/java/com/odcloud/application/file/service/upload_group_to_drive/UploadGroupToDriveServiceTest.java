package com.odcloud.application.file.service.upload_group_to_drive;

import static com.odcloud.fakeClass.FakeGoogleDrivePort.FIXED_FOLDER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.odcloud.domain.model.FileInfo;
import com.odcloud.domain.model.FolderInfo;
import com.odcloud.domain.model.Group;
import com.odcloud.fakeClass.FakeFilePort;
import com.odcloud.fakeClass.FakeFileStoragePort;
import com.odcloud.fakeClass.FakeFolderStoragePort;
import com.odcloud.fakeClass.FakeGoogleDrivePort;
import com.odcloud.fakeClass.FakeGroupStoragePort;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UploadGroupToDriveServiceTest {

    private FakeGroupStoragePort fakeGroupStoragePort;
    private FakeFileStoragePort fakeFileStoragePort;
    private FakeFolderStoragePort fakeFolderStoragePort;
    private FakeFilePort fakeFilePort;
    private FakeGoogleDrivePort fakeGoogleDrivePort;
    private UploadGroupToDriveService service;

    @BeforeEach
    void setUp() {
        fakeGroupStoragePort = new FakeGroupStoragePort();
        fakeFileStoragePort = new FakeFileStoragePort();
        fakeFolderStoragePort = new FakeFolderStoragePort();
        fakeFilePort = new FakeFilePort();
        fakeGoogleDrivePort = new FakeGoogleDrivePort();
        service = new UploadGroupToDriveService(
            fakeGroupStoragePort,
            fakeFileStoragePort,
            fakeFolderStoragePort,
            fakeFilePort,
            fakeGoogleDrivePort
        );
    }

    @Nested
    @DisplayName("[upload] 그룹 파일 Drive 전체 업로드")
    class Describe_upload {

        @Test
        @DisplayName("[success] 그룹의 파일이 Drive에 정상 업로드된다")
        void success_uploadFiles() {
            Group group = Group.builder()
                .id(1L).name("테스트그룹").driveFolderId(FIXED_FOLDER_ID).build();
            fakeGroupStoragePort.groupDatabase.add(group);

            fakeFileStoragePort.database.add(FileInfo.builder()
                .id(10L).groupId(1L).folderId(null)
                .fileName("doc.pdf").fileLoc("/disk1/1_aaa_20240101.pdf")
                .fileSize(1024L).regDt(LocalDateTime.now()).build());

            UploadGroupToDriveResponse response = service.upload(1L);

            assertThat(response.totalFiles()).isEqualTo(1);
            assertThat(response.uploadedCount()).isEqualTo(1);
            assertThat(response.skippedCount()).isEqualTo(0);
            assertThat(response.failedCount()).isEqualTo(0);
            assertThat(fakeGoogleDrivePort.uploadFileCallCount).isEqualTo(1);
            assertThat(fakeGoogleDrivePort.uploadedFileNames).containsExactly("doc.pdf");
        }

        @Test
        @DisplayName("[success] Drive에 이미 존재하는 파일은 건너뛴다")
        void success_skipExistingFiles() {
            Group group = Group.builder()
                .id(1L).name("테스트그룹").driveFolderId(FIXED_FOLDER_ID).build();
            fakeGroupStoragePort.groupDatabase.add(group);

            fakeFileStoragePort.database.add(FileInfo.builder()
                .id(10L).groupId(1L).folderId(null)
                .fileName("exists.pdf").fileLoc("/disk1/1_aaa_20240101.pdf")
                .fileSize(1024L).regDt(LocalDateTime.now()).build());
            fakeFileStoragePort.database.add(FileInfo.builder()
                .id(11L).groupId(1L).folderId(null)
                .fileName("new.pdf").fileLoc("/disk1/1_bbb_20240101.pdf")
                .fileSize(512L).regDt(LocalDateTime.now()).build());

            fakeGoogleDrivePort.addPreExistingFile(FIXED_FOLDER_ID, "exists.pdf");

            UploadGroupToDriveResponse response = service.upload(1L);

            assertThat(response.totalFiles()).isEqualTo(2);
            assertThat(response.uploadedCount()).isEqualTo(1);
            assertThat(response.skippedCount()).isEqualTo(1);
            assertThat(response.failedCount()).isEqualTo(0);
            assertThat(fakeGoogleDrivePort.uploadedFileNames).containsExactly("new.pdf");
        }

        @Test
        @DisplayName("[success] 그룹에 파일이 없으면 카운트가 모두 0이다")
        void success_noFiles() {
            Group group = Group.builder()
                .id(1L).name("빈그룹").driveFolderId(FIXED_FOLDER_ID).build();
            fakeGroupStoragePort.groupDatabase.add(group);

            UploadGroupToDriveResponse response = service.upload(1L);

            assertThat(response.totalFiles()).isEqualTo(0);
            assertThat(response.uploadedCount()).isEqualTo(0);
            assertThat(response.skippedCount()).isEqualTo(0);
            assertThat(response.failedCount()).isEqualTo(0);
            assertThat(fakeGoogleDrivePort.uploadFileCallCount).isEqualTo(0);
        }

        @Test
        @DisplayName("[success] driveFolderId가 없는 그룹은 ensureFolder를 호출하고 저장한다")
        void success_ensureFolderWhenDriveFolderIdIsNull() {
            Group group = Group.builder()
                .id(2L).name("폴더없는그룹").driveFolderId(null).build();
            fakeGroupStoragePort.groupDatabase.add(group);

            fakeFileStoragePort.database.add(FileInfo.builder()
                .id(20L).groupId(2L).folderId(null)
                .fileName("file.txt").fileLoc("/disk1/2_xyz_20240102.txt")
                .fileSize(512L).regDt(LocalDateTime.now()).build());

            service.upload(2L);

            assertThat(fakeGoogleDrivePort.ensureFolderCallCount).isEqualTo(1);
            assertThat(fakeGroupStoragePort.groupDatabase.stream()
                .filter(g -> g.getId().equals(2L))
                .findFirst().get().getDriveFolderId())
                .isEqualTo(FIXED_FOLDER_ID);
            assertThat(fakeGoogleDrivePort.uploadFileCallCount).isEqualTo(1);
        }

        @Test
        @DisplayName("[success] 서브폴더에 속한 파일은 Drive 계층을 그대로 재현하여 업로드한다")
        void success_uploadToSubFolder() {
            Group group = Group.builder()
                .id(1L).name("계층그룹").driveFolderId(FIXED_FOLDER_ID).build();
            fakeGroupStoragePort.groupDatabase.add(group);

            fakeFolderStoragePort.database.add(FolderInfo.builder()
                .id(100L).groupId(1L).name("문서폴더").parentId(null)
                .regDt(LocalDateTime.now()).build());

            fakeFileStoragePort.database.add(FileInfo.builder()
                .id(10L).groupId(1L).folderId(100L)
                .fileName("doc.pdf").fileLoc("/disk1/1_aaa_20240101.pdf")
                .fileSize(1024L).regDt(LocalDateTime.now()).build());

            UploadGroupToDriveResponse response = service.upload(1L);

            assertThat(response.uploadedCount()).isEqualTo(1);
            assertThat(fakeGoogleDrivePort.ensureSubFolderCallCount).isEqualTo(1);
            assertThat(fakeGoogleDrivePort.uploadedFolderIds.get(0))
                .isEqualTo("fake-sub-folder-id-문서폴더");
        }

        @Test
        @DisplayName("[success] 같은 folderId를 가진 파일이 여러 개면 서브폴더 생성은 한 번만 호출된다")
        void success_subFolderCachePreventsDuplicateCreation() {
            Group group = Group.builder()
                .id(1L).name("캐시그룹").driveFolderId(FIXED_FOLDER_ID).build();
            fakeGroupStoragePort.groupDatabase.add(group);

            fakeFolderStoragePort.database.add(FolderInfo.builder()
                .id(200L).groupId(1L).name("공유폴더").parentId(null)
                .regDt(LocalDateTime.now()).build());

            fakeFileStoragePort.database.add(FileInfo.builder()
                .id(10L).groupId(1L).folderId(200L)
                .fileName("file1.txt").fileLoc("/disk1/1_aa1_20240101.txt")
                .fileSize(100L).regDt(LocalDateTime.now()).build());
            fakeFileStoragePort.database.add(FileInfo.builder()
                .id(11L).groupId(1L).folderId(200L)
                .fileName("file2.txt").fileLoc("/disk1/1_aa2_20240101.txt")
                .fileSize(200L).regDt(LocalDateTime.now()).build());

            service.upload(1L);

            assertThat(fakeGoogleDrivePort.ensureSubFolderCallCount).isEqualTo(1);
            assertThat(fakeGoogleDrivePort.uploadFileCallCount).isEqualTo(2);
        }

        @Test
        @DisplayName("[success] 중첩 폴더 구조가 Drive 계층으로 그대로 재현된다")
        void success_nestedFolderHierarchyReproducedInDrive() {
            Group group = Group.builder()
                .id(1L).name("계층그룹").driveFolderId(FIXED_FOLDER_ID).build();
            fakeGroupStoragePort.groupDatabase.add(group);

            fakeFolderStoragePort.database.add(FolderInfo.builder()
                .id(10L).groupId(1L).name("루트").parentId(null)
                .regDt(LocalDateTime.now()).build());
            fakeFolderStoragePort.database.add(FolderInfo.builder()
                .id(11L).groupId(1L).name("문서").parentId(10L)
                .regDt(LocalDateTime.now()).build());
            fakeFolderStoragePort.database.add(FolderInfo.builder()
                .id(12L).groupId(1L).name("계약서").parentId(11L)
                .regDt(LocalDateTime.now()).build());

            fakeFileStoragePort.database.add(FileInfo.builder()
                .id(10L).groupId(1L).folderId(12L)
                .fileName("contract.pdf").fileLoc("/disk1/1_ccc_20240101.pdf")
                .fileSize(1024L).regDt(LocalDateTime.now()).build());

            UploadGroupToDriveResponse response = service.upload(1L);

            assertThat(response.uploadedCount()).isEqualTo(1);
            assertThat(fakeGoogleDrivePort.ensureSubFolderCallCount).isEqualTo(3);
            assertThat(fakeGoogleDrivePort.uploadedFolderIds.get(0))
                .isEqualTo("fake-sub-folder-id-계약서");
        }

        @Test
        @DisplayName("[success] 파일 읽기 실패 시 해당 파일을 skip하고 나머지는 계속 처리한다")
        void success_fileReadFailureContinuesOthers() {
            Group group = Group.builder()
                .id(1L).name("테스트그룹").driveFolderId(FIXED_FOLDER_ID).build();
            fakeGroupStoragePort.groupDatabase.add(group);

            String failPath = "/disk1/missing.txt";
            fakeFileStoragePort.database.add(FileInfo.builder()
                .id(10L).groupId(1L).folderId(null)
                .fileName("fail.txt").fileLoc(failPath)
                .fileSize(100L).regDt(LocalDateTime.now()).build());
            fakeFileStoragePort.database.add(FileInfo.builder()
                .id(11L).groupId(1L).folderId(null)
                .fileName("ok.txt").fileLoc("/disk1/1_bbb_20240101.txt")
                .fileSize(200L).regDt(LocalDateTime.now()).build());

            fakeFilePort.addFailPath(failPath);

            UploadGroupToDriveResponse response = service.upload(1L);

            assertThat(response.totalFiles()).isEqualTo(2);
            assertThat(response.uploadedCount()).isEqualTo(1);
            assertThat(response.failedCount()).isEqualTo(1);
            assertThat(fakeGoogleDrivePort.uploadedFileNames).containsExactly("ok.txt");
        }

        @Test
        @DisplayName("[success] Drive 서브폴더 생성 실패 시 해당 파일을 skip하고 failedCount가 증가한다")
        void success_ensureSubFolderFailureIncrementsFailedCount() {
            Group group = Group.builder()
                .id(1L).name("테스트그룹").driveFolderId(FIXED_FOLDER_ID).build();
            fakeGroupStoragePort.groupDatabase.add(group);

            fakeFolderStoragePort.database.add(FolderInfo.builder()
                .id(100L).groupId(1L).name("실패폴더").parentId(null)
                .regDt(LocalDateTime.now()).build());

            fakeFileStoragePort.database.add(FileInfo.builder()
                .id(10L).groupId(1L).folderId(100L)
                .fileName("file.txt").fileLoc("/disk1/1_aaa_20240101.txt")
                .fileSize(100L).regDt(LocalDateTime.now()).build());

            fakeGoogleDrivePort.shouldThrowEnsureSubFolder = true;

            UploadGroupToDriveResponse response = service.upload(1L);

            assertThat(response.failedCount()).isEqualTo(1);
            assertThat(fakeGoogleDrivePort.uploadFileCallCount).isEqualTo(0);
        }

        @Test
        @DisplayName("[error] 존재하지 않는 그룹 ID로 요청 시 예외가 발생한다")
        void error_groupNotFound() {
            assertThatThrownBy(() -> service.upload(999L))
                .isInstanceOf(CustomBusinessException.class)
                .hasMessageContaining(ErrorCode.Business_DoesNotExists_GROUP.getMessage());
        }

        @Test
        @DisplayName("[error] Drive 그룹 폴더 생성 실패 시 예외가 전파된다")
        void error_ensureFolderFailurePropagates() {
            Group group = Group.builder()
                .id(1L).name("폴더실패그룹").driveFolderId(null).build();
            fakeGroupStoragePort.groupDatabase.add(group);

            fakeFileStoragePort.database.add(FileInfo.builder()
                .id(10L).groupId(1L).folderId(null)
                .fileName("file.txt").fileLoc("/disk1/1_aaa_20240101.txt")
                .fileSize(100L).regDt(LocalDateTime.now()).build());

            fakeGoogleDrivePort.shouldThrowEnsureFolder = true;

            assertThatThrownBy(() -> service.upload(1L))
                .isInstanceOf(CustomBusinessException.class)
                .hasMessageContaining(ErrorCode.Business_GOOGLE_DRIVE_ENSURE_FOLDER_ERROR.getMessage());
        }
    }
}
