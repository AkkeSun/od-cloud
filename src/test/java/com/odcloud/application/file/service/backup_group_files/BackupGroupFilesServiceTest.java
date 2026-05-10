package com.odcloud.application.file.service.backup_group_files;

import static com.odcloud.fakeClass.FakeGoogleDrivePort.FIXED_FOLDER_ID;
import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.domain.model.FileHistory;
import com.odcloud.domain.model.FileHistoryActionType;
import com.odcloud.domain.model.FolderInfo;
import com.odcloud.domain.model.Group;
import com.odcloud.fakeClass.FakeFileHistoryStoragePort;
import com.odcloud.fakeClass.FakeFilePort;
import com.odcloud.fakeClass.FakeFolderStoragePort;
import com.odcloud.fakeClass.FakeGoogleDrivePort;
import com.odcloud.fakeClass.FakeGroupStoragePort;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class BackupGroupFilesServiceTest {

    private FakeGroupStoragePort fakeGroupStoragePort;
    private FakeFileHistoryStoragePort fakeFileHistoryStoragePort;
    private FakeFilePort fakeFilePort;
    private FakeGoogleDrivePort fakeGoogleDrivePort;
    private FakeFolderStoragePort fakeFolderStoragePort;
    private BackupGroupFilesService backupGroupFilesService;

    @BeforeEach
    void setUp() {
        fakeGroupStoragePort = new FakeGroupStoragePort();
        fakeFileHistoryStoragePort = new FakeFileHistoryStoragePort();
        fakeFilePort = new FakeFilePort();
        fakeGoogleDrivePort = new FakeGoogleDrivePort();
        fakeFolderStoragePort = new FakeFolderStoragePort();
        backupGroupFilesService = new BackupGroupFilesService(
            fakeGroupStoragePort,
            fakeFileHistoryStoragePort,
            fakeFilePort,
            fakeGoogleDrivePort,
            fakeFolderStoragePort
        );
    }

    @Nested
    @DisplayName("[backup] 그룹별 Drive 증분 백업")
    class Describe_backup {

        @Test
        @DisplayName("[success] 미백업 이력이 있는 그룹은 Drive에 업로드하고 backupDt를 갱신한다")
        void success_uploadAndUpdateBackupDt() {
            Group group = Group.builder()
                .id(1L).name("테스트그룹").driveFolderId(FIXED_FOLDER_ID).backupYn("Y").build();
            fakeGroupStoragePort.groupDatabase.add(group);

            FileHistory history = FileHistory.builder()
                .id(10L).groupId(1L).actionType(FileHistoryActionType.UPLOAD)
                .afterFileName("test.txt").fileLoc("/disk1/1_abc_20240101.txt")
                .fileSize(1024L).backupDt(null).regDt(LocalDateTime.now()).build();
            fakeFileHistoryStoragePort.database.add(history);

            BackupGroupFilesResponse response = backupGroupFilesService.backup();

            assertThat(response.totalGroups()).isEqualTo(1);
            assertThat(response.successCount()).isEqualTo(1);
            assertThat(response.failCount()).isEqualTo(0);
            assertThat(response.skipCount()).isEqualTo(0);
            assertThat(fakeGoogleDrivePort.uploadFileCallCount).isEqualTo(1);
            assertThat(fakeFileHistoryStoragePort.updatedBackupDtMap).containsKey(10L);
        }

        @Test
        @DisplayName("[success] 미백업 이력이 없는 그룹은 skip 처리된다")
        void success_skipGroupWithNoHistory() {
            Group group = Group.builder()
                .id(1L).name("히스토리없는그룹").driveFolderId(FIXED_FOLDER_ID).backupYn("Y").build();
            fakeGroupStoragePort.groupDatabase.add(group);

            BackupGroupFilesResponse response = backupGroupFilesService.backup();

            assertThat(response.totalGroups()).isEqualTo(1);
            assertThat(response.successCount()).isEqualTo(0);
            assertThat(response.skipCount()).isEqualTo(1);
            assertThat(fakeGoogleDrivePort.uploadFileCallCount).isEqualTo(0);
        }

        @Test
        @DisplayName("[success] DELETE 타입 이력은 Drive 파일을 삭제하고 backupDt를 갱신한다")
        void success_deleteHistoryDeletesDriveFile() {
            Group group = Group.builder()
                .id(1L).name("삭제이력그룹").driveFolderId(FIXED_FOLDER_ID).backupYn("Y").build();
            fakeGroupStoragePort.groupDatabase.add(group);

            FolderInfo folder = FolderInfo.builder()
                .id(50L).groupId(1L).name("문서폴더").parentId(null)
                .regDt(LocalDateTime.now()).build();
            fakeFolderStoragePort.database.add(folder);

            FileHistory deleteHistory = FileHistory.builder()
                .id(20L).groupId(1L).actionType(FileHistoryActionType.DELETE)
                .beforeFileName("deleted-file.txt").beforeFolderId(50L)
                .fileSize(512L).backupDt(null).regDt(LocalDateTime.now()).build();
            fakeFileHistoryStoragePort.database.add(deleteHistory);

            BackupGroupFilesResponse response = backupGroupFilesService.backup();

            assertThat(response.successCount()).isEqualTo(1);
            assertThat(fakeGoogleDrivePort.uploadFileCallCount).isEqualTo(0);
            assertThat(fakeGoogleDrivePort.deleteFileCallCount).isEqualTo(1);
            assertThat(fakeGoogleDrivePort.deletedFileNames).containsExactly("deleted-file.txt");
            assertThat(fakeFileHistoryStoragePort.updatedBackupDtMap).containsKey(20L);
        }

        @Test
        @DisplayName("[success] MOVE 타입 이력은 새 폴더에 업로드하고 이전 폴더 Drive 파일을 삭제한다")
        void success_moveHistoryUploadsToNewFolderAndDeletesFromOld() {
            Group group = Group.builder()
                .id(1L).name("이동이력그룹").driveFolderId(FIXED_FOLDER_ID).backupYn("Y").build();
            fakeGroupStoragePort.groupDatabase.add(group);

            FolderInfo beforeFolder = FolderInfo.builder()
                .id(10L).groupId(1L).name("문서").parentId(null)
                .regDt(LocalDateTime.now()).build();
            FolderInfo afterFolder = FolderInfo.builder()
                .id(20L).groupId(1L).name("계약").parentId(null)
                .regDt(LocalDateTime.now()).build();
            fakeFolderStoragePort.database.add(beforeFolder);
            fakeFolderStoragePort.database.add(afterFolder);

            FileHistory moveHistory = FileHistory.builder()
                .id(30L).groupId(1L).actionType(FileHistoryActionType.MOVE)
                .beforeFileName("report.pdf").beforeFolderId(10L)
                .afterFileName("report.pdf").afterFolderId(20L)
                .fileLoc("/disk1/1_abc_20240101.pdf")
                .fileSize(1024L).backupDt(null).regDt(LocalDateTime.now()).build();
            fakeFileHistoryStoragePort.database.add(moveHistory);

            BackupGroupFilesResponse response = backupGroupFilesService.backup();

            assertThat(response.successCount()).isEqualTo(1);
            assertThat(fakeGoogleDrivePort.uploadFileCallCount).isEqualTo(1);
            assertThat(fakeGoogleDrivePort.deleteFileCallCount).isEqualTo(1);
            assertThat(fakeGoogleDrivePort.deletedFileNames).containsExactly("report.pdf");
            assertThat(fakeFileHistoryStoragePort.updatedBackupDtMap).containsKey(30L);
        }

        @Test
        @DisplayName("[success] RENAME 타입 이력은 새 이름으로 업로드하고 이전 이름 Drive 파일을 삭제한다")
        void success_renameHistoryUploadsWithNewNameAndDeletesOldName() {
            Group group = Group.builder()
                .id(1L).name("이름변경그룹").driveFolderId(FIXED_FOLDER_ID).backupYn("Y").build();
            fakeGroupStoragePort.groupDatabase.add(group);

            FolderInfo folder = FolderInfo.builder()
                .id(10L).groupId(1L).name("문서").parentId(null)
                .regDt(LocalDateTime.now()).build();
            fakeFolderStoragePort.database.add(folder);

            FileHistory renameHistory = FileHistory.builder()
                .id(40L).groupId(1L).actionType(FileHistoryActionType.RENAME)
                .beforeFileName("old-name.pdf").beforeFolderId(10L)
                .afterFileName("new-name.pdf").afterFolderId(10L)
                .fileLoc("/disk1/1_abc_20240101.pdf")
                .fileSize(1024L).backupDt(null).regDt(LocalDateTime.now()).build();
            fakeFileHistoryStoragePort.database.add(renameHistory);

            BackupGroupFilesResponse response = backupGroupFilesService.backup();

            assertThat(response.successCount()).isEqualTo(1);
            // 새 이름으로 업로드
            assertThat(fakeGoogleDrivePort.uploadFileCallCount).isEqualTo(1);
            assertThat(fakeGoogleDrivePort.uploadedFileNames).containsExactly("new-name.pdf");
            // 이전 이름 삭제
            assertThat(fakeGoogleDrivePort.deleteFileCallCount).isEqualTo(1);
            assertThat(fakeGoogleDrivePort.deletedFileNames).containsExactly("old-name.pdf");
            assertThat(fakeFileHistoryStoragePort.updatedBackupDtMap).containsKey(40L);
        }

        @Test
        @DisplayName("[success] driveFolderId가 없는 그룹은 ensureFolder를 호출하고 저장한다")
        void success_ensureFolderWhenDriveFolderIdIsNull() {
            Group group = Group.builder()
                .id(2L).name("폴더없는그룹").driveFolderId(null).backupYn("Y").build();
            fakeGroupStoragePort.groupDatabase.add(group);

            FileHistory history = FileHistory.builder()
                .id(30L).groupId(2L).actionType(FileHistoryActionType.UPLOAD)
                .afterFileName("new-file.pdf").fileLoc("/disk1/2_xyz_20240102.pdf")
                .fileSize(2048L).backupDt(null).regDt(LocalDateTime.now()).build();
            fakeFileHistoryStoragePort.database.add(history);

            BackupGroupFilesResponse response = backupGroupFilesService.backup();

            assertThat(fakeGoogleDrivePort.ensureFolderCallCount).isEqualTo(1);
            assertThat(fakeGroupStoragePort.groupDatabase.get(0).getDriveFolderId())
                .isEqualTo(FIXED_FOLDER_ID);
            assertThat(response.successCount()).isEqualTo(1);
            assertThat(fakeFileHistoryStoragePort.updatedBackupDtMap).containsKey(30L);
        }

        @Test
        @DisplayName("[success] afterFolderId가 있으면 해당 서브폴더에 업로드한다")
        void success_uploadToSubFolderWhenAfterFolderIdExists() {
            Group group = Group.builder()
                .id(1L).name("계층그룹").driveFolderId(FIXED_FOLDER_ID).backupYn("Y").build();
            fakeGroupStoragePort.groupDatabase.add(group);

            FolderInfo folder = FolderInfo.builder()
                .id(100L).groupId(1L).name("문서폴더")
                .regDt(LocalDateTime.now()).build();
            fakeFolderStoragePort.database.add(folder);

            FileHistory history = FileHistory.builder()
                .id(10L).groupId(1L).actionType(FileHistoryActionType.UPLOAD)
                .afterFileName("doc.pdf").afterFolderId(100L)
                .fileLoc("/disk1/1_aaa_20240101.pdf")
                .fileSize(1024L).backupDt(null).regDt(LocalDateTime.now()).build();
            fakeFileHistoryStoragePort.database.add(history);

            BackupGroupFilesResponse response = backupGroupFilesService.backup();

            assertThat(response.successCount()).isEqualTo(1);
            assertThat(fakeGoogleDrivePort.ensureSubFolderCallCount).isEqualTo(1);
            // 업로드된 폴더가 서브폴더 ID인지 확인
            assertThat(fakeGoogleDrivePort.uploadedFolderIds.get(0))
                .startsWith("fake-sub-folder-id-");
        }

        @Test
        @DisplayName("[success] 같은 afterFolderId의 파일이 여러 개면 ensureSubFolder는 한 번만 호출된다")
        void success_ensureSubFolderCalledOnceForSameFolderId() {
            Group group = Group.builder()
                .id(1L).name("캐시그룹").driveFolderId(FIXED_FOLDER_ID).backupYn("Y").build();
            fakeGroupStoragePort.groupDatabase.add(group);

            FolderInfo folder = FolderInfo.builder()
                .id(200L).groupId(1L).name("공유폴더")
                .regDt(LocalDateTime.now()).build();
            fakeFolderStoragePort.database.add(folder);

            FileHistory h1 = FileHistory.builder()
                .id(11L).groupId(1L).actionType(FileHistoryActionType.UPLOAD)
                .afterFileName("file1.txt").afterFolderId(200L)
                .fileLoc("/disk1/1_aa1_20240101.txt")
                .fileSize(100L).backupDt(null).regDt(LocalDateTime.now()).build();
            FileHistory h2 = FileHistory.builder()
                .id(12L).groupId(1L).actionType(FileHistoryActionType.UPLOAD)
                .afterFileName("file2.txt").afterFolderId(200L)
                .fileLoc("/disk1/1_aa2_20240101.txt")
                .fileSize(200L).backupDt(null).regDt(LocalDateTime.now()).build();
            fakeFileHistoryStoragePort.database.add(h1);
            fakeFileHistoryStoragePort.database.add(h2);

            backupGroupFilesService.backup();

            // 동일한 folderId → 캐시 사용으로 1번만 호출
            assertThat(fakeGoogleDrivePort.ensureSubFolderCallCount).isEqualTo(1);
            assertThat(fakeGoogleDrivePort.uploadFileCallCount).isEqualTo(2);
        }

        @Test
        @DisplayName("[error] 파일 읽기 실패 시 해당 항목을 skip하고 나머지 항목은 계속 처리한다")
        void error_fileReadFailureContinuesOtherItems() {
            Group group = Group.builder()
                .id(4L).name("복수이력그룹").driveFolderId(FIXED_FOLDER_ID).backupYn("Y").build();
            fakeGroupStoragePort.groupDatabase.add(group);

            String failFileLoc = "/disk1/missing.txt";
            String successFileLoc = "/disk1/4_ghi_20240104.txt";

            FileHistory failHistory = FileHistory.builder()
                .id(50L).groupId(4L).actionType(FileHistoryActionType.UPLOAD)
                .afterFileName("fail-file.txt").fileLoc(failFileLoc)
                .fileSize(100L).backupDt(null).regDt(LocalDateTime.now()).build();
            FileHistory successHistory = FileHistory.builder()
                .id(51L).groupId(4L).actionType(FileHistoryActionType.UPLOAD)
                .afterFileName("success-file.txt").fileLoc(successFileLoc)
                .fileSize(200L).backupDt(null).regDt(LocalDateTime.now().plusSeconds(1)).build();

            fakeFileHistoryStoragePort.database.add(failHistory);
            fakeFileHistoryStoragePort.database.add(successHistory);
            fakeFilePort.addFailPath(failFileLoc);

            BackupGroupFilesResponse response = backupGroupFilesService.backup();

            assertThat(response.successCount()).isEqualTo(1);
            assertThat(response.failCount()).isEqualTo(1);
            assertThat(fakeFileHistoryStoragePort.updatedBackupDtMap).containsKey(51L);
            assertThat(fakeFileHistoryStoragePort.updatedBackupDtMap).doesNotContainKey(50L);
        }

        @Test
        @DisplayName("[error] Drive 폴더 생성/조회 실패 시 해당 그룹을 skip하고 failCount가 증가한다")
        void error_ensureFolderFailureIncrementsFailCount() {
            Group group = Group.builder()
                .id(6L).name("폴더생성실패그룹").driveFolderId(null).backupYn("Y").build();
            fakeGroupStoragePort.groupDatabase.add(group);

            FileHistory history = FileHistory.builder()
                .id(70L).groupId(6L).actionType(FileHistoryActionType.UPLOAD)
                .afterFileName("some-file.txt").fileLoc("/disk1/6_pqr_20240107.txt")
                .fileSize(300L).backupDt(null).regDt(LocalDateTime.now()).build();
            fakeFileHistoryStoragePort.database.add(history);
            fakeGoogleDrivePort.shouldThrowEnsureFolder = true;

            BackupGroupFilesResponse response = backupGroupFilesService.backup();

            assertThat(response.totalGroups()).isEqualTo(1);
            assertThat(response.failCount()).isEqualTo(1);
            assertThat(response.successCount()).isEqualTo(0);
            assertThat(fakeGoogleDrivePort.uploadFileCallCount).isEqualTo(0);
            assertThat(fakeFileHistoryStoragePort.updatedBackupDtMap).isEmpty();
        }

        @Test
        @DisplayName("[success] 폴더 내 폴더(중첩 구조)도 Drive 계층으로 그대로 재현된다")
        void success_nestedFolderHierarchyReproducedInDrive() {
            // given
            // 앱 폴더 구조:
            // 루트(id=10, parentId=null) → 문서(id=11, parentId=10) → 계약서(id=12, parentId=11)
            Group group = Group.builder()
                .id(1L).name("계층그룹").driveFolderId(FIXED_FOLDER_ID).backupYn("Y").build();
            fakeGroupStoragePort.groupDatabase.add(group);

            FolderInfo rootFolder = FolderInfo.builder()
                .id(10L).groupId(1L).name("루트").parentId(null)
                .regDt(LocalDateTime.now()).build();
            FolderInfo docFolder = FolderInfo.builder()
                .id(11L).groupId(1L).name("문서").parentId(10L)
                .regDt(LocalDateTime.now()).build();
            FolderInfo contractFolder = FolderInfo.builder()
                .id(12L).groupId(1L).name("계약서").parentId(11L)
                .regDt(LocalDateTime.now()).build();
            fakeFolderStoragePort.database.add(rootFolder);
            fakeFolderStoragePort.database.add(docFolder);
            fakeFolderStoragePort.database.add(contractFolder);

            // 파일은 가장 깊은 "계약서" 폴더에 위치
            FileHistory history = FileHistory.builder()
                .id(10L).groupId(1L).actionType(FileHistoryActionType.UPLOAD)
                .afterFileName("contract.pdf").afterFolderId(12L)
                .fileLoc("/disk1/1_ccc_20240101.pdf")
                .fileSize(1024L).backupDt(null).regDt(LocalDateTime.now()).build();
            fakeFileHistoryStoragePort.database.add(history);

            // when
            BackupGroupFilesResponse response = backupGroupFilesService.backup();

            // then
            assertThat(response.successCount()).isEqualTo(1);
            // 루트(10) → 문서(11) → 계약서(12) 순서로 서브폴더가 3번 생성됨
            assertThat(fakeGoogleDrivePort.ensureSubFolderCallCount).isEqualTo(3);
            // 파일은 계약서 폴더에 업로드
            assertThat(fakeGoogleDrivePort.uploadedFolderIds.get(0))
                .isEqualTo("fake-sub-folder-id-계약서");
        }

        @Test
        @DisplayName("[success] 같은 폴더 경로를 가진 파일이 여러 개면 런타임 캐시로 폴더 생성은 한 번만 호출된다")
        void success_runtimeCachePreventsRedundantSubFolderCreation() {
            // 문서(id=11) → 계약서(id=12) 구조에서 계약서 폴더 파일 2개
            // 두 번째 파일 처리 시 캐시 히트로 Drive API 재호출 없음
            Group group = Group.builder()
                .id(1L).name("캐시검증그룹").driveFolderId(FIXED_FOLDER_ID).backupYn("Y").build();
            fakeGroupStoragePort.groupDatabase.add(group);

            FolderInfo docFolder = FolderInfo.builder()
                .id(11L).groupId(1L).name("문서").parentId(null)
                .regDt(LocalDateTime.now()).build();
            FolderInfo contractFolder = FolderInfo.builder()
                .id(12L).groupId(1L).name("계약서").parentId(11L)
                .regDt(LocalDateTime.now()).build();
            fakeFolderStoragePort.database.add(docFolder);
            fakeFolderStoragePort.database.add(contractFolder);

            FileHistory h1 = FileHistory.builder()
                .id(10L).groupId(1L).actionType(FileHistoryActionType.UPLOAD)
                .afterFileName("a.pdf").afterFolderId(12L)
                .fileLoc("/disk1/1_aa_20240101.pdf")
                .fileSize(512L).backupDt(null).regDt(LocalDateTime.now()).build();
            FileHistory h2 = FileHistory.builder()
                .id(11L).groupId(1L).actionType(FileHistoryActionType.UPLOAD)
                .afterFileName("b.pdf").afterFolderId(12L)
                .fileLoc("/disk1/1_bb_20240101.pdf")
                .fileSize(512L).backupDt(null).regDt(LocalDateTime.now()).build();
            fakeFileHistoryStoragePort.database.add(h1);
            fakeFileHistoryStoragePort.database.add(h2);

            backupGroupFilesService.backup();

            // 문서(11), 계약서(12) 생성 → 2번 (두 번째 파일은 캐시 히트)
            assertThat(fakeGoogleDrivePort.ensureSubFolderCallCount).isEqualTo(2);
            assertThat(fakeGoogleDrivePort.uploadFileCallCount).isEqualTo(2);
        }

        @Test
        @DisplayName("[success] backupYn이 N인 그룹은 백업 대상에서 제외된다")
        void success_excludeGroupWhenBackupYnIsN() {
            Group enabledGroup = Group.builder()
                .id(1L).name("백업활성그룹").driveFolderId(FIXED_FOLDER_ID).backupYn("Y").build();
            Group disabledGroup = Group.builder()
                .id(2L).name("백업비활성그룹").driveFolderId(FIXED_FOLDER_ID).backupYn("N").build();
            fakeGroupStoragePort.groupDatabase.add(enabledGroup);
            fakeGroupStoragePort.groupDatabase.add(disabledGroup);

            FileHistory h1 = FileHistory.builder()
                .id(10L).groupId(1L).actionType(FileHistoryActionType.UPLOAD)
                .afterFileName("file1.txt").fileLoc("/disk1/1_aaa_20240101.txt")
                .fileSize(100L).backupDt(null).regDt(LocalDateTime.now()).build();
            FileHistory h2 = FileHistory.builder()
                .id(20L).groupId(2L).actionType(FileHistoryActionType.UPLOAD)
                .afterFileName("file2.txt").fileLoc("/disk1/2_bbb_20240101.txt")
                .fileSize(200L).backupDt(null).regDt(LocalDateTime.now()).build();
            fakeFileHistoryStoragePort.database.add(h1);
            fakeFileHistoryStoragePort.database.add(h2);

            BackupGroupFilesResponse response = backupGroupFilesService.backup();

            // backupYn=Y 그룹만 대상 → totalGroups=1, 비활성 그룹은 아예 조회되지 않음
            assertThat(response.totalGroups()).isEqualTo(1);
            assertThat(response.successCount()).isEqualTo(1);
            assertThat(fakeGoogleDrivePort.uploadFileCallCount).isEqualTo(1);
            // 비활성 그룹 이력(id=20)은 backupDt 갱신 없음
            assertThat(fakeFileHistoryStoragePort.updatedBackupDtMap).containsKey(10L);
            assertThat(fakeFileHistoryStoragePort.updatedBackupDtMap).doesNotContainKey(20L);
        }

        @Test
        @DisplayName("[error] 서브폴더 생성 실패 시 해당 이력만 skip하고 failCount가 증가한다")
        void error_ensureSubFolderFailureSkipsItem() {
            Group group = Group.builder()
                .id(1L).name("서브폴더실패그룹").driveFolderId(FIXED_FOLDER_ID).backupYn("Y").build();
            fakeGroupStoragePort.groupDatabase.add(group);

            FolderInfo folder = FolderInfo.builder()
                .id(400L).groupId(1L).name("실패폴더")
                .regDt(LocalDateTime.now()).build();
            fakeFolderStoragePort.database.add(folder);

            FileHistory history = FileHistory.builder()
                .id(80L).groupId(1L).actionType(FileHistoryActionType.UPLOAD)
                .afterFileName("file.txt").afterFolderId(400L)
                .fileLoc("/disk1/1_zzz_20240101.txt")
                .fileSize(100L).backupDt(null).regDt(LocalDateTime.now()).build();
            fakeFileHistoryStoragePort.database.add(history);
            fakeGoogleDrivePort.shouldThrowEnsureSubFolder = true;

            BackupGroupFilesResponse response = backupGroupFilesService.backup();

            assertThat(response.failCount()).isEqualTo(1);
            assertThat(fakeGoogleDrivePort.uploadFileCallCount).isEqualTo(0);
            assertThat(fakeFileHistoryStoragePort.updatedBackupDtMap).isEmpty();
        }
    }
}
