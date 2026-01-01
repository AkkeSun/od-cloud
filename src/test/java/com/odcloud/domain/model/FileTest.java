package com.odcloud.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

class FileTest {

    @Nested
    @DisplayName("[create] Folder와 MultipartFile로부터 File을 생성하는 정적 팩토리 메서드")
    class Describe_create {

        @Test
        @DisplayName("[success] Folder와 MultipartFile로부터 File을 생성한다")
        void success() {
            // given
            FolderInfo folder = FolderInfo.builder()
                .id(1L)
                .path("/group-123")
                .build();

            MultipartFile multipartFile = mock(MultipartFile.class);
            when(multipartFile.getOriginalFilename()).thenReturn("test.txt");
            when(multipartFile.getSize()).thenReturn(1024L);

            LocalDateTime before = LocalDateTime.now().minusSeconds(1);

            // when
            FileInfo file = FileInfo.create(folder, multipartFile);

            // then
            LocalDateTime after = LocalDateTime.now().plusSeconds(1);

            assertThat(file).isNotNull();
            assertThat(file.getFolderId()).isEqualTo(1L);
            assertThat(file.getFileName()).isEqualTo("test.txt");
            assertThat(file.getFileLoc()).startsWith("/group-123/");
            assertThat(file.getFileLoc()).endsWith(".txt");
            assertThat(file.getFileSize()).isEqualTo(1024L);
            assertThat(file.getMultipartFile()).isEqualTo(multipartFile);
            assertThat(file.getRegDt()).isAfter(before);
            assertThat(file.getRegDt()).isBefore(after);
        }

        @Test
        @DisplayName("[success] 확장자가 없는 파일로 File을 생성한다")
        void success_noExtension() {
            // given
            FolderInfo folder = FolderInfo.builder()
                .id(1L)
                .path("/group-123")
                .build();

            MultipartFile multipartFile = mock(MultipartFile.class);
            when(multipartFile.getOriginalFilename()).thenReturn("testfile");

            // when
            FileInfo file = FileInfo.create(folder, multipartFile);

            // then
            assertThat(file).isNotNull();
            assertThat(file.getFileName()).isEqualTo("testfile");
            assertThat(file.getFileLoc()).startsWith("/group-123/");
            assertThat(file.getFileLoc()).doesNotContain(".");
        }

        @Test
        @DisplayName("[success] 여러 개의 점이 있는 파일명으로 File을 생성한다")
        void success_multipleExtensions() {
            // given
            FolderInfo folder = FolderInfo.builder()
                .id(1L)
                .path("/group-123")
                .build();

            MultipartFile multipartFile = mock(MultipartFile.class);
            when(multipartFile.getOriginalFilename()).thenReturn("test.backup.tar.gz");

            // when
            FileInfo file = FileInfo.create(folder, multipartFile);

            // then
            assertThat(file).isNotNull();
            assertThat(file.getFileName()).isEqualTo("test.backup.tar.gz");
            assertThat(file.getFileLoc()).endsWith(".gz");
        }

        @Test
        @DisplayName("[success] 다양한 확장자를 가진 파일로 File을 생성한다")
        void success_variousExtensions() {
            // given
            FolderInfo folder = FolderInfo.builder()
                .id(1L)
                .path("/group-123")
                .build();

            MultipartFile multipartFile = mock(MultipartFile.class);
            when(multipartFile.getOriginalFilename()).thenReturn("image.png");

            // when
            FileInfo file = FileInfo.create(folder, multipartFile);

            // then
            assertThat(file).isNotNull();
            assertThat(file.getFileName()).isEqualTo("image.png");
            assertThat(file.getFileLoc()).endsWith(".png");
        }

        @Test
        @DisplayName("[success] 경로에 슬래시가 있는 폴더로 File을 생성한다")
        void success_folderWithSlash() {
            // given
            FolderInfo folder = FolderInfo.builder()
                .id(1L)
                .path("/group-123/subfolder")
                .build();

            MultipartFile multipartFile = mock(MultipartFile.class);
            when(multipartFile.getOriginalFilename()).thenReturn("test.txt");

            // when
            FileInfo file = FileInfo.create(folder, multipartFile);

            // then
            assertThat(file).isNotNull();
            assertThat(file.getFileLoc()).startsWith("/group-123/subfolder/");
        }
    }

    @Nested
    @DisplayName("[getter] Getter 메서드 테스트")
    class Describe_getter {

