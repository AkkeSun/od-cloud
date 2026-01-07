package com.odcloud.adapter.out.persistence.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.IntegrationTestSupport;
import com.odcloud.application.file.port.in.command.FindFilesCommand;
import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.FileInfo;
import com.odcloud.domain.model.Group;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@DisplayName("FileRepository keyword 검색 상세 테스트")
class FileRepositoryKeywordSearchTest extends IntegrationTestSupport {

    @Autowired
    private FileInfoRepository fileRepository;

    @Autowired
    private EntityManager entityManager;

    @AfterEach
    void tearDown() {
        entityManager.createQuery("DELETE FROM FileInfoEntity").executeUpdate();
        entityManager.createQuery("DELETE FROM FolderInfoEntity").executeUpdate();
        entityManager.flush();
    }

    @Test
    @DisplayName("keyword 검색 시 PUBLIC 폴더와 본인 PRIVATE 폴더의 파일만 검색된다")
    void keywordSearch_withMultipleFolders() {
        // given
        LocalDateTime now = LocalDateTime.now();

        // 1. PUBLIC 폴더 (group1) 생성
        FolderInfoEntity publicFolder = FolderInfoEntity.builder()
            .parentId(null)
            .groupId(1L)
            .name("Public Folder")
            .owner("admin@example.com")
            .path("/group1/public")
            .regDt(now)
            .modDt(now)
            .build();
        entityManager.persist(publicFolder);

        FolderInfoEntity noAccessPublicFolder = FolderInfoEntity.builder()
            .parentId(null)
            .groupId(2L)
            .name("No Access Public Folder")
            .owner("admin@example.com")
            .path("/group2/public")
            .regDt(now)
            .modDt(now)
            .build();
        entityManager.persist(noAccessPublicFolder);
        entityManager.flush();

        // PUBLIC 폴더에 파일 생성
        FileInfoEntity publicFile = FileInfoEntity.builder()
            .folderId(publicFolder.getId())
            .fileName("test-public.txt")
            .fileLoc("/group1/public/test-public.txt")
            .regDt(now)
            .modDt(now)
            .build();
        entityManager.persist(publicFile);

        // 권한 없는 그룹의 PUBLIC 폴더에 파일 생성 (검색 안되어야 함)
        FileInfoEntity noAccessFile = FileInfoEntity.builder()
            .folderId(noAccessPublicFolder.getId())
            .fileName("test-no-access.txt")
            .fileLoc("/group2/public/test-no-access.txt")
            .regDt(now)
            .modDt(now)
            .build();
        entityManager.persist(noAccessFile);
        entityManager.flush();
        entityManager.clear();

        // when
        Account account = Account.builder()
            .id(1L)
            .email("user@example.com")
            .groups(List.of(Group.of(1L)))
            .build();

        FindFilesCommand command = FindFilesCommand.builder()
            .account(account)
            .keyword("test")
            .sortType("NAME_ASC")
            .build();

        List<FileInfo> result = fileRepository.findAll(command);

        // then
        System.out.println("=== 검색 결과 ===");
        result.forEach(file -> System.out.println("Found: " + file.getFileName()));

        assertThat(result).hasSize(1);
        assertThat(result).extracting(FileInfo::getFileName)
            .containsExactlyInAnyOrder("test-public.txt");
    }

    @Test
    @DisplayName("keyword가 빈 문자열이면 검색되지 않는다")
    void keywordSearch_withEmptyKeyword() {
        // given
        LocalDateTime now = LocalDateTime.now();

        FolderInfoEntity folder = FolderInfoEntity.builder()
            .parentId(null)
            .groupId(1L)
            .name("Test Folder")
            .owner("user@example.com")
            .path("/group1/test")
            .regDt(now)
            .modDt(now)
            .build();
        entityManager.persist(folder);
        entityManager.flush();

        FileInfoEntity file = FileInfoEntity.builder()
            .folderId(folder.getId())
            .fileName("test.txt")
            .fileLoc("/group1/test/test.txt")
            .regDt(now)
            .modDt(now)
            .build();
        entityManager.persist(file);

        entityManager.flush();
        entityManager.clear();

        // when
        Account account = Account.builder()
            .id(1L)
            .email("user@example.com")
            .groups(List.of(Group.of(1L)))
            .build();

        // keyword가 빈 문자열일 때는 folderId가 필요
        FindFilesCommand command = FindFilesCommand.builder()
            .account(account)
            .keyword("")
            .folderId(folder.getId())
            .sortType("NAME_ASC")
            .build();

        List<FileInfo> result = fileRepository.findAll(command);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFileName()).isEqualTo("test.txt");
    }
}
