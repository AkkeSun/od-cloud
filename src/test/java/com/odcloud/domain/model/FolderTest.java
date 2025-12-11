package com.odcloud.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.application.port.in.command.RegisterFolderCommand;
import com.odcloud.application.port.in.command.RegisterGroupCommand;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FolderTest {

    @Nested
    @DisplayName("[ofRootFolder] RegisterGroupCommand로부터 루트 폴더를 생성하는 정적 팩토리 메서드")
    class Describe_ofRootFolder {

        @Test
        @DisplayName("[success] RegisterGroupCommand로부터 루트 폴더를 생성한다")
        void success() {
            // given
            RegisterGroupCommand command = new RegisterGroupCommand(
                "테스트 그룹",
                "owner@example.com"
            );

            LocalDateTime before = LocalDateTime.now().minusSeconds(1);

            // when
            FolderInfo folder = FolderInfo.ofRootFolder(Group.of(command));

            // then
            LocalDateTime after = LocalDateTime.now().plusSeconds(1);

            assertThat(folder).isNotNull();
            assertThat(folder.getParentId()).isNull();
            assertThat(folder.getName()).isEqualTo("테스트 그룹");
            assertThat(folder.getOwner()).isEqualTo("owner@example.com");
            assertThat(folder.getAccessLevel()).isEqualTo("PUBLIC");
            assertThat(folder.getRegDt()).isAfter(before);
            assertThat(folder.getRegDt()).isBefore(after);
        }

        @Test
        @DisplayName("[success] null 값을 포함한 RegisterGroupCommand로부터 루트 폴더를 생성한다")
        void success_nullValues() {
            // given
            RegisterGroupCommand command = new RegisterGroupCommand(
                null,
                null
            );

            // when
            FolderInfo folder = FolderInfo.ofRootFolder(Group.of(command));

            // then
            assertThat(folder).isNotNull();
            assertThat(folder.getParentId()).isNull();
            assertThat(folder.getName()).isNull();
            assertThat(folder.getOwner()).isNull();
            assertThat(folder.getAccessLevel()).isEqualTo("PUBLIC");
        }
    }

    @Nested
    @DisplayName("[createSubFolder] RegisterFolderCommand로부터 서브 폴더를 생성하는 정적 팩토리 메서드")
    class Describe_createSubFolder {

        @Test
        @DisplayName("[success] RegisterFolderCommand로부터 서브 폴더를 생성한다")
        void success() {
            // given
            RegisterFolderCommand command = new RegisterFolderCommand(
                1L,
                "group-123",
                "서브 폴더",
                "owner@example.com",
                "PRIVATE"
            );
            String parentPath = "/group-123";

            LocalDateTime before = LocalDateTime.now().minusSeconds(1);

            // when
            FolderInfo folder = FolderInfo.createSubFolder(command, parentPath);

            // then
            LocalDateTime after = LocalDateTime.now().plusSeconds(1);

            assertThat(folder).isNotNull();
            assertThat(folder.getParentId()).isEqualTo(1L);
            assertThat(folder.getGroupId()).isEqualTo("group-123");
            assertThat(folder.getName()).isEqualTo("서브 폴더");
            assertThat(folder.getOwner()).isEqualTo("owner@example.com");
            assertThat(folder.getPath()).startsWith("/group-123/");
            assertThat(folder.getPath()).contains("_");
            assertThat(folder.getAccessLevel()).isEqualTo("PRIVATE");
            assertThat(folder.getRegDt()).isAfter(before);
            assertThat(folder.getRegDt()).isBefore(after);
        }

        @Test
        @DisplayName("[success] 루트 경로에서 서브 폴더를 생성한다")
        void success_rootPath() {
            // given
            RegisterFolderCommand command = new RegisterFolderCommand(
                1L,
                "group-123",
                "서브 폴더",
                "owner@example.com",
                "PUBLIC"
            );
            String parentPath = "/test";

            // when
            FolderInfo folder = FolderInfo.createSubFolder(command, parentPath);

            // then
            assertThat(folder).isNotNull();
            assertThat(folder.getPath()).startsWith("/");
            assertThat(folder.getPath().split("/")).hasSize(3);
        }

        @Test
        @DisplayName("[success] 깊은 경로에서 서브 폴더를 생성한다")
        void success_deepPath() {
            // given
            RegisterFolderCommand command = new RegisterFolderCommand(
                3L,
                "group-123",
                "서브 폴더",
                "owner@example.com",
                "PUBLIC"
            );
            String parentPath = "/group-123/folder1/folder2";

            // when
            FolderInfo folder = FolderInfo.createSubFolder(command, parentPath);

            // then
            assertThat(folder).isNotNull();
            assertThat(folder.getPath()).startsWith("/group-123/folder1/folder2/");
        }

        @Test
        @DisplayName("[success] null 값을 포함한 RegisterFolderCommand로부터 서브 폴더를 생성한다")
        void success_nullValues() {
            // given
            RegisterFolderCommand command = new RegisterFolderCommand(
                null,
                null,
                null,
                null,
                null
            );
            String parentPath = "/group-123";

            // when
            FolderInfo folder = FolderInfo.createSubFolder(command, parentPath);

            // then
            assertThat(folder).isNotNull();
            assertThat(folder.getParentId()).isNull();
            assertThat(folder.getGroupId()).isNull();
            assertThat(folder.getName()).isNull();
            assertThat(folder.getOwner()).isNull();
            assertThat(folder.getAccessLevel()).isNull();
            assertThat(folder.getPath()).startsWith("/group-123/");
        }

        @Test
        @DisplayName("[success] 다양한 accessLevel로 서브 폴더를 생성한다")
        void success_variousAccessLevels() {
            // given
            RegisterFolderCommand publicCommand = new RegisterFolderCommand(
                1L, "group-123", "공개 폴더", "owner@example.com", "PUBLIC"
            );
            RegisterFolderCommand privateCommand = new RegisterFolderCommand(
                1L, "group-123", "비공개 폴더", "owner@example.com", "PRIVATE"
            );

            // when
            FolderInfo publicFolder = FolderInfo.createSubFolder(publicCommand, "/group-123");
            FolderInfo privateFolder = FolderInfo.createSubFolder(privateCommand, "/group-123");

            // then
            assertThat(publicFolder.getAccessLevel()).isEqualTo("PUBLIC");
            assertThat(privateFolder.getAccessLevel()).isEqualTo("PRIVATE");
        }
    }

    @Nested
    @DisplayName("[getter] Getter 메서드 테스트")
    class Describe_getter {

        @Test
        @DisplayName("[success] getId()로 id를 조회한다")
        void success_getId() {
            // given
            FolderInfo folder = FolderInfo.builder()
                .id(1L)
                .build();

            // when
            Long id = folder.getId();

            // then
            assertThat(id).isEqualTo(1L);
        }

        @Test
        @DisplayName("[success] getParentId()로 parentId를 조회한다")
        void success_getParentId() {
            // given
            FolderInfo folder = FolderInfo.builder()
                .parentId(100L)
                .build();

            // when
            Long parentId = folder.getParentId();

            // then
            assertThat(parentId).isEqualTo(100L);
        }

        @Test
        @DisplayName("[success] getGroupId()로 groupId를 조회한다")
        void success_getGroupId() {
            // given
            FolderInfo folder = FolderInfo.builder()
                .groupId("group-123")
                .build();

            // when
            String groupId = folder.getGroupId();

            // then
            assertThat(groupId).isEqualTo("group-123");
        }

        @Test
        @DisplayName("[success] getName()으로 name을 조회한다")
        void success_getName() {
            // given
            FolderInfo folder = FolderInfo.builder()
                .name("테스트 폴더")
                .build();

            // when
            String name = folder.getName();

            // then
            assertThat(name).isEqualTo("테스트 폴더");
        }

        @Test
        @DisplayName("[success] getOwner()로 owner를 조회한다")
        void success_getOwner() {
            // given
            FolderInfo folder = FolderInfo.builder()
                .owner("owner@example.com")
                .build();

            // when
            String owner = folder.getOwner();

            // then
            assertThat(owner).isEqualTo("owner@example.com");
        }

        @Test
        @DisplayName("[success] getPath()로 path를 조회한다")
        void success_getPath() {
            // given
            FolderInfo folder = FolderInfo.builder()
                .path("/group-123")
                .build();

            // when
            String path = folder.getPath();

            // then
            assertThat(path).isEqualTo("/group-123");
        }

        @Test
        @DisplayName("[success] getAccessLevel()로 accessLevel을 조회한다")
        void success_getAccessLevel() {
            // given
            FolderInfo folder = FolderInfo.builder()
                .accessLevel("PUBLIC")
                .build();

            // when
            String accessLevel = folder.getAccessLevel();

            // then
            assertThat(accessLevel).isEqualTo("PUBLIC");
        }

        @Test
        @DisplayName("[success] getModDt()로 modDt를 조회한다")
        void success_getModDt() {
            // given
            LocalDateTime now = LocalDateTime.now();
            FolderInfo folder = FolderInfo.builder()
                .modDt(now)
                .build();

            // when
            LocalDateTime modDt = folder.getModDt();

            // then
            assertThat(modDt).isEqualTo(now);
        }

        @Test
        @DisplayName("[success] getRegDt()로 regDt를 조회한다")
        void success_getRegDt() {
            // given
            LocalDateTime now = LocalDateTime.now();
            FolderInfo folder = FolderInfo.builder()
                .regDt(now)
                .build();

            // when
            LocalDateTime regDt = folder.getRegDt();

            // then
            assertThat(regDt).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("[noArgsConstructor] NoArgsConstructor 테스트")
    class Describe_noArgsConstructor {

        @Test
        @DisplayName("[success] NoArgsConstructor로 Folder를 생성한다")
        void success() {
            // when
            FolderInfo folder = new FolderInfo();

            // then
            assertThat(folder).isNotNull();
        }
    }

    @Nested
    @DisplayName("[allArgsConstructor] AllArgsConstructor 테스트")
    class Describe_allArgsConstructor {

        @Test
        @DisplayName("[success] AllArgsConstructor로 Folder를 생성한다")
        void success() {
            // given
            LocalDateTime now = LocalDateTime.now();

            // when
            FolderInfo folder = new FolderInfo(
                1L,
                null,
                "group-123",
                "테스트 폴더",
                "owner@example.com",
                "/group-123",
                "PUBLIC",
                now,
                now
            );

            // then
            assertThat(folder).isNotNull();
            assertThat(folder.getId()).isEqualTo(1L);
            assertThat(folder.getParentId()).isNull();
            assertThat(folder.getGroupId()).isEqualTo("group-123");
            assertThat(folder.getName()).isEqualTo("테스트 폴더");
            assertThat(folder.getOwner()).isEqualTo("owner@example.com");
            assertThat(folder.getPath()).isEqualTo("/group-123");
            assertThat(folder.getAccessLevel()).isEqualTo("PUBLIC");
            assertThat(folder.getModDt()).isEqualTo(now);
            assertThat(folder.getRegDt()).isEqualTo(now);
        }
    }
}
