package com.odcloud.adapter.out.persistence.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.IntegrationTestSupport;
import com.odcloud.application.file.port.in.command.FindFilesCommand;
import com.odcloud.domain.model.FileInfo;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class FileStorageAdapterTest extends IntegrationTestSupport {

    @Autowired
    FileInfoStorageAdapter adapter;

    @Autowired
    EntityManager entityManager;

    @AfterEach
    void tearDown() {
        entityManager.createQuery("DELETE FROM FileInfoEntity").executeUpdate();
        entityManager.createQuery("DELETE FROM FolderInfoEntity").executeUpdate();
        entityManager.flush();
        entityManager.clear();
    }

    @Nested
    @DisplayName("[save] 파일을 저장하는 메소드")
    class Describe_save {

        @Test
        @DisplayName("[success] 신규 파일을 저장한다")
        void success() {
            // given
            LocalDateTime now = LocalDateTime.now();

            // 폴더 생성
            FolderInfoEntity folder = FolderInfoEntity.builder()
                .parentId(null)
                .groupId(1L)
                .name("테스트 폴더")
                .owner("owner@example.com")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(folder);
            entityManager.flush();

            FileInfo file = FileInfo.builder()
                .folderId(folder.getId())
                .fileName("test.txt")
                .fileLoc("/test-group/12345678_20231201.txt")
                .regDt(now)
                .build();

            // when
            adapter.save(file);
            entityManager.flush();
            entityManager.clear();

            // then
            FileInfoEntity savedEntity = entityManager
                .createQuery("SELECT f FROM FileInfoEntity f WHERE f.folderId = :folderId",
                    FileInfoEntity.class)
                .setParameter("folderId", folder.getId())
                .getSingleResult();

            assertThat(savedEntity).isNotNull();
            assertThat(savedEntity.getFolderId()).isEqualTo(folder.getId());
            assertThat(savedEntity.getFileName()).isEqualTo("test.txt");
            assertThat(savedEntity.getFileLoc()).isEqualTo("/test-group/12345678_20231201.txt");
        }

        @Test
        @DisplayName("[success] 기존 파일을 업데이트한다")
        void success_update() {
            // given
            LocalDateTime now = LocalDateTime.now();

            // 폴더 생성
            FolderInfoEntity folder = FolderInfoEntity.builder()
                .parentId(null)
                .groupId(1L)
                .name("테스트 폴더")
                .owner("owner@example.com")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(folder);

            // 기존 파일 생성
            FileInfoEntity existingFile = FileInfoEntity.builder()
                .folderId(folder.getId())
                .fileName("old.txt")
                .fileLoc("/test-group/old_file.txt")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(existingFile);
            entityManager.flush();
            entityManager.clear();

            FileInfo updatedFile = FileInfo.builder()
                .id(existingFile.getId())
                .folderId(folder.getId())
                .fileName("new.txt")
                .fileLoc("/test-group/new_file.txt")
                .regDt(now)
                .modDt(now.plusHours(1))
                .build();

            // when
            adapter.save(updatedFile);
            entityManager.flush();
            entityManager.clear();

            // then
            FileInfoEntity savedEntity = entityManager.find(FileInfoEntity.class,
                existingFile.getId());
            assertThat(savedEntity).isNotNull();
            assertThat(savedEntity.getFileName()).isEqualTo("new.txt");
            assertThat(savedEntity.getFileLoc()).isEqualTo("/test-group/new_file.txt");
        }

        @Test
        @DisplayName("[success] 여러 파일을 저장한다")
        void success_multipleFiles() {
            // given
            LocalDateTime now = LocalDateTime.now();

            // 폴더 생성
            FolderInfoEntity folder = FolderInfoEntity.builder()
                .parentId(null)
                .groupId(1L)
                .name("테스트 폴더")
                .owner("owner@example.com")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(folder);
            entityManager.flush();

            FileInfo file1 = FileInfo.builder()
                .folderId(folder.getId())
                .fileName("file1.txt")
                .fileLoc("/test-group/file1.txt")
                .regDt(now)
                .build();

            FileInfo file2 = FileInfo.builder()
                .folderId(folder.getId())
                .fileName("file2.txt")
                .fileLoc("/test-group/file2.txt")
                .regDt(now)
                .build();

            FileInfo file3 = FileInfo.builder()
                .folderId(folder.getId())
                .fileName("file3.txt")
                .fileLoc("/test-group/file3.txt")
                .regDt(now)
                .build();

            // when
            adapter.save(file1);
            adapter.save(file2);
            adapter.save(file3);
            entityManager.flush();
            entityManager.clear();

            // then
            var savedFiles = entityManager
                .createQuery("SELECT f FROM FileInfoEntity f WHERE f.folderId = :folderId",
                    FileInfoEntity.class)
                .setParameter("folderId", folder.getId())
                .getResultList();

            assertThat(savedFiles).hasSize(3);
            assertThat(savedFiles)
                .extracting(FileInfoEntity::getFileName)
                .containsExactlyInAnyOrder("file1.txt", "file2.txt", "file3.txt");
        }

        @Test
        @DisplayName("[success] 다양한 파일 확장자를 저장한다")
        void success_variousExtensions() {
            // given
            LocalDateTime now = LocalDateTime.now();

            // 폴더 생성
            FolderInfoEntity folder = FolderInfoEntity.builder()
                .parentId(null)
                .groupId(1L)
                .name("테스트 폴더")
                .owner("owner@example.com")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(folder);
            entityManager.flush();

            FileInfo txtFile = FileInfo.builder()
                .folderId(folder.getId())
                .fileName("document.txt")
                .fileLoc("/test-group/document.txt")
                .regDt(now)
                .build();

            FileInfo pdfFile = FileInfo.builder()
                .folderId(folder.getId())
                .fileName("report.pdf")
                .fileLoc("/test-group/report.pdf")
                .regDt(now)
                .build();

            FileInfo imageFile = FileInfo.builder()
                .folderId(folder.getId())
                .fileName("photo.jpg")
                .fileLoc("/test-group/photo.jpg")
                .regDt(now)
                .build();

            // when
            adapter.save(txtFile);
            adapter.save(pdfFile);
            adapter.save(imageFile);
            entityManager.flush();
            entityManager.clear();

            // then
            var savedFiles = entityManager
                .createQuery(
                    "SELECT f FROM FileInfoEntity f WHERE f.folderId = :folderId ORDER BY f.fileName",
                    FileInfoEntity.class)
                .setParameter("folderId", folder.getId())
                .getResultList();

            assertThat(savedFiles).hasSize(3);
            assertThat(savedFiles.get(0).getFileName()).isEqualTo("document.txt");
            assertThat(savedFiles.get(1).getFileName()).isEqualTo("photo.jpg");
            assertThat(savedFiles.get(2).getFileName()).isEqualTo("report.pdf");
        }

        @Test
        @DisplayName("[success] 서로 다른 폴더에 같은 이름의 파일을 저장한다")
        void success_sameFileNameInDifferentFolders() {
            // given
            LocalDateTime now = LocalDateTime.now();

            // 폴더 1 생성
            FolderInfoEntity folder1 = FolderInfoEntity.builder()
                .parentId(null)
                .groupId(1L)
                .name("폴더 1")
                .owner("owner@example.com")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(folder1);

            // 폴더 2 생성
            FolderInfoEntity folder2 = FolderInfoEntity.builder()
                .parentId(null)
                .groupId(1L)
                .name("폴더 2")
                .owner("owner@example.com")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(folder2);

            entityManager.flush();

            FileInfo fileInFolder1 = FileInfo.builder()
                .folderId(folder1.getId())
                .fileName("test.txt")
                .fileLoc("/group-1/test.txt")
                .regDt(now)
                .build();

            FileInfo fileInFolder2 = FileInfo.builder()
                .folderId(folder2.getId())
                .fileName("test.txt")
                .fileLoc("/group-2/test.txt")
                .regDt(now)
                .build();

            // when
            adapter.save(fileInFolder1);
            adapter.save(fileInFolder2);
            entityManager.flush();
            entityManager.clear();

            // then
            var filesInFolder1 = entityManager
                .createQuery("SELECT f FROM FileInfoEntity f WHERE f.folderId = :folderId",
                    FileInfoEntity.class)
                .setParameter("folderId", folder1.getId())
                .getResultList();

            var filesInFolder2 = entityManager
                .createQuery("SELECT f FROM FileInfoEntity f WHERE f.folderId = :folderId",
                    FileInfoEntity.class)
                .setParameter("folderId", folder2.getId())
                .getResultList();

            assertThat(filesInFolder1).hasSize(1);
            assertThat(filesInFolder2).hasSize(1);
            assertThat(filesInFolder1.get(0).getFileName()).isEqualTo("test.txt");
            assertThat(filesInFolder2.get(0).getFileName()).isEqualTo("test.txt");
            assertThat(filesInFolder1.get(0).getFileLoc()).isEqualTo("/group-1/test.txt");
            assertThat(filesInFolder2.get(0).getFileLoc()).isEqualTo("/group-2/test.txt");
        }

        @Test
        @DisplayName("[success] null modDt로 파일을 저장한다")
        void success_nullModDt() {
            // given
            LocalDateTime now = LocalDateTime.now();

            // 폴더 생성
            FolderInfoEntity folder = FolderInfoEntity.builder()
                .parentId(null)
                .groupId(1L)
                .name("테스트 폴더")
                .owner("owner@example.com")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(folder);
            entityManager.flush();

            FileInfo file = FileInfo.builder()
                .folderId(folder.getId())
                .fileName("test.txt")
                .fileLoc("/test-group/test.txt")
                .regDt(now)
                .modDt(null)
                .build();

            // when
            adapter.save(file);
            entityManager.flush();
            entityManager.clear();

            // then
            FileInfoEntity savedEntity = entityManager
                .createQuery("SELECT f FROM FileInfoEntity f WHERE f.folderId = :folderId",
                    FileInfoEntity.class)
                .setParameter("folderId", folder.getId())
                .getSingleResult();

            assertThat(savedEntity).isNotNull();
            assertThat(savedEntity.getModDt()).isNull();
        }

        @Test
        @DisplayName("[success] 긴 파일 경로를 저장한다")
        void success_longFilePath() {
            // given
            LocalDateTime now = LocalDateTime.now();

            // 폴더 생성
            FolderInfoEntity folder = FolderInfoEntity.builder()
                .parentId(null)
                .groupId(1L)
                .name("테스트 폴더")
                .owner("owner@example.com")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(folder);
            entityManager.flush();

            String longFilePath = "/test-group/very/long/nested/folder/path/12345678_20231201_very_long_filename.txt";
            FileInfo file = FileInfo.builder()
                .folderId(folder.getId())
                .fileName("very_long_filename.txt")
                .fileLoc(longFilePath)
                .regDt(now)
                .build();

            // when
            adapter.save(file);
            entityManager.flush();
            entityManager.clear();

            // then
            FileInfoEntity savedEntity = entityManager
                .createQuery("SELECT f FROM FileInfoEntity f WHERE f.folderId = :folderId",
                    FileInfoEntity.class)
                .setParameter("folderId", folder.getId())
                .getSingleResult();

            assertThat(savedEntity).isNotNull();
            assertThat(savedEntity.getFileLoc()).isEqualTo(longFilePath);
        }

        @Test
        @DisplayName("[success] 한글 파일명을 저장한다")
        void success_koreanFileName() {
            // given
            LocalDateTime now = LocalDateTime.now();

            // 폴더 생성
            FolderInfoEntity folder = FolderInfoEntity.builder()
                .parentId(null)
                .groupId(1L)
                .name("테스트 폴더")
                .owner("owner@example.com")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(folder);
            entityManager.flush();

            FileInfo file = FileInfo.builder()
                .folderId(folder.getId())
                .fileName("테스트파일.txt")
                .fileLoc("/test-group/12345678_20231201.txt")
                .regDt(now)
                .build();

            // when
            adapter.save(file);
            entityManager.flush();
            entityManager.clear();

            // then
            FileInfoEntity savedEntity = entityManager
                .createQuery("SELECT f FROM FileInfoEntity f WHERE f.folderId = :folderId",
                    FileInfoEntity.class)
                .setParameter("folderId", folder.getId())
                .getSingleResult();

            assertThat(savedEntity).isNotNull();
            assertThat(savedEntity.getFileName()).isEqualTo("테스트파일.txt");
        }
    }

    @Nested
    @DisplayName("[findAll] 파일 목록을 조회하는 메소드")
    class Describe_findAll {

        @Test
        @DisplayName("[success] folderId로 파일 목록을 조회한다")
        void success_findByFolderId() {
            // given
            LocalDateTime now = LocalDateTime.now();

            // 폴더 생성
            FolderInfoEntity folder = FolderInfoEntity.builder()
                .parentId(null)
                .groupId(1L)
                .name("Test Folder")
                .owner("user@example.com")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(folder);

            // 파일 생성
            FileInfoEntity file1 = FileInfoEntity.builder()
                .folderId(folder.getId())
                .groupId(1L)
                .fileName("test1.txt")
                .fileLoc("/group1/test1.txt")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(file1);

            FileInfoEntity file2 = FileInfoEntity.builder()
                .folderId(folder.getId())
                .groupId(1L)
                .fileName("test2.txt")
                .fileLoc("/group1/test2.txt")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(file2);

            entityManager.flush();
            entityManager.clear();

            com.odcloud.domain.model.Account account = com.odcloud.domain.model.Account.builder()
                .id(1L)
                .email("user@example.com")
                .groups(List.of(com.odcloud.domain.model.Group.of(1L)))
                .build();

            FindFilesCommand command = FindFilesCommand.builder()
                .account(account)
                .folderId(folder.getId())
                .sortType("NAME_ASC")
                .build();

            // when
            var result = adapter.findAll(command);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).extracting(FileInfo::getFileName)
                .containsExactly("test1.txt", "test2.txt");
        }

        @Test
        @DisplayName("[success] keyword로 파일을 검색한다")
        void success_searchByKeyword() {
            // given
            LocalDateTime now = LocalDateTime.now();

            // 폴더 생성
            FolderInfoEntity folder = FolderInfoEntity.builder()
                .parentId(null)
                .groupId(1L)
                .name("Test Folder")
                .owner("user@example.com")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(folder);

            // 파일 생성
            FileInfoEntity file1 = FileInfoEntity.builder()
                .folderId(folder.getId())
                .groupId(1L)
                .fileName("report.pdf")
                .fileLoc("/group1/report.pdf")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(file1);

            FileInfoEntity file2 = FileInfoEntity.builder()
                .folderId(folder.getId())
                .groupId(1L)
                .fileName("document.txt")
                .fileLoc("/group1/document.txt")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(file2);

            FileInfoEntity file3 = FileInfoEntity.builder()
                .folderId(folder.getId())
                .groupId(1L)
                .fileName("report2.pdf")
                .fileLoc("/group1/report2.pdf")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(file3);

            entityManager.flush();
            entityManager.clear();

            com.odcloud.domain.model.Account account = com.odcloud.domain.model.Account.builder()
                .id(1L)
                .email("user@example.com")
                .groups(List.of(com.odcloud.domain.model.Group.of(1L)))
                .build();

            FindFilesCommand command = FindFilesCommand.builder()
                .account(account)
                .keyword("report")
                .sortType("NAME_ASC")
                .build();

            // when
            var result = adapter.findAll(command);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).extracting(FileInfo::getFileName)
                .contains("report.pdf", "report2.pdf");
        }

        @Test
        @DisplayName("[success] 정렬 순서를 적용한다")
        void success_withSorting() {
            // given
            LocalDateTime now = LocalDateTime.now();

            // 폴더 생성
            FolderInfoEntity folder = FolderInfoEntity.builder()
                .parentId(null)
                .groupId(1L)
                .name("Test Folder")
                .owner("user@example.com")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(folder);

            // 파일 생성 (역순으로)
            FileInfoEntity file1 = FileInfoEntity.builder()
                .folderId(folder.getId())
                .groupId(1L)
                .fileName("c.txt")
                .fileLoc("/group1/c.txt")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(file1);

            FileInfoEntity file2 = FileInfoEntity.builder()
                .folderId(folder.getId())
                .groupId(1L)
                .fileName("a.txt")
                .fileLoc("/group1/a.txt")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(file2);

            FileInfoEntity file3 = FileInfoEntity.builder()
                .folderId(folder.getId())
                .groupId(1L)
                .fileName("b.txt")
                .fileLoc("/group1/b.txt")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(file3);

            entityManager.flush();
            entityManager.clear();

            com.odcloud.domain.model.Account account = com.odcloud.domain.model.Account.builder()
                .id(1L)
                .email("user@example.com")
                .groups(List.of(com.odcloud.domain.model.Group.of(1L)))
                .build();

            FindFilesCommand command = FindFilesCommand.builder()
                .account(account)
                .folderId(folder.getId())
                .sortType("NAME_DESC")
                .build();

            // when
            var result = adapter.findAll(command);

            // then
            assertThat(result).hasSize(3);
            assertThat(result).extracting(FileInfo::getFileName)
                .containsExactly("c.txt", "b.txt", "a.txt");
        }

        @Test
        @DisplayName("[success] keyword 검색 시 권한이 없는 폴더의 파일은 제외된다")
        void success_keywordSearchExcludesUnauthorizedFolderFiles() {
            // given
            LocalDateTime now = LocalDateTime.now();

            // PUBLIC 폴더 생성 (group1)
            FolderInfoEntity publicFolder = FolderInfoEntity.builder()
                .parentId(null)
                .groupId(1L)
                .name("Public Folder")
                .owner("owner@example.com")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(publicFolder);

            // PUBLIC 폴더에 "test" 키워드 파일 생성
            FileInfoEntity publicTestFile = FileInfoEntity.builder()
                .folderId(publicFolder.getId())
                .groupId(1L)
                .fileName("test-public.txt")
                .fileLoc("/group1/public/test-public.txt")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(publicTestFile);

            entityManager.flush();
            entityManager.clear();

            // 권한 없는 사용자 (PRIVATE 폴더의 owner가 아님)
            com.odcloud.domain.model.Account account = com.odcloud.domain.model.Account.builder()
                .id(1L)
                .email("user@example.com")
                .groups(List.of(com.odcloud.domain.model.Group.of(1L)))
                .build();

            FindFilesCommand command = FindFilesCommand.builder()
                .account(account)
                .keyword("test")
                .sortType("NAME_ASC")
                .build();

            // when
            var result = adapter.findAll(command);

            // then
            // PUBLIC 폴더의 파일만 조회되고, PRIVATE 폴더의 파일은 제외되어야 함
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getFileName()).isEqualTo("test-public.txt");
        }

        @Test
        @DisplayName("[success] keyword 검색 시 자신의 PRIVATE 폴더 파일은 포함된다")
        void success_keywordSearchIncludesOwnPrivateFolderFiles() {
            // given
            LocalDateTime now = LocalDateTime.now();

            // PUBLIC 폴더 생성
            FolderInfoEntity publicFolder = FolderInfoEntity.builder()
                .parentId(null)
                .groupId(1L)
                .name("Public Folder")
                .owner("owner@example.com")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(publicFolder);

            // 자신의 PRIVATE 폴더 생성
            FolderInfoEntity myPrivateFolder = FolderInfoEntity.builder()
                .parentId(null)
                .groupId(1L)
                .name("My Private Folder")
                .owner("user@example.com")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(myPrivateFolder);

            // PUBLIC 폴더에 파일 생성
            FileInfoEntity publicFile = FileInfoEntity.builder()
                .folderId(publicFolder.getId())
                .groupId(1L)
                .fileName("report-public.pdf")
                .fileLoc("/group1/public/report-public.pdf")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(publicFile);

            // 자신의 PRIVATE 폴더에 파일 생성
            FileInfoEntity myPrivateFile = FileInfoEntity.builder()
                .folderId(myPrivateFolder.getId())
                .groupId(1L)
                .fileName("report-private.pdf")
                .fileLoc("/group1/my-private/report-private.pdf")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(myPrivateFile);

            entityManager.flush();
            entityManager.clear();

            com.odcloud.domain.model.Account account = com.odcloud.domain.model.Account.builder()
                .id(1L)
                .email("user@example.com")
                .groups(List.of(com.odcloud.domain.model.Group.of(1L)))
                .build();

            FindFilesCommand command = FindFilesCommand.builder()
                .account(account)
                .keyword("report")
                .sortType("NAME_ASC")
                .build();

            // when
            var result = adapter.findAll(command);

            // then
            // PUBLIC 폴더와 자신의 PRIVATE 폴더의 파일 모두 조회되어야 함
            assertThat(result).hasSize(2);
            assertThat(result).extracting(FileInfo::getFileName)
                .containsExactlyInAnyOrder("report-public.pdf", "report-private.pdf");
        }
    }
}
