package com.odcloud.adapter.out.persistence.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.IntegrationTestSupport;
import com.odcloud.application.port.in.command.FindFilesCommand;
import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.File;
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
    private FileRepository fileRepository;

    @Autowired
    private EntityManager entityManager;

    @AfterEach
    void tearDown() {
        entityManager.createQuery("DELETE FROM FileEntity").executeUpdate();
        entityManager.createQuery("DELETE FROM FolderEntity").executeUpdate();
        entityManager.flush();
    }

    @Test
    @DisplayName("keyword 검색 시 PUBLIC 폴더와 본인 PRIVATE 폴더의 파일만 검색된다")
    void keywordSearch_withMultipleFolders() {
        // given
        LocalDateTime now = LocalDateTime.now();

        // 1. PUBLIC 폴더 (group1) 생성
        FolderEntity publicFolder = FolderEntity.builder()
            .parentId(null)
            .groupId("group1")
            .name("Public Folder")
            .owner("admin@example.com")
            .path("/group1/public")
            .accessLevel("PUBLIC")
            .regDt(now)
            .modDt(now)
            .build();
        entityManager.persist(publicFolder);

        // 2. 본인의 PRIVATE 폴더 생성
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

        // 3. 다른 사람의 PRIVATE 폴더 생성
        FolderEntity otherPrivateFolder = FolderEntity.builder()
            .parentId(null)
            .groupId("group1")
            .name("Other Private Folder")
            .owner("other@example.com")
            .path("/group1/other-private")
            .accessLevel("PRIVATE")
            .regDt(now)
            .modDt(now)
            .build();
        entityManager.persist(otherPrivateFolder);

        // 4. 권한 없는 그룹의 PUBLIC 폴더 생성
        FolderEntity noAccessPublicFolder = FolderEntity.builder()
            .parentId(null)
            .groupId("group2")
            .name("No Access Public Folder")
            .owner("admin@example.com")
            .path("/group2/public")
            .accessLevel("PUBLIC")
            .regDt(now)
            .modDt(now)
            .build();
        entityManager.persist(noAccessPublicFolder);

        entityManager.flush();

        // PUBLIC 폴더에 파일 생성
        FileEntity publicFile = FileEntity.builder()
            .folderId(publicFolder.getId())
            .fileName("test-public.txt")
            .fileLoc("/group1/public/test-public.txt")
            .regDt(now)
            .modDt(now)
            .build();
        entityManager.persist(publicFile);

        // 본인의 PRIVATE 폴더에 파일 생성
        FileEntity myPrivateFile = FileEntity.builder()
            .folderId(myPrivateFolder.getId())
            .fileName("test-my-private.pdf")
            .fileLoc("/group1/my-private/test-my-private.pdf")
            .regDt(now)
            .modDt(now)
            .build();
        entityManager.persist(myPrivateFile);

        // 다른 사람의 PRIVATE 폴더에 파일 생성 (검색 안되어야 함)
        FileEntity otherPrivateFile = FileEntity.builder()
            .folderId(otherPrivateFolder.getId())
            .fileName("test-other-private.doc")
            .fileLoc("/group1/other-private/test-other-private.doc")
            .regDt(now)
            .modDt(now)
            .build();
        entityManager.persist(otherPrivateFile);

        // 권한 없는 그룹의 PUBLIC 폴더에 파일 생성 (검색 안되어야 함)
        FileEntity noAccessFile = FileEntity.builder()
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
            .groups(List.of(Group.of("group1")))
            .build();

        FindFilesCommand command = FindFilesCommand.builder()
            .account(account)
            .keyword("test")
            .sortType("NAME_ASC")
            .build();

        List<File> result = fileRepository.findAll(command);

        // then
        System.out.println("=== 검색 결과 ===");
        result.forEach(file -> System.out.println("Found: " + file.getFileName()));

        assertThat(result).hasSize(2);
        assertThat(result).extracting(File::getFileName)
            .containsExactlyInAnyOrder("test-public.txt", "test-my-private.pdf");
    }

    @Test
    @DisplayName("keyword가 빈 문자열이면 검색되지 않는다")
    void keywordSearch_withEmptyKeyword() {
        // given
        LocalDateTime now = LocalDateTime.now();

        FolderEntity folder = FolderEntity.builder()
            .parentId(null)
            .groupId("group1")
            .name("Test Folder")
            .owner("user@example.com")
            .path("/group1/test")
            .accessLevel("PUBLIC")
            .regDt(now)
            .modDt(now)
            .build();
        entityManager.persist(folder);
        entityManager.flush();

        FileEntity file = FileEntity.builder()
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
            .groups(List.of(Group.of("group1")))
            .build();

        // keyword가 빈 문자열일 때는 folderId가 필요
        FindFilesCommand command = FindFilesCommand.builder()
            .account(account)
            .keyword("")
            .folderId(folder.getId())
            .sortType("NAME_ASC")
            .build();

        List<File> result = fileRepository.findAll(command);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFileName()).isEqualTo("test.txt");
    }
}
