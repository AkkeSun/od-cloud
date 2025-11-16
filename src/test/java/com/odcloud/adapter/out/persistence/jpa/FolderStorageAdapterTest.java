package com.odcloud.adapter.out.persistence.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.IntegrationTestSupport;
import com.odcloud.domain.model.Folder;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class FolderStorageAdapterTest extends IntegrationTestSupport {

    @Autowired
    FolderStorageAdapter adapter;

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
    @DisplayName("[save] 폴더를 저장하는 메소드")
    class Describe_save {

        @Test
        @DisplayName("[success] 신규 루트 폴더를 저장한다")
        void success_rootFolder() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Folder folder = Folder.builder()
                .parentId(null)
                .groupId("test-group")
                .name("루트 폴더")
                .owner("owner@example.com")
                .path("/test-group")
                .accessLevel("PUBLIC")
                .regDt(now)
                .build();

            // when
            adapter.save(folder);
            entityManager.flush();
            entityManager.clear();

            // then
            var savedFolders = entityManager
                .createQuery("SELECT f FROM FolderEntity f WHERE f.groupId = :groupId",
                    FolderEntity.class)
                .setParameter("groupId", "test-group")
                .getResultList();

            assertThat(savedFolders).hasSize(1);
            assertThat(savedFolders.get(0).getParentId()).isNull();
            assertThat(savedFolders.get(0).getGroupId()).isEqualTo("test-group");
            assertThat(savedFolders.get(0).getName()).isEqualTo("루트 폴더");
            assertThat(savedFolders.get(0).getOwner()).isEqualTo("owner@example.com");
            assertThat(savedFolders.get(0).getPath()).isEqualTo("/test-group");
            assertThat(savedFolders.get(0).getAccessLevel()).isEqualTo("PUBLIC");
        }

        @Test
        @DisplayName("[success] 신규 서브 폴더를 저장한다")
        void success_subFolder() {
            // given
            LocalDateTime now = LocalDateTime.now();

            // 부모 폴더 생성
            FolderEntity parentFolder = FolderEntity.builder()
                .parentId(null)
                .groupId("test-group")
                .name("부모 폴더")
                .owner("owner@example.com")
                .path("/test-group")
                .accessLevel("PUBLIC")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(parentFolder);
            entityManager.flush();

            Folder subFolder = Folder.builder()
                .parentId(parentFolder.getId())
                .groupId("test-group")
                .name("서브 폴더")
                .owner("owner@example.com")
                .path("/test-group/subfolder")
                .accessLevel("PUBLIC")
                .regDt(now)
                .build();

            // when
            adapter.save(subFolder);
            entityManager.flush();
            entityManager.clear();

            // then
            var savedFolders = entityManager
                .createQuery("SELECT f FROM FolderEntity f WHERE f.parentId = :parentId",
                    FolderEntity.class)
                .setParameter("parentId", parentFolder.getId())
                .getResultList();

            assertThat(savedFolders).hasSize(1);
            assertThat(savedFolders.get(0).getParentId()).isEqualTo(parentFolder.getId());
            assertThat(savedFolders.get(0).getName()).isEqualTo("서브 폴더");
            assertThat(savedFolders.get(0).getPath()).isEqualTo("/test-group/subfolder");
        }

        @Test
        @DisplayName("[success] 기존 폴더를 업데이트한다")
        void success_update() {
            // given
            LocalDateTime now = LocalDateTime.now();
            FolderEntity existingFolder = FolderEntity.builder()
                .parentId(null)
                .groupId("test-group")
                .name("기존 폴더")
                .owner("owner@example.com")
                .path("/test-group")
                .accessLevel("PUBLIC")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(existingFolder);
            entityManager.flush();
            entityManager.clear();

            Folder updatedFolder = Folder.builder()
                .id(existingFolder.getId())
                .parentId(null)
                .groupId("test-group")
                .name("업데이트된 폴더")
                .owner("owner@example.com")
                .path("/test-group")
                .accessLevel("PRIVATE")
                .regDt(now)
                .modDt(now.plusHours(1))
                .build();

            // when
            adapter.save(updatedFolder);
            entityManager.flush();
            entityManager.clear();

            // then
            FolderEntity savedEntity = entityManager.find(FolderEntity.class,
                existingFolder.getId());
            assertThat(savedEntity).isNotNull();
            assertThat(savedEntity.getName()).isEqualTo("업데이트된 폴더");
            assertThat(savedEntity.getAccessLevel()).isEqualTo("PRIVATE");
        }

        @Test
        @DisplayName("[success] 여러 서브 폴더를 저장한다")
        void success_multipleSubFolders() {
            // given
            LocalDateTime now = LocalDateTime.now();

            // 부모 폴더 생성
            FolderEntity parentFolder = FolderEntity.builder()
                .parentId(null)
                .groupId("test-group")
                .name("부모 폴더")
                .owner("owner@example.com")
                .path("/test-group")
                .accessLevel("PUBLIC")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(parentFolder);
            entityManager.flush();

            Folder subFolder1 = Folder.builder()
                .parentId(parentFolder.getId())
                .groupId("test-group")
                .name("서브 폴더 1")
                .owner("owner@example.com")
                .path("/test-group/sub1")
                .accessLevel("PUBLIC")
                .regDt(now)
                .build();

            Folder subFolder2 = Folder.builder()
                .parentId(parentFolder.getId())
                .groupId("test-group")
                .name("서브 폴더 2")
                .owner("owner@example.com")
                .path("/test-group/sub2")
                .accessLevel("PUBLIC")
                .regDt(now)
                .build();

            Folder subFolder3 = Folder.builder()
                .parentId(parentFolder.getId())
                .groupId("test-group")
                .name("서브 폴더 3")
                .owner("owner@example.com")
                .path("/test-group/sub3")
                .accessLevel("PUBLIC")
                .regDt(now)
                .build();

            // when
            adapter.save(subFolder1);
            adapter.save(subFolder2);
            adapter.save(subFolder3);
            entityManager.flush();
            entityManager.clear();

            // then
            var savedFolders = entityManager
                .createQuery(
                    "SELECT f FROM FolderEntity f WHERE f.parentId = :parentId ORDER BY f.name",
                    FolderEntity.class)
                .setParameter("parentId", parentFolder.getId())
                .getResultList();

            assertThat(savedFolders).hasSize(3);
            assertThat(savedFolders)
                .extracting(FolderEntity::getName)
                .containsExactly("서브 폴더 1", "서브 폴더 2", "서브 폴더 3");
        }

        @Test
        @DisplayName("[success] 다양한 접근 레벨의 폴더를 저장한다")
        void success_variousAccessLevels() {
            // given
            LocalDateTime now = LocalDateTime.now();

            Folder publicFolder = Folder.builder()
                .parentId(null)
                .groupId("test-group")
                .name("공개 폴더")
                .owner("owner@example.com")
                .path("/test-group/public")
                .accessLevel("PUBLIC")
                .regDt(now)
                .build();

            Folder privateFolder = Folder.builder()
                .parentId(null)
                .groupId("test-group")
                .name("비공개 폴더")
                .owner("owner@example.com")
                .path("/test-group/private")
                .accessLevel("PRIVATE")
                .regDt(now)
                .build();

            // when
            adapter.save(publicFolder);
            adapter.save(privateFolder);
            entityManager.flush();
            entityManager.clear();

            // then
            var savedFolders = entityManager
                .createQuery(
                    "SELECT f FROM FolderEntity f WHERE f.groupId = :groupId ORDER BY f.accessLevel",
                    FolderEntity.class)
                .setParameter("groupId", "test-group")
                .getResultList();

            assertThat(savedFolders).hasSize(2);
            assertThat(savedFolders.get(0).getAccessLevel()).isEqualTo("PRIVATE");
            assertThat(savedFolders.get(1).getAccessLevel()).isEqualTo("PUBLIC");
        }

        @Test
        @DisplayName("[success] null modDt로 폴더를 저장한다")
        void success_nullModDt() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Folder folder = Folder.builder()
                .parentId(null)
                .groupId("test-group")
                .name("테스트 폴더")
                .owner("owner@example.com")
                .path("/test-group")
                .accessLevel("PUBLIC")
                .regDt(now)
                .modDt(null)
                .build();

            // when
            adapter.save(folder);
            entityManager.flush();
            entityManager.clear();

            // then
            var savedFolders = entityManager
                .createQuery("SELECT f FROM FolderEntity f WHERE f.groupId = :groupId",
                    FolderEntity.class)
                .setParameter("groupId", "test-group")
                .getResultList();

            assertThat(savedFolders).hasSize(1);
            assertThat(savedFolders.get(0).getModDt()).isNull();
        }
    }

    @Nested
    @DisplayName("[findById] ID로 폴더를 조회하는 메소드")
    class Describe_findById {

        @Test
        @DisplayName("[success] ID로 폴더를 조회한다")
        void success() {
            // given
            LocalDateTime now = LocalDateTime.now();
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
            entityManager.clear();

            // when
            Folder result = adapter.findById(folder.getId());

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(folder.getId());
            assertThat(result.getGroupId()).isEqualTo("test-group");
            assertThat(result.getName()).isEqualTo("테스트 폴더");
            assertThat(result.getOwner()).isEqualTo("owner@example.com");
            assertThat(result.getPath()).isEqualTo("/test-group");
            assertThat(result.getAccessLevel()).isEqualTo("PUBLIC");
        }

        @Test
        @DisplayName("[success] 서브 폴더를 조회한다")
        void success_subFolder() {
            // given
            LocalDateTime now = LocalDateTime.now();

            // 부모 폴더 생성
            FolderEntity parentFolder = FolderEntity.builder()
                .parentId(null)
                .groupId("test-group")
                .name("부모 폴더")
                .owner("owner@example.com")
                .path("/test-group")
                .accessLevel("PUBLIC")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(parentFolder);

            // 서브 폴더 생성
            FolderEntity subFolder = FolderEntity.builder()
                .parentId(parentFolder.getId())
                .groupId("test-group")
                .name("서브 폴더")
                .owner("owner@example.com")
                .path("/test-group/subfolder")
                .accessLevel("PUBLIC")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(subFolder);
            entityManager.flush();
            entityManager.clear();

            // when
            Folder result = adapter.findById(subFolder.getId());

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(subFolder.getId());
            assertThat(result.getParentId()).isEqualTo(parentFolder.getId());
            assertThat(result.getName()).isEqualTo("서브 폴더");
            assertThat(result.getPath()).isEqualTo("/test-group/subfolder");
        }

        @Test
        @DisplayName("[error] 존재하지 않는 ID로 조회 시 예외를 발생시킨다")
        void error_notFound() {
            // when & then
            org.junit.jupiter.api.Assertions.assertThrows(
                com.odcloud.infrastructure.exception.CustomBusinessException.class,
                () -> adapter.findById(999999L)
            );
        }

        @Test
        @DisplayName("[success] 여러 폴더 중 특정 ID만 조회한다")
        void success_specificFolder() {
            // given
            LocalDateTime now = LocalDateTime.now();

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
            entityManager.clear();

            // when
            Folder result = adapter.findById(folder1.getId());

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(folder1.getId());
            assertThat(result.getGroupId()).isEqualTo("group-1");
            assertThat(result.getName()).isEqualTo("폴더 1");
        }
    }

    @Nested
    @DisplayName("[existsSameFolderName] 같은 부모 폴더 내 동일 이름 존재 여부를 확인하는 메소드")
    class Describe_existsSameFolderName {

        @Test
        @DisplayName("[success] 같은 이름의 폴더가 존재하면 true를 반환한다")
        void success_exists() {
            // given
            LocalDateTime now = LocalDateTime.now();

            // 부모 폴더 생성
            FolderEntity parentFolder = FolderEntity.builder()
                .parentId(null)
                .groupId("test-group")
                .name("부모 폴더")
                .owner("owner@example.com")
                .path("/test-group")
                .accessLevel("PUBLIC")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(parentFolder);

            // 서브 폴더 생성
            FolderEntity subFolder = FolderEntity.builder()
                .parentId(parentFolder.getId())
                .groupId("test-group")
                .name("기존 폴더")
                .owner("owner@example.com")
                .path("/test-group/existing")
                .accessLevel("PUBLIC")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(subFolder);
            entityManager.flush();
            entityManager.clear();

            // when
            boolean result = adapter.existsSameFolderName(parentFolder.getId(), "기존 폴더");

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("[success] 같은 이름의 폴더가 없으면 false를 반환한다")
        void success_notExists() {
            // given
            LocalDateTime now = LocalDateTime.now();

            // 부모 폴더 생성
            FolderEntity parentFolder = FolderEntity.builder()
                .parentId(null)
                .groupId("test-group")
                .name("부모 폴더")
                .owner("owner@example.com")
                .path("/test-group")
                .accessLevel("PUBLIC")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(parentFolder);
            entityManager.flush();
            entityManager.clear();

            // when
            boolean result = adapter.existsSameFolderName(parentFolder.getId(), "존재하지않는폴더");

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("[success] 다른 부모 폴더에 같은 이름이 있어도 false를 반환한다")
        void success_differentParent() {
            // given
            LocalDateTime now = LocalDateTime.now();

            // 부모 폴더 1 생성
            FolderEntity parentFolder1 = FolderEntity.builder()
                .parentId(null)
                .groupId("group-1")
                .name("부모 폴더 1")
                .owner("owner@example.com")
                .path("/group-1")
                .accessLevel("PUBLIC")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(parentFolder1);

            // 부모 폴더 2 생성
            FolderEntity parentFolder2 = FolderEntity.builder()
                .parentId(null)
                .groupId("group-2")
                .name("부모 폴더 2")
                .owner("owner@example.com")
                .path("/group-2")
                .accessLevel("PUBLIC")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(parentFolder2);

            // 부모 폴더 1 아래 서브 폴더 생성
            FolderEntity subFolder = FolderEntity.builder()
                .parentId(parentFolder1.getId())
                .groupId("group-1")
                .name("공통 이름")
                .owner("owner@example.com")
                .path("/group-1/common")
                .accessLevel("PUBLIC")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(subFolder);
            entityManager.flush();
            entityManager.clear();

            // when - 부모 폴더 2 아래에서 같은 이름 확인
            boolean result = adapter.existsSameFolderName(parentFolder2.getId(), "공통 이름");

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("[success] 같은 부모 아래 여러 폴더 중 특정 이름을 확인한다")
        void success_multipleFolders() {
            // given
            LocalDateTime now = LocalDateTime.now();

            // 부모 폴더 생성
            FolderEntity parentFolder = FolderEntity.builder()
                .parentId(null)
                .groupId("test-group")
                .name("부모 폴더")
                .owner("owner@example.com")
                .path("/test-group")
                .accessLevel("PUBLIC")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(parentFolder);

            // 여러 서브 폴더 생성
            entityManager.persist(FolderEntity.builder()
                .parentId(parentFolder.getId())
                .groupId("test-group")
                .name("폴더 A")
                .owner("owner@example.com")
                .path("/test-group/a")
                .accessLevel("PUBLIC")
                .regDt(now)
                .modDt(now)
                .build());

            entityManager.persist(FolderEntity.builder()
                .parentId(parentFolder.getId())
                .groupId("test-group")
                .name("폴더 B")
                .owner("owner@example.com")
                .path("/test-group/b")
                .accessLevel("PUBLIC")
                .regDt(now)
                .modDt(now)
                .build());

            entityManager.persist(FolderEntity.builder()
                .parentId(parentFolder.getId())
                .groupId("test-group")
                .name("폴더 C")
                .owner("owner@example.com")
                .path("/test-group/c")
                .accessLevel("PUBLIC")
                .regDt(now)
                .modDt(now)
                .build());

            entityManager.flush();
            entityManager.clear();

            // when & then
            assertThat(adapter.existsSameFolderName(parentFolder.getId(), "폴더 A")).isTrue();
            assertThat(adapter.existsSameFolderName(parentFolder.getId(), "폴더 B")).isTrue();
            assertThat(adapter.existsSameFolderName(parentFolder.getId(), "폴더 C")).isTrue();
            assertThat(adapter.existsSameFolderName(parentFolder.getId(), "폴더 D")).isFalse();
        }

        @Test
        @DisplayName("[success] 대소문자를 구분하여 확인한다")
        void success_caseSensitive() {
            // given
            LocalDateTime now = LocalDateTime.now();

            // 부모 폴더 생성
            FolderEntity parentFolder = FolderEntity.builder()
                .parentId(null)
                .groupId("test-group")
                .name("부모 폴더")
                .owner("owner@example.com")
                .path("/test-group")
                .accessLevel("PUBLIC")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(parentFolder);

            // 서브 폴더 생성
            FolderEntity subFolder = FolderEntity.builder()
                .parentId(parentFolder.getId())
                .groupId("test-group")
                .name("TestFolder")
                .owner("owner@example.com")
                .path("/test-group/test")
                .accessLevel("PUBLIC")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(subFolder);
            entityManager.flush();
            entityManager.clear();

            // when & then
            assertThat(adapter.existsSameFolderName(parentFolder.getId(), "TestFolder")).isTrue();
            assertThat(adapter.existsSameFolderName(parentFolder.getId(), "testfolder")).isFalse();
            assertThat(adapter.existsSameFolderName(parentFolder.getId(), "TESTFOLDER")).isFalse();
        }
    }
}
