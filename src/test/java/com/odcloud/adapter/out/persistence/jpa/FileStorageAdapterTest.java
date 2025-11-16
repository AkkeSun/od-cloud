package com.odcloud.adapter.out.persistence.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.IntegrationTestSupport;
import com.odcloud.domain.model.File;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
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
}
