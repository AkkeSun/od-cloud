package com.odcloud.application.file.service.find_files;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.application.file.port.in.command.FindFilesCommand;
import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.FileInfo;
import com.odcloud.domain.model.FolderInfo;
import com.odcloud.domain.model.Group;
import com.odcloud.fakeClass.FakeFileStoragePort;
import com.odcloud.fakeClass.FakeFolderStoragePort;
import com.odcloud.fakeClass.FakeProfileConstant;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FindFilesServiceTest {

    private FakeFileStoragePort fakeFileStoragePort;
    private FakeFolderStoragePort fakeFolderStoragePort;
    private FindFilesService findFilesService;

    @BeforeEach
    void setUp() {
        fakeFileStoragePort = new FakeFileStoragePort();
        fakeFolderStoragePort = new FakeFolderStoragePort();
        findFilesService = new FindFilesService(
            FakeProfileConstant.create(),
            fakeFileStoragePort,
            fakeFolderStoragePort
        );
    }

    @Nested
    @DisplayName("[findAll] 파일 및 폴더 목록 조회")
    class Describe_findAll {

        @Test
        @DisplayName("[success] folderId로 파일과 폴더 목록을 조회한다")
        void success_findByFolderId() {
            // given
            Account account = Account.builder()
                .id(1L)
                .email("user@example.com")
                .groups(List.of(Group.of(1L)))
                .build();

            FolderInfo parentFolder = FolderInfo.builder()
                .id(1L)
                .parentId(0L)
                .groupId(1L)
                .name("Parent Folder")
                .owner("user@example.com")
                .path("/group1")
                .regDt(LocalDateTime.now())
                .build();
            fakeFolderStoragePort.database.add(parentFolder);

            FolderInfo childFolder = FolderInfo.builder()
                .id(2L)
                .parentId(1L)
                .groupId(1L)
                .name("Child Folder")
                .owner("user@example.com")
                .path("/group1/child")
                .regDt(LocalDateTime.now())
                .build();
            fakeFolderStoragePort.database.add(childFolder);

            FileInfo file1 = FileInfo.builder()
                .id(1L)
                .folderId(1L)
                .fileName("test1.txt")
                .fileLoc("/group1/test1.txt")
                .regDt(LocalDateTime.now())
                .build();
            fakeFileStoragePort.database.add(file1);

            FileInfo file2 = FileInfo.builder()
                .id(2L)
                .folderId(1L)
                .fileName("test2.txt")
                .fileLoc("/group1/test2.txt")
                .regDt(LocalDateTime.now())
                .build();
            fakeFileStoragePort.database.add(file2);

            FindFilesCommand command = FindFilesCommand.builder()
                .account(account)
                .folderId(1L)
                .sortType("NAME_ASC")
                .build();

            // when
            FindFilesServiceResponse response = findFilesService.findAll(command);

            // then
            assertThat(response).isNotNull();
            assertThat(response.parentFolderId()).isEqualTo(1L);
            assertThat(response.folders()).hasSize(1);
            assertThat(response.folders().get(0).name()).isEqualTo("Child Folder");
            assertThat(response.files()).hasSize(2);
            assertThat(response.files()).extracting("name")
                .containsExactly("test1.txt", "test2.txt");
        }

        @Test
        @DisplayName("[success] keyword로 파일을 검색한다")
        void success_searchByKeyword() {
            // given
            Account account = Account.builder()
                .id(1L)
                .email("user@example.com")
                .groups(List.of(Group.of(1L)))
                .build();

            FolderInfo folder = FolderInfo.builder()
                .id(1L)
                .parentId(0L)
                .groupId(1L)
                .name("Test Folder")
                .owner("user@example.com")
                .path("/group1")
                .regDt(LocalDateTime.now())
                .build();
            fakeFolderStoragePort.database.add(folder);

            FileInfo file1 = FileInfo.builder()
                .id(1L)
                .folderId(1L)
                .fileName("report.pdf")
                .fileLoc("/group1/report.pdf")
                .regDt(LocalDateTime.now())
                .build();
            fakeFileStoragePort.database.add(file1);

            FileInfo file2 = FileInfo.builder()
                .id(2L)
                .folderId(1L)
                .fileName("document.txt")
                .fileLoc("/group1/document.txt")
                .regDt(LocalDateTime.now())
                .build();
            fakeFileStoragePort.database.add(file2);

            FileInfo file3 = FileInfo.builder()
                .id(3L)
                .folderId(1L)
                .fileName("report2.pdf")
                .fileLoc("/group1/report2.pdf")
                .regDt(LocalDateTime.now())
                .build();
            fakeFileStoragePort.database.add(file3);

            FindFilesCommand command = FindFilesCommand.builder()
                .account(account)
                .keyword("report")
                .sortType("NAME_ASC")
                .build();

            // when
            FindFilesServiceResponse response = findFilesService.findAll(command);

            // then
            assertThat(response).isNotNull();
            assertThat(response.files()).hasSize(2);
            assertThat(response.files()).extracting("name")
                .containsExactly("report.pdf", "report2.pdf");
        }

        @Test
        @DisplayName("[success] groupId로 폴더를 필터링한다")
        void success_filterByGroupId() {
            // given
            Account account = Account.builder()
                .id(1L)
                .email("user@example.com")
                .groups(List.of(Group.of(1L), Group.of(2L)))
                .build();

            FolderInfo folder1 = FolderInfo.builder()
                .id(1L)
                .parentId(0L)
                .groupId(1L)
                .name("Group1 Folder")
                .owner("user@example.com")
                .path("/group1")
                .regDt(LocalDateTime.now())
                .build();
            fakeFolderStoragePort.database.add(folder1);

            FolderInfo folder2 = FolderInfo.builder()
                .id(2L)
                .parentId(0L)
                .groupId(2L)
                .name("Group2 Folder")
                .owner("user@example.com")
                .path("/group2")
                .regDt(LocalDateTime.now())
                .build();
            fakeFolderStoragePort.database.add(folder2);

            FolderInfo childFolder1 = FolderInfo.builder()
                .id(3L)
                .parentId(1L)
                .groupId(1L)
                .name("Child of Group1")
                .owner("user@example.com")
                .path("/group1/child")
                .regDt(LocalDateTime.now())
                .build();
            fakeFolderStoragePort.database.add(childFolder1);

            FolderInfo childFolder2 = FolderInfo.builder()
                .id(4L)
                .parentId(1L)
                .groupId(2L)
                .name("Child of Group2")
                .owner("user@example.com")
                .path("/group1/child2")
                .regDt(LocalDateTime.now())
                .build();
            fakeFolderStoragePort.database.add(childFolder2);

            FindFilesCommand command = FindFilesCommand.builder()
                .account(account)
                .folderId(1L)
                .groupId(1L)
                .sortType("NAME_ASC")
                .build();

            // when
            FindFilesServiceResponse response = findFilesService.findAll(command);

            // then
            assertThat(response).isNotNull();
            assertThat(response.folders()).hasSize(1);
            assertThat(response.folders().get(0).name()).isEqualTo("Child of Group1");
            assertThat(response.folders().get(0).groupId()).isEqualTo(1L);
        }

        // Note: 권한 체크 로직(PUBLIC/PRIVATE)은 Repository 계층에서 처리되므로
        // 통합 테스트(FileStorageAdapterTest, FolderStorageAdapterTest)에서 테스트합니다.

        @Test
        @DisplayName("[success] 정렬 타입이 null이면 기본 정렬을 적용한다")
        void success_defaultSorting() {
            // given
            Account account = Account.builder()
                .id(1L)
                .email("user@example.com")
                .groups(List.of(Group.of(1L)))
                .build();

            FolderInfo folder = FolderInfo.builder()
                .id(1L)
                .parentId(0L)
                .groupId(1L)
                .name("Test Folder")
                .owner("user@example.com")
                .path("/group1")
                .regDt(LocalDateTime.now())
                .build();
            fakeFolderStoragePort.database.add(folder);

            FindFilesCommand command = FindFilesCommand.builder()
                .account(account)
                .folderId(0L)
                .sortType(null)
                .build();

            // when
            FindFilesServiceResponse response = findFilesService.findAll(command);

            // then
            assertThat(response).isNotNull();
            assertThat(response.folders()).hasSize(1);
        }

        @Test
        @DisplayName("[success] 빈 결과를 반환한다")
        void success_emptyResult() {
            // given
            Account account = Account.builder()
                .id(1L)
                .email("user@example.com")
                .groups(List.of(Group.of(1L)))
                .build();

            FindFilesCommand command = FindFilesCommand.builder()
                .account(account)
                .folderId(999L)
                .sortType("NAME_ASC")
                .build();

            // when
            FindFilesServiceResponse response = findFilesService.findAll(command);

            // then
            assertThat(response).isNotNull();
            assertThat(response.folders()).isEmpty();
            assertThat(response.files()).isEmpty();
            assertThat(response.parentFolderId()).isEqualTo(999L);
        }
    }
}
