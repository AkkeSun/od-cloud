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
        @DisplayName("[success] 여러 개의 점이 있는 파일명으로 File을 생성한다")
        void success_multipleExtensions() {
            // given
            FolderInfo folder = FolderInfo.builder()
                .id(1L)
                .build();

            MultipartFile multipartFile = mock(MultipartFile.class);
            when(multipartFile.getOriginalFilename()).thenReturn("test.backup.tar.gz");

            // when
            FileInfo file = FileInfo.create("/data", folder, multipartFile);

            // then
            assertThat(file).isNotNull();
            assertThat(file.getFileName()).isEqualTo("test.backup.tar.gz");
            assertThat(file.getFileLoc()).endsWith(".gz");
        }

        @Test
        @DisplayName("[success] 파일명이 40자를 초과하면 확장자를 제외한 앞 40자만 남긴다")
        void success_truncateLongFileName() {
            // given
            FolderInfo folder = FolderInfo.builder()
                .id(1L)
                .build();

            String longName = "a".repeat(45);
            MultipartFile multipartFile = mock(MultipartFile.class);
            when(multipartFile.getOriginalFilename()).thenReturn(longName + ".txt");

            // when
            FileInfo file = FileInfo.create("/data", folder, multipartFile);

            // then
            assertThat(file.getFileName()).isEqualTo("a".repeat(40) + ".txt");
        }

        @Test
        @DisplayName("[success] 확장자를 제외한 파일명이 40자면 자르지 않는다")
        void success_notTruncateBoundaryFileName() {
            // given
            FolderInfo folder = FolderInfo.builder()
                .id(1L)
                .build();

            String name = "a".repeat(40);
            MultipartFile multipartFile = mock(MultipartFile.class);
            when(multipartFile.getOriginalFilename()).thenReturn(name + ".txt");

            // when
            FileInfo file = FileInfo.create("/data", folder, multipartFile);

            // then
            assertThat(file.getFileName()).isEqualTo(name + ".txt");
        }

        @Test
        @DisplayName("[success] 파일명 중간에 확장자와 같은 문자열이 있어도 마지막 확장자만 분리한다")
        void success_duplicatedExtensionText() {
            // given
            FolderInfo folder = FolderInfo.builder()
                .id(1L)
                .build();

            MultipartFile multipartFile = mock(MultipartFile.class);
            when(multipartFile.getOriginalFilename()).thenReturn("2024.txt.report.txt");

            // when
            FileInfo file = FileInfo.create("/data", folder, multipartFile);

            // then
            assertThat(file.getFileName()).isEqualTo("2024.txt.report.txt");
            assertThat(file.getFileLoc()).endsWith(".txt");
        }

        @Test
        @DisplayName("[success] 확장자가 없는 파일명으로 File을 생성한다")
        void success_noExtension() {
            // given
            FolderInfo folder = FolderInfo.builder()
                .id(1L)
                .build();

            MultipartFile multipartFile = mock(MultipartFile.class);
            when(multipartFile.getOriginalFilename()).thenReturn("README");

            // when
            FileInfo file = FileInfo.create("/data", folder, multipartFile);

            // then
            assertThat(file.getFileName()).isEqualTo("README");
        }

        @Test
        @DisplayName("[success] 다양한 확장자를 가진 파일로 File을 생성한다")
        void success_variousExtensions() {
            // given
            FolderInfo folder = FolderInfo.builder()
                .id(1L)
                .build();

            MultipartFile multipartFile = mock(MultipartFile.class);
            when(multipartFile.getOriginalFilename()).thenReturn("image.png");

            // when
            FileInfo file = FileInfo.create("/data", folder, multipartFile);

            // then
            assertThat(file).isNotNull();
            assertThat(file.getFileName()).isEqualTo("image.png");
            assertThat(file.getFileLoc()).endsWith(".png");
        }
    }

    @Nested
    @DisplayName("[addFileNameNumber] 중복 파일명에 순번을 붙이는 메서드")
    class Describe_addFileNameNumber {

        @Test
        @DisplayName("[success] 확장자 앞에 순번을 붙인다")
        void success() {
            // given
            FileInfo file = FileInfo.builder()
                .fileName("test.txt")
                .build();

            // when
            file.addFileNameNumber(2);

            // then
            assertThat(file.getFileName()).isEqualTo("test(2).txt");
        }

        @Test
        @DisplayName("[success] 점이 여러 개인 파일명은 마지막 확장자 앞에 순번을 붙인다")
        void success_multipleDots() {
            // given
            FileInfo file = FileInfo.builder()
                .fileName("my.report.pdf")
                .build();

            // when
            file.addFileNameNumber(2);

            // then
            assertThat(file.getFileName()).isEqualTo("my.report(2).pdf");
        }

        @Test
        @DisplayName("[success] 연속으로 호출해도 순번이 누적되지 않고 교체된다")
        void success_repeatedCall() {
            // given
            FileInfo file = FileInfo.builder()
                .fileName("test.txt")
                .build();

            // when
            file.addFileNameNumber(2);
            file.addFileNameNumber(3);
            file.addFileNameNumber(4);

            // then
            assertThat(file.getFileName()).isEqualTo("test(4).txt");
        }

        @Test
        @DisplayName("[success] 점이 여러 개인 파일명도 순번이 누적되지 않는다")
        void success_repeatedCallWithMultipleDots() {
            // given
            FileInfo file = FileInfo.builder()
                .fileName("my.report.pdf")
                .build();

            // when
            file.addFileNameNumber(2);
            file.addFileNameNumber(3);

            // then
            assertThat(file.getFileName()).isEqualTo("my.report(3).pdf");
        }

        @Test
        @DisplayName("[success] 확장자가 없는 파일명은 끝에 순번을 붙인다")
        void success_noExtension() {
            // given
            FileInfo file = FileInfo.builder()
                .fileName("README")
                .build();

            // when
            file.addFileNameNumber(2);

            // then
            assertThat(file.getFileName()).isEqualTo("README(2)");
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
                1L,
                "test.txt",
                "/path/to/test.txt",
                1024L,
                now,
                now
            );

            // then
            assertThat(file).isNotNull();
            assertThat(file.getId()).isEqualTo(1L);
            assertThat(file.getFolderId()).isEqualTo(100L);
            assertThat(file.getGroupId()).isEqualTo(1L);
            assertThat(file.getFileName()).isEqualTo("test.txt");
            assertThat(file.getFileLoc()).isEqualTo("/path/to/test.txt");
            assertThat(file.getFileSize()).isEqualTo(1024L);
            assertThat(file.getModDt()).isEqualTo(now);
            assertThat(file.getRegDt()).isEqualTo(now);
        }
    }
}
