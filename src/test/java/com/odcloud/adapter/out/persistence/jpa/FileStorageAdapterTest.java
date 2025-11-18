package com.odcloud.adapter.out.persistence.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.IntegrationTestSupport;
import com.odcloud.domain.model.File;
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
    FileStorageAdapter adapter;

    @Autowired
    EntityManager entityManager;

    @AfterEach
    void tearDown() {
        entityManager.createQuery("DELETE FROM FileEntity").executeUpdate();
        entityManager.createQuery("DELETE FROM FolderEntity").executeUpdate();
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
            FolderEntity folder = FolderEntity.builder()
                .parentId(null)
                .groupId("test-group")
                .name("테스트 폴더")
                .owner("owner@example.com")
                .path("/test-group")
                .accessLevel("PUBLIC")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(folder);
            entityManager.flush();

            File file = File.builder()
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
            FileEntity savedEntity = entityManager
                .createQuery("SELECT f FROM FileEntity f WHERE f.folderId = :folderId",
                    FileEntity.class)
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
            FolderEntity folder = FolderEntity.builder()
                .parentId(null)
                .groupId("test-group")
                .name("테스트 폴더")
                .owner("owner@example.com")
                .path("/test-group")
                .accessLevel("PUBLIC")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(folder);

            // 기존 파일 생성
            FileEntity existingFile = FileEntity.builder()
                .folderId(folder.getId())
                .fileName("old.txt")
                .fileLoc("/test-group/old_file.txt")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(existingFile);
            entityManager.flush();
            entityManager.clear();

            File updatedFile = File.builder()
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
            FileEntity savedEntity = entityManager.find(FileEntity.class, existingFile.getId());
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
            FolderEntity folder = FolderEntity.builder()
                .parentId(null)
                .groupId("test-group")
                .name("테스트 폴더")
                .owner("owner@example.com")
                .path("/test-group")
                .accessLevel("PUBLIC")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(folder);
            entityManager.flush();

            File file1 = File.builder()
                .folderId(folder.getId())
                .fileName("file1.txt")
                .fileLoc("/test-group/file1.txt")
                .regDt(now)
                .build();

            File file2 = File.builder()
                .folderId(folder.getId())
                .fileName("file2.txt")
                .fileLoc("/test-group/file2.txt")
                .regDt(now)
                .build();

            File file3 = File.builder()
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
                .createQuery("SELECT f FROM FileEntity f WHERE f.folderId = :folderId",
                    FileEntity.class)
                .setParameter("folderId", folder.getId())
                .getResultList();

            assertThat(savedFiles).hasSize(3);
            assertThat(savedFiles)
                .extracting(FileEntity::getFileName)
                .containsExactlyInAnyOrder("file1.txt", "file2.txt", "file3.txt");
        }

        @Test
        @DisplayName("[success] 다양한 파일 확장자를 저장한다")
        void success_variousExtensions() {
            // given
            LocalDateTime now = LocalDateTime.now();

            // 폴더 생성
            FolderEntity folder = FolderEntity.builder()
                .parentId(null)
                .groupId("test-group")
                .name("테스트 폴더")
                .owner("owner@example.com")
                .path("/test-group")
                .accessLevel("PUBLIC")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(folder);
            entityManager.flush();

            File txtFile = File.builder()
                .folderId(folder.getId())
                .fileName("document.txt")
                .fileLoc("/test-group/document.txt")
                .regDt(now)
                .build();

            File pdfFile = File.builder()
                .folderId(folder.getId())
                .fileName("report.pdf")
                .fileLoc("/test-group/report.pdf")
                .regDt(now)
                .build();

            File imageFile = File.builder()
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
                    "SELECT f FROM FileEntity f WHERE f.folderId = :folderId ORDER BY f.fileName",
                    FileEntity.class)
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
            FolderEntity folder1 = FolderEntity.builder()
                .parentId(null)
                .groupId("group-1")
                .name("폴더 1")
                .owner("owner@example.com")
                .path("/group-1")
                .accessLevel("PUBLIC")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(folder1);

            // 폴더 2 생성
            FolderEntity folder2 = FolderEntity.builder()
                .parentId(null)
                .groupId("group-2")
                .name("폴더 2")
                .owner("owner@example.com")
                .path("/group-2")
                .accessLevel("PUBLIC")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(folder2);

            entityManager.flush();

            File fileInFolder1 = File.builder()
                .folderId(folder1.getId())
                .fileName("test.txt")
                .fileLoc("/group-1/test.txt")
                .regDt(now)
                .build();

            File fileInFolder2 = File.builder()
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
                .createQuery("SELECT f FROM FileEntity f WHERE f.folderId = :folderId",
                    FileEntity.class)
                .setParameter("folderId", folder1.getId())
                .getResultList();

            var filesInFolder2 = entityManager
                .createQuery("SELECT f FROM FileEntity f WHERE f.folderId = :folderId",
                    FileEntity.class)
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
            FolderEntity folder = FolderEntity.builder()
                .parentId(null)
                .groupId("test-group")
                .name("테스트 폴더")
                .owner("owner@example.com")
                .path("/test-group")
                .accessLevel("PUBLIC")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(folder);
            entityManager.flush();

            File file = File.builder()
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
            FileEntity savedEntity = entityManager
                .createQuery("SELECT f FROM FileEntity f WHERE f.folderId = :folderId",
                    FileEntity.class)
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
            FolderEntity folder = FolderEntity.builder()
                .parentId(null)
                .groupId("test-group")
                .name("테스트 폴더")
                .owner("owner@example.com")
                .path("/test-group/very/long/nested/folder/path")
                .accessLevel("PUBLIC")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(folder);
            entityManager.flush();

            String longFilePath = "/test-group/very/long/nested/folder/path/12345678_20231201_very_long_filename.txt";
            File file = File.builder()
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
            FileEntity savedEntity = entityManager
                .createQuery("SELECT f FROM FileEntity f WHERE f.folderId = :folderId",
                    FileEntity.class)
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
            FolderEntity folder = FolderEntity.builder()
                .parentId(null)
                .groupId("test-group")
                .name("테스트 폴더")
                .owner("owner@example.com")
                .path("/test-group")
                .accessLevel("PUBLIC")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(folder);
            entityManager.flush();

            File file = File.builder()
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
            FileEntity savedEntity = entityManager
                .createQuery("SELECT f FROM FileEntity f WHERE f.folderId = :folderId",
                    FileEntity.class)
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
            FolderEntity folder = FolderEntity.builder()
                .parentId(null)
                .groupId("group1")
                .name("Test Folder")
                .owner("user@example.com")
                .path("/group1")
                .accessLevel("PUBLIC")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(folder);

            // 파일 생성
            FileEntity file1 = FileEntity.builder()
                .folderId(folder.getId())
                .fileName("test1.txt")
                .fileLoc("/group1/test1.txt")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(file1);

            FileEntity file2 = FileEntity.builder()
                .folderId(folder.getId())
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
                .groups(List.of(com.odcloud.domain.model.Group.of("group1")))
                .build();

            com.odcloud.application.port.in.command.FindFilesCommand command = com.odcloud.application.port.in.command.FindFilesCommand.builder()
                .account(account)
                .folderId(folder.getId())
                .sortType("NAME_ASC")
                .build();

            // when
            var result = adapter.findAll(command);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).extracting(File::getFileName)
                .containsExactly("test1.txt", "test2.txt");
        }

        @Test
        @DisplayName("[success] keyword로 파일을 검색한다")
        void success_searchByKeyword() {
            // given
            LocalDateTime now = LocalDateTime.now();

            // 폴더 생성
            FolderEntity folder = FolderEntity.builder()
                .parentId(null)
                .groupId("group1")
                .name("Test Folder")
                .owner("user@example.com")
                .path("/group1")
                .accessLevel("PUBLIC")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(folder);

            // 파일 생성
            FileEntity file1 = FileEntity.builder()
                .folderId(folder.getId())
                .fileName("report.pdf")
                .fileLoc("/group1/report.pdf")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(file1);

            FileEntity file2 = FileEntity.builder()
                .folderId(folder.getId())
                .fileName("document.txt")
                .fileLoc("/group1/document.txt")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(file2);

            FileEntity file3 = FileEntity.builder()
                .folderId(folder.getId())
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
                .groups(List.of(com.odcloud.domain.model.Group.of("group1")))
                .build();

            com.odcloud.application.port.in.command.FindFilesCommand command = com.odcloud.application.port.in.command.FindFilesCommand.builder()
                .account(account)
                .keyword("report")
                .sortType("NAME_ASC")
                .build();

            // when
            var result = adapter.findAll(command);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).extracting(File::getFileName)
                .contains("report.pdf", "report2.pdf");
        }

        @Test
        @DisplayName("[success] PUBLIC 폴더의 파일만 조회한다")
        void success_onlyPublicFolders() {
            // given
            LocalDateTime now = LocalDateTime.now();

            // PUBLIC 폴더 생성
            FolderEntity publicFolder = FolderEntity.builder()
                .parentId(null)
                .groupId("group1")
                .name("Public Folder")
                .owner("other@example.com")
                .path("/group1/public")
                .accessLevel("PUBLIC")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(publicFolder);

            // PRIVATE 폴더 생성 (다른 소유자)
            FolderEntity privateFolder = FolderEntity.builder()
                .parentId(null)
                .groupId("group1")
                .name("Private Folder")
                .owner("other@example.com")
                .path("/group1/private")
                .accessLevel("PRIVATE")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(privateFolder);

            // PUBLIC 폴더에 파일 생성
            FileEntity publicFile = FileEntity.builder()
                .folderId(publicFolder.getId())
                .fileName("public.txt")
                .fileLoc("/group1/public/public.txt")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(publicFile);

            // PRIVATE 폴더에 파일 생성
            FileEntity privateFile = FileEntity.builder()
                .folderId(privateFolder.getId())
                .fileName("private.txt")
                .fileLoc("/group1/private/private.txt")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(privateFile);

            entityManager.flush();
            entityManager.clear();

            com.odcloud.domain.model.Account account = com.odcloud.domain.model.Account.builder()
                .id(1L)
                .email("user@example.com")
                .groups(List.of(com.odcloud.domain.model.Group.of("group1")))
                .build();

            com.odcloud.application.port.in.command.FindFilesCommand command = com.odcloud.application.port.in.command.FindFilesCommand.builder()
                .account(account)
                .folderId(publicFolder.getId())
                .sortType("NAME_ASC")
                .build();

            // when
            var result = adapter.findAll(command);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getFileName()).isEqualTo("public.txt");
        }

        @Test
        @DisplayName("[success] 소유자는 자신의 PRIVATE 폴더 파일을 조회할 수 있다")
        void success_ownerCanSeePrivateFiles() {
            // given
            LocalDateTime now = LocalDateTime.now();

            // PUBLIC 폴더 생성
            FolderEntity publicFolder = FolderEntity.builder()
                .parentId(null)
                .groupId("group1")
                .name("Public Folder")
                .owner("owner@example.com")
                .path("/group1/public")
                .accessLevel("PUBLIC")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(publicFolder);

            // PRIVATE 폴더 생성 (동일 소유자)
            FolderEntity privateFolder = FolderEntity.builder()
                .parentId(null)
                .groupId("group1")
                .name("Private Folder")
                .owner("owner@example.com")
                .path("/group1/private")
                .accessLevel("PRIVATE")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(privateFolder);

            // PUBLIC 폴더에 파일 생성
            FileEntity publicFile = FileEntity.builder()
                .folderId(publicFolder.getId())
                .fileName("public.txt")
                .fileLoc("/group1/public/public.txt")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(publicFile);

            // PRIVATE 폴더에 파일 생성
            FileEntity privateFile = FileEntity.builder()
                .folderId(privateFolder.getId())
                .fileName("private.txt")
                .fileLoc("/group1/private/private.txt")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(privateFile);

            entityManager.flush();
            entityManager.clear();

            com.odcloud.domain.model.Account account = com.odcloud.domain.model.Account.builder()
                .id(1L)
                .email("owner@example.com")
                .groups(List.of(com.odcloud.domain.model.Group.of("group1")))
                .build();

            com.odcloud.application.port.in.command.FindFilesCommand command = com.odcloud.application.port.in.command.FindFilesCommand.builder()
                .account(account)
                .folderId(privateFolder.getId())
                .sortType("NAME_ASC")
                .build();

            // when
            var result = adapter.findAll(command);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getFileName()).isEqualTo("private.txt");
        }

        @Test
        @DisplayName("[success] 정렬 순서를 적용한다")
        void success_withSorting() {
            // given
            LocalDateTime now = LocalDateTime.now();

            // 폴더 생성
            FolderEntity folder = FolderEntity.builder()
                .parentId(null)
                .groupId("group1")
                .name("Test Folder")
                .owner("user@example.com")
                .path("/group1")
                .accessLevel("PUBLIC")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(folder);

            // 파일 생성 (역순으로)
            FileEntity file1 = FileEntity.builder()
                .folderId(folder.getId())
                .fileName("c.txt")
                .fileLoc("/group1/c.txt")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(file1);

            FileEntity file2 = FileEntity.builder()
                .folderId(folder.getId())
                .fileName("a.txt")
                .fileLoc("/group1/a.txt")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(file2);

            FileEntity file3 = FileEntity.builder()
                .folderId(folder.getId())
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
                .groups(List.of(com.odcloud.domain.model.Group.of("group1")))
                .build();

            com.odcloud.application.port.in.command.FindFilesCommand command = com.odcloud.application.port.in.command.FindFilesCommand.builder()
                .account(account)
                .folderId(folder.getId())
                .sortType("NAME_DESC")
                .build();

            // when
            var result = adapter.findAll(command);

            // then
            assertThat(result).hasSize(3);
            assertThat(result).extracting(File::getFileName)
                .containsExactly("c.txt", "b.txt", "a.txt");
        }

        @Test
        @DisplayName("[success] keyword 검색 시 권한이 없는 폴더의 파일은 제외된다")
        void success_keywordSearchExcludesUnauthorizedFolderFiles() {
            // given
            LocalDateTime now = LocalDateTime.now();

            // PUBLIC 폴더 생성 (group1)
            FolderEntity publicFolder = FolderEntity.builder()
                .parentId(null)
                .groupId("group1")
                .name("Public Folder")
                .owner("owner@example.com")
                .path("/group1/public")
                .accessLevel("PUBLIC")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(publicFolder);

            // PRIVATE 폴더 생성 (다른 사용자 소유)
            FolderEntity privateFolder = FolderEntity.builder()
                .parentId(null)
                .groupId("group1")
                .name("Private Folder")
                .owner("other@example.com")
                .path("/group1/private")
                .accessLevel("PRIVATE")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(privateFolder);

            // PUBLIC 폴더에 "test" 키워드 파일 생성
            FileEntity publicTestFile = FileEntity.builder()
                .folderId(publicFolder.getId())
                .fileName("test-public.txt")
                .fileLoc("/group1/public/test-public.txt")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(publicTestFile);

            // PRIVATE 폴더에 "test" 키워드 파일 생성 (권한 없는 폴더)
            FileEntity privateTestFile = FileEntity.builder()
                .folderId(privateFolder.getId())
                .fileName("test-private.txt")
                .fileLoc("/group1/private/test-private.txt")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(privateTestFile);

            entityManager.flush();
            entityManager.clear();

            // 권한 없는 사용자 (PRIVATE 폴더의 owner가 아님)
            com.odcloud.domain.model.Account account = com.odcloud.domain.model.Account.builder()
                .id(1L)
                .email("user@example.com")
                .groups(List.of(com.odcloud.domain.model.Group.of("group1")))
                .build();

            com.odcloud.application.port.in.command.FindFilesCommand command = com.odcloud.application.port.in.command.FindFilesCommand.builder()
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
            FolderEntity publicFolder = FolderEntity.builder()
                .parentId(null)
                .groupId("group1")
                .name("Public Folder")
                .owner("owner@example.com")
                .path("/group1/public")
                .accessLevel("PUBLIC")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(publicFolder);

            // 자신의 PRIVATE 폴더 생성
            FolderEntity myPrivateFolder = FolderEntity.builder()
                .parentId(null)
                .groupId("group1")
                .name("My Private Folder")
                .owner("user@example.com")
                .path("/group1/my-private")
                .accessLevel("PRIVATE")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(myPrivateFolder);

            // PUBLIC 폴더에 파일 생성
            FileEntity publicFile = FileEntity.builder()
                .folderId(publicFolder.getId())
                .fileName("report-public.pdf")
                .fileLoc("/group1/public/report-public.pdf")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(publicFile);

            // 자신의 PRIVATE 폴더에 파일 생성
            FileEntity myPrivateFile = FileEntity.builder()
                .folderId(myPrivateFolder.getId())
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
                .groups(List.of(com.odcloud.domain.model.Group.of("group1")))
                .build();

            com.odcloud.application.port.in.command.FindFilesCommand command = com.odcloud.application.port.in.command.FindFilesCommand.builder()
                .account(account)
                .keyword("report")
                .sortType("NAME_ASC")
                .build();

            // when
            var result = adapter.findAll(command);

            // then
            // PUBLIC 폴더와 자신의 PRIVATE 폴더의 파일 모두 조회되어야 함
            assertThat(result).hasSize(2);
            assertThat(result).extracting(File::getFileName)
                .containsExactlyInAnyOrder("report-public.pdf", "report-private.pdf");
        }
    }
}
