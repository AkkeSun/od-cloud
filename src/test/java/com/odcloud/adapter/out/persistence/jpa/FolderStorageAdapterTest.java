package com.odcloud.adapter.out.persistence.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.IntegrationTestSupport;
import com.odcloud.application.file.port.in.command.FindFilesCommand;
import com.odcloud.domain.model.FolderInfo;
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
class FolderStorageAdapterTest extends IntegrationTestSupport {

    @Autowired
    FolderInfoStorageAdapter adapter;

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
    @DisplayName("[save] 폴더를 저장하는 메소드")
    class Describe_save {

        @Test
        @DisplayName("[success] 신규 루트 폴더를 저장한다")
        void success_rootFolder() {
            // given
            LocalDateTime now = LocalDateTime.now();
            FolderInfo folder = FolderInfo.builder()
                .parentId(null)
                .groupId("test-group")
                .name("루트 폴더")
                .owner("owner@example.com")
                .path("/test-group")
                .regDt(now)
                .build();

            // when
            adapter.save(folder);
            entityManager.flush();
            entityManager.clear();

            // then
            var savedFolders = entityManager
                .createQuery("SELECT f FROM FolderInfoEntity f WHERE f.groupId = :groupId",
                    FolderInfoEntity.class)
                .setParameter("groupId", "test-group")
                .getResultList();

            assertThat(savedFolders).hasSize(1);
            assertThat(savedFolders.get(0).getParentId()).isNull();
            assertThat(savedFolders.get(0).getGroupId()).isEqualTo("test-group");
            assertThat(savedFolders.get(0).getName()).isEqualTo("루트 폴더");
            assertThat(savedFolders.get(0).getOwner()).isEqualTo("owner@example.com");
            assertThat(savedFolders.get(0).getPath()).isEqualTo("/test-group");
        }

        @Test
        @DisplayName("[success] 신규 서브 폴더를 저장한다")
        void success_subFolder() {
            // given
            LocalDateTime now = LocalDateTime.now();

            // 부모 폴더 생성
            FolderInfoEntity parentFolder = FolderInfoEntity.builder()
                .parentId(null)
                .groupId("test-group")
                .name("부모 폴더")
                .owner("owner@example.com")
                .path("/test-group")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(parentFolder);
            entityManager.flush();

            FolderInfo subFolder = FolderInfo.builder()
                .parentId(parentFolder.getId())
                .groupId("test-group")
                .name("서브 폴더")
                .owner("owner@example.com")
                .path("/test-group/subfolder")
                .regDt(now)
                .build();

            // when
            adapter.save(subFolder);
            entityManager.flush();
            entityManager.clear();

            // then
            var savedFolders = entityManager
                .createQuery("SELECT f FROM FolderInfoEntity f WHERE f.parentId = :parentId",
                    FolderInfoEntity.class)
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
            FolderInfoEntity existingFolder = FolderInfoEntity.builder()
                .parentId(null)
                .groupId("test-group")
                .name("기존 폴더")
                .owner("owner@example.com")
                .path("/test-group")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(existingFolder);
            entityManager.flush();
            entityManager.clear();

            FolderInfo updatedFolder = FolderInfo.builder()
                .id(existingFolder.getId())
                .parentId(null)
                .groupId("test-group")
                .name("업데이트된 폴더")
                .owner("owner@example.com")
                .path("/test-group")
                .regDt(now)
                .modDt(now.plusHours(1))
                .build();

            // when
            adapter.save(updatedFolder);
            entityManager.flush();
            entityManager.clear();

            // then
            FolderInfoEntity savedEntity = entityManager.find(FolderInfoEntity.class,
                existingFolder.getId());
            assertThat(savedEntity).isNotNull();
            assertThat(savedEntity.getName()).isEqualTo("업데이트된 폴더");
        }

