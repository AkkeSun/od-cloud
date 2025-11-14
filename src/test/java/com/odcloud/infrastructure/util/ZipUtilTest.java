package com.odcloud.infrastructure.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.odcloud.infrastructure.util.ZipUtil.FileInfo;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ZipUtilTest {

    @Nested
    @DisplayName("[createZip] ZIP 파일 생성")
    class Describe_createZip {

        @Test
        @DisplayName("[success] 여러 파일을 ZIP으로 압축한다")
        void success() throws Exception {
            // given
            List<FileInfo> fileInfos = new ArrayList<>();
            fileInfos.add(new FileInfo("file1.txt",
                new ByteArrayInputStream("content1".getBytes())));
            fileInfos.add(new FileInfo("file2.txt",
                new ByteArrayInputStream("content2".getBytes())));

            // when
            byte[] zipContent = ZipUtil.createZip(fileInfos);

            // then
            assertThat(zipContent).isNotEmpty();

            // ZIP 내용 검증
            try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipContent))) {
                ZipEntry entry1 = zis.getNextEntry();
                assertThat(entry1.getName()).isEqualTo("file1.txt");

                byte[] content1 = zis.readAllBytes();
                assertThat(new String(content1)).isEqualTo("content1");

                ZipEntry entry2 = zis.getNextEntry();
                assertThat(entry2.getName()).isEqualTo("file2.txt");

                byte[] content2 = zis.readAllBytes();
                assertThat(new String(content2)).isEqualTo("content2");
            }
        }

        @Test
        @DisplayName("[success] 단일 파일을 ZIP으로 압축한다")
        void success_singleFile() throws Exception {
            // given
            List<FileInfo> fileInfos = new ArrayList<>();
            fileInfos.add(new FileInfo("single.txt",
                new ByteArrayInputStream("single content".getBytes())));

            // when
            byte[] zipContent = ZipUtil.createZip(fileInfos);

            // then
            assertThat(zipContent).isNotEmpty();

            try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipContent))) {
                ZipEntry entry = zis.getNextEntry();
                assertThat(entry.getName()).isEqualTo("single.txt");

                byte[] content = zis.readAllBytes();
                assertThat(new String(content)).isEqualTo("single content");
            }
        }

        @Test
        @DisplayName("[success] 폴더 구조를 포함한 파일을 ZIP으로 압축한다")
        void success_withFolderStructure() throws Exception {
            // given
            List<FileInfo> fileInfos = new ArrayList<>();
            fileInfos.add(new FileInfo("folder1/file1.txt",
                new ByteArrayInputStream("content1".getBytes())));
            fileInfos.add(new FileInfo("folder2/subfolder/file2.txt",
                new ByteArrayInputStream("content2".getBytes())));

            // when
            byte[] zipContent = ZipUtil.createZip(fileInfos);

            // then
            assertThat(zipContent).isNotEmpty();

            try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipContent))) {
                ZipEntry entry1 = zis.getNextEntry();
                assertThat(entry1.getName()).isEqualTo("folder1/file1.txt");

                ZipEntry entry2 = zis.getNextEntry();
                assertThat(entry2.getName()).isEqualTo("folder2/subfolder/file2.txt");
            }
        }

        @Test
        @DisplayName("[success] 빈 파일을 ZIP으로 압축한다")
        void success_emptyFile() throws Exception {
            // given
            List<FileInfo> fileInfos = new ArrayList<>();
            fileInfos.add(new FileInfo("empty.txt",
                new ByteArrayInputStream(new byte[0])));

            // when
            byte[] zipContent = ZipUtil.createZip(fileInfos);

            // then
            assertThat(zipContent).isNotEmpty();

            try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipContent))) {
                ZipEntry entry = zis.getNextEntry();
                assertThat(entry.getName()).isEqualTo("empty.txt");

                byte[] content = zis.readAllBytes();
                assertThat(content).isEmpty();
            }
        }

        @Test
        @DisplayName("[exception] InputStream 읽기 실패 시 예외가 발생한다")
        void exception_inputStreamError() {
            // given
            List<FileInfo> fileInfos = new ArrayList<>();
            InputStream errorStream = new InputStream() {
                @Override
                public int read() {
                    throw new RuntimeException("Read error");
                }
            };
            fileInfos.add(new FileInfo("error.txt", errorStream));

            // when & then
            assertThatThrownBy(() -> ZipUtil.createZip(fileInfos))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("파일 압축 실패");
        }
    }
}