        @Test
        @DisplayName("[success] getId()로 id를 조회한다")
        void success_getId() {
            // given
            FileInfo file = FileInfo.builder()
                .id(1L)
                .build();

            // when
            Long id = file.getId();

            // then
            assertThat(id).isEqualTo(1L);
        }

        @Test
        @DisplayName("[success] getFolderId()로 folderId를 조회한다")
        void success_getFolderId() {
            // given
            FileInfo file = FileInfo.builder()
                .folderId(100L)
                .build();

            // when
            Long folderId = file.getFolderId();

            // then
            assertThat(folderId).isEqualTo(100L);
        }

        @Test
        @DisplayName("[success] getFileName()으로 fileName을 조회한다")
        void success_getFileName() {
            // given
            FileInfo file = FileInfo.builder()
                .fileName("test.txt")
                .build();

            // when
            String fileName = file.getFileName();

            // then
            assertThat(fileName).isEqualTo("test.txt");
        }

        @Test
        @DisplayName("[success] getFileLoc()로 fileLoc을 조회한다")
        void success_getFileLoc() {
            // given
            FileInfo file = FileInfo.builder()
                .fileLoc("/path/to/test.txt")
                .build();

            // when
            String fileLoc = file.getFileLoc();

            // then
            assertThat(fileLoc).isEqualTo("/path/to/test.txt");
        }

        @Test
        @DisplayName("[success] getFileSize()로 fileSize를 조회한다")
        void success_getFileSize() {
            // given
            FileInfo file = FileInfo.builder()
                .fileSize(2048L)
                .build();

            // when
            Long fileSize = file.getFileSize();

            // then
            assertThat(fileSize).isEqualTo(2048L);
        }

        @Test
        @DisplayName("[success] getMultipartFile()로 multipartFile을 조회한다")
        void success_getMultipartFile() {
            // given
            MultipartFile multipartFile = mock(MultipartFile.class);
            FileInfo file = FileInfo.builder()
                .multipartFile(multipartFile)
                .build();

            // when
            MultipartFile result = file.getMultipartFile();

            // then
            assertThat(result).isEqualTo(multipartFile);
        }

        @Test
        @DisplayName("[success] getModDt()로 modDt를 조회한다")
        void success_getModDt() {
            // given
            LocalDateTime now = LocalDateTime.now();
            FileInfo file = FileInfo.builder()
                .modDt(now)
                .build();

            // when
            LocalDateTime modDt = file.getModDt();

            // then
            assertThat(modDt).isEqualTo(now);
        }

        @Test
        @DisplayName("[success] getRegDt()로 regDt를 조회한다")
        void success_getRegDt() {
            // given
            LocalDateTime now = LocalDateTime.now();
            FileInfo file = FileInfo.builder()
                .regDt(now)
                .build();

            // when
            LocalDateTime regDt = file.getRegDt();

            // then
            assertThat(regDt).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("[noArgsConstructor] NoArgsConstructor 테스트")
    class Describe_noArgsConstructor {

        @Test
        @DisplayName("[success] NoArgsConstructor로 File을 생성한다")
        void success() {
            // when
            FileInfo file = new FileInfo();

            // then
            assertThat(file).isNotNull();
        }
    }

    @Nested
    @DisplayName("[allArgsConstructor] AllArgsConstructor 테스트")
    class Describe_allArgsConstructor {

        @Test
        @DisplayName("[success] AllArgsConstructor로 File을 생성한다")
        void success() {
            // given
            LocalDateTime now = LocalDateTime.now();
            MultipartFile multipartFile = mock(MultipartFile.class);

            // when
            FileInfo file = new FileInfo(
                1L,
                100L,
                "test.txt",
                "/path/to/test.txt",
                1024L,
                multipartFile,
                now,
                now
            );

            // then
            assertThat(file).isNotNull();
            assertThat(file.getId()).isEqualTo(1L);
            assertThat(file.getFolderId()).isEqualTo(100L);
            assertThat(file.getFileName()).isEqualTo("test.txt");
            assertThat(file.getFileLoc()).isEqualTo("/path/to/test.txt");
            assertThat(file.getFileSize()).isEqualTo(1024L);
            assertThat(file.getMultipartFile()).isEqualTo(multipartFile);
            assertThat(file.getModDt()).isEqualTo(now);
            assertThat(file.getRegDt()).isEqualTo(now);
        }
    }
}