        @Test
        @DisplayName("[success] 여러 서브 폴더를 저장한다")
        void success_multipleSubFolders() {
            // given
            LocalDateTime now = LocalDateTime.now();

            // 부모 폴더 생성
            FolderInfoEntity parentFolder = FolderInfoEntity.builder()
                .parentId(null)
                .groupId("test-group")
                .name("부모 폴더")
                .owner("owner@example.com")
                .path("/test-group")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(parentFolder);
            entityManager.flush();

            FolderInfo subFolder1 = FolderInfo.builder()
                .parentId(parentFolder.getId())
                .groupId("test-group")
                .name("서브 폴더 1")
                .owner("owner@example.com")
                .path("/test-group/sub1")
                .regDt(now)
                .build();

            FolderInfo subFolder2 = FolderInfo.builder()
                .parentId(parentFolder.getId())
                .groupId("test-group")
                .name("서브 폴더 2")
                .owner("owner@example.com")
                .path("/test-group/sub2")
                .regDt(now)
                .build();

            FolderInfo subFolder3 = FolderInfo.builder()
                .parentId(parentFolder.getId())
                .groupId("test-group")
                .name("서브 폴더 3")
                .owner("owner@example.com")
                .path("/test-group/sub3")
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
                    "SELECT f FROM FolderInfoEntity f WHERE f.parentId = :parentId ORDER BY f.name",
                    FolderInfoEntity.class)
                .setParameter("parentId", parentFolder.getId())
                .getResultList();

            assertThat(savedFolders).hasSize(3);
            assertThat(savedFolders)
                .extracting(FolderInfoEntity::getName)
                .containsExactly("서브 폴더 1", "서브 폴더 2", "서브 폴더 3");
        }

        @Test
        @DisplayName("[success] null modDt로 폴더를 저장한다")
        void success_nullModDt() {
            // given
            LocalDateTime now = LocalDateTime.now();
            FolderInfo folder = FolderInfo.builder()
                .parentId(null)
                .groupId("test-group")
                .name("테스트 폴더")
                .owner("owner@example.com")
                .path("/test-group")
                .regDt(now)
                .modDt(null)
                .build();

            // when
            adapter.save(folder);
            entityManager.flush();
            entityManager.clear();

            // then
            var savedFolders = entityManager
                .createQuery("SELECT f FROM FolderInfoEntity f WHERE f.groupId = :groupId",
                    FolderInfoEntity.class)
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
            FolderInfoEntity folder = FolderInfoEntity.builder()
                .parentId(null)
                .groupId("test-group")
                .name("테스트 폴더")
                .owner("owner@example.com")
                .path("/test-group")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(folder);
            entityManager.flush();
            entityManager.clear();

            // when
            FolderInfo result = adapter.findById(folder.getId());

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(folder.getId());
            assertThat(result.getGroupId()).isEqualTo("test-group");
            assertThat(result.getName()).isEqualTo("테스트 폴더");
            assertThat(result.getOwner()).isEqualTo("owner@example.com");
            assertThat(result.getPath()).isEqualTo("/test-group");
        }

        @Test
        @DisplayName("[success] 서브 폴더를 조회한다")
        void success_subFolder() {
            // given
            LocalDateTime now = LocalDateTime.now();

            // 부모 폴더 생성
            FolderInfoEntity parentFolder = FolderInfoEntity.builder()
                .parentId(null)
                .groupId("test-group")
                .name("부모 폴더")
                .owner("owner@example.com")
                .path("/test-group")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(parentFolder);

            // 서브 폴더 생성
            FolderInfoEntity subFolder = FolderInfoEntity.builder()
                .parentId(parentFolder.getId())
                .groupId("test-group")
                .name("서브 폴더")
                .owner("owner@example.com")
                .path("/test-group/subfolder")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(subFolder);
            entityManager.flush();
            entityManager.clear();

            // when
            FolderInfo result = adapter.findById(subFolder.getId());

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

            FolderInfoEntity folder1 = FolderInfoEntity.builder()
                .parentId(null)
                .groupId("group-1")
                .name("폴더 1")
                .owner("owner@example.com")
                .path("/group-1")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(folder1);

            FolderInfoEntity folder2 = FolderInfoEntity.builder()
                .parentId(null)
                .groupId("group-2")
                .name("폴더 2")
                .owner("owner@example.com")
                .path("/group-2")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(folder2);

            entityManager.flush();
            entityManager.clear();

            // when
            FolderInfo result = adapter.findById(folder1.getId());

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
            FolderInfoEntity parentFolder = FolderInfoEntity.builder()
                .parentId(null)
                .groupId("test-group")
                .name("부모 폴더")
                .owner("owner@example.com")
                .path("/test-group")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(parentFolder);

            // 서브 폴더 생성
            FolderInfoEntity subFolder = FolderInfoEntity.builder()
                .parentId(parentFolder.getId())
                .groupId("test-group")
                .name("기존 폴더")
                .owner("owner@example.com")
                .path("/test-group/existing")
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
            FolderInfoEntity parentFolder = FolderInfoEntity.builder()
                .parentId(null)
                .groupId("test-group")
                .name("부모 폴더")
                .owner("owner@example.com")
                .path("/test-group")
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
            FolderInfoEntity parentFolder1 = FolderInfoEntity.builder()
                .parentId(null)
                .groupId("group-1")
                .name("부모 폴더 1")
                .owner("owner@example.com")
                .path("/group-1")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(parentFolder1);

            // 부모 폴더 2 생성
            FolderInfoEntity parentFolder2 = FolderInfoEntity.builder()
                .parentId(null)
                .groupId("group-2")
                .name("부모 폴더 2")
                .owner("owner@example.com")
                .path("/group-2")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(parentFolder2);

            // 부모 폴더 1 아래 서브 폴더 생성
            FolderInfoEntity subFolder = FolderInfoEntity.builder()
                .parentId(parentFolder1.getId())
                .groupId("group-1")
                .name("공통 이름")
                .owner("owner@example.com")
                .path("/group-1/common")
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
            FolderInfoEntity parentFolder = FolderInfoEntity.builder()
                .parentId(null)
                .groupId("test-group")
                .name("부모 폴더")
                .owner("owner@example.com")
                .path("/test-group")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(parentFolder);

            // 여러 서브 폴더 생성
            entityManager.persist(FolderInfoEntity.builder()
                .parentId(parentFolder.getId())
                .groupId("test-group")
                .name("폴더 A")
                .owner("owner@example.com")
                .path("/test-group/a")
                .regDt(now)
                .modDt(now)
                .build());

            entityManager.persist(FolderInfoEntity.builder()
                .parentId(parentFolder.getId())
                .groupId("test-group")
                .name("폴더 B")
                .owner("owner@example.com")
                .path("/test-group/b")
                .regDt(now)
                .modDt(now)
                .build());

            entityManager.persist(FolderInfoEntity.builder()
                .parentId(parentFolder.getId())
                .groupId("test-group")
                .name("폴더 C")
                .owner("owner@example.com")
                .path("/test-group/c")
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
            FolderInfoEntity parentFolder = FolderInfoEntity.builder()
                .parentId(null)
                .groupId("test-group")
                .name("부모 폴더")
                .owner("owner@example.com")
                .path("/test-group")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(parentFolder);

            // 서브 폴더 생성
            FolderInfoEntity subFolder = FolderInfoEntity.builder()
                .parentId(parentFolder.getId())
                .groupId("test-group")
                .name("TestFolder")
                .owner("owner@example.com")
                .path("/test-group/test")
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

    @Nested
    @DisplayName("[findAll] 폴더 목록을 조회하는 메소드")
    class Describe_findAll {

        @Test
        @DisplayName("[success] parentId로 하위 폴더 목록을 조회한다")
        void success_findByParentId() {
            // given
            LocalDateTime now = LocalDateTime.now();

            // 부모 폴더 생성
            FolderInfoEntity parentFolder = FolderInfoEntity.builder()
                .parentId(null)
                .groupId("group1")
                .name("Parent Folder")
                .owner("user@example.com")
                .path("/group1")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(parentFolder);

            // 하위 폴더 생성
            FolderInfoEntity child1 = FolderInfoEntity.builder()
                .parentId(parentFolder.getId())
                .groupId("group1")
                .name("Child 1")
                .owner("user@example.com")
                .path("/group1/child1")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(child1);

            FolderInfoEntity child2 = FolderInfoEntity.builder()
                .parentId(parentFolder.getId())
                .groupId("group1")
                .name("Child 2")
                .owner("user@example.com")
                .path("/group1/child2")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(child2);

            entityManager.flush();
            entityManager.clear();

            com.odcloud.domain.model.Account account = com.odcloud.domain.model.Account.builder()
                .id(1L)
                .email("user@example.com")
                .groups(List.of(com.odcloud.domain.model.Group.of("group1")))
                .build();

            FindFilesCommand command = FindFilesCommand.builder()
                .account(account)
                .folderId(parentFolder.getId())
                .sortType("NAME_ASC")
                .build();

            // when
            var result = adapter.findAll(command);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).extracting(FolderInfo::getName)
                .containsExactly("Child 1", "Child 2");
        }

        @Test
        @DisplayName("[success] keyword로 폴더를 검색한다")
        void success_searchByKeyword() {
            // given
            LocalDateTime now = LocalDateTime.now();

            // 폴더 생성
            FolderInfoEntity folder1 = FolderInfoEntity.builder()
                .parentId(null)
                .groupId("group1")
                .name("Project A")
                .owner("user@example.com")
                .path("/group1/projectA")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(folder1);

            FolderInfoEntity folder2 = FolderInfoEntity.builder()
                .parentId(null)
                .groupId("group1")
                .name("Documents")
                .owner("user@example.com")
                .path("/group1/documents")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(folder2);

            FolderInfoEntity folder3 = FolderInfoEntity.builder()
                .parentId(null)
                .groupId("group1")
                .name("Project B")
                .owner("user@example.com")
                .path("/group1/projectB")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(folder3);

            entityManager.flush();
            entityManager.clear();

            com.odcloud.domain.model.Account account = com.odcloud.domain.model.Account.builder()
                .id(1L)
                .email("user@example.com")
                .groups(List.of(com.odcloud.domain.model.Group.of("group1")))
                .build();

            FindFilesCommand command = FindFilesCommand.builder()
                .account(account)
                .keyword("Project")
                .sortType("NAME_ASC")
                .build();

            // when
            var result = adapter.findAll(command);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).extracting(FolderInfo::getName)
                .contains("Project A", "Project B");
        }

        @Test
        @DisplayName("[success] groupId로 폴더를 필터링한다")
        void success_filterByGroupId() {
            // given
            LocalDateTime now = LocalDateTime.now();

            // 부모 폴더 생성
            FolderInfoEntity parentFolder = FolderInfoEntity.builder()
                .parentId(null)
                .groupId("group1")
                .name("Parent")
                .owner("user@example.com")
                .path("/group1")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(parentFolder);

            // group1 하위 폴더
            FolderInfoEntity child1 = FolderInfoEntity.builder()
                .parentId(parentFolder.getId())
                .groupId("group1")
                .name("Child of Group1")
                .owner("user@example.com")
                .path("/group1/child1")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(child1);

            // group2 하위 폴더
            FolderInfoEntity child2 = FolderInfoEntity.builder()
                .parentId(parentFolder.getId())
                .groupId("group2")
                .name("Child of Group2")
                .owner("user@example.com")
                .path("/group1/child2")
                .regDt(now)
                .modDt(now)
                .build();
            entityManager.persist(child2);

            entityManager.flush();
            entityManager.clear();

            com.odcloud.domain.model.Account account = com.odcloud.domain.model.Account.builder()
                .id(1L)
                .email("user@example.com")
                .groups(List.of(com.odcloud.domain.model.Group.of("group1"),
                    com.odcloud.domain.model.Group.of("group2")))
                .build();

            FindFilesCommand command = FindFilesCommand.builder()
                .account(account)
                .folderId(parentFolder.getId())
                .groupId("group1")
                .sortType("NAME_ASC")
                .build();

            // when
            var result = adapter.findAll(command);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getName()).isEqualTo("Child of Group1");
            assertThat(result.get(0).getGroupId()).isEqualTo("group1");
        }
    }
}
