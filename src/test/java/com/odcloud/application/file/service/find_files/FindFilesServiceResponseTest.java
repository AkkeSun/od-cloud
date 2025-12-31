package com.odcloud.application.file.service.find_files;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.domain.model.FileInfo;
import com.odcloud.domain.model.FolderInfo;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FindFilesServiceResponseTest {

    @Nested
    @DisplayName("[of] File과 Folder 리스트로 Response 생성")
    class Describe_of {

        @Test
        @DisplayName("[success] File과 Folder 리스트로 Response를 생성한다")
        void success() {
            // given
            LocalDateTime now = LocalDateTime.now();

            FileInfo file1 = FileInfo.builder()
                .id(1L)
                .folderId(10L)
                .fileName("test1.txt")
                .fileLoc("/group1/test1.txt")
                .regDt(now)
                .build();

            FileInfo file2 = FileInfo.builder()
                .id(2L)
                .folderId(10L)
                .fileName("test2.txt")
                .fileLoc("/group1/test2.txt")
                .regDt(now)
                .build();

            FolderInfo folder1 = FolderInfo.builder()
                .id(11L)
                .parentId(10L)
                .groupId("group1")
                .name("Folder 1")
                .owner("owner@example.com")
                .path("/group1/folder1")
                .regDt(now)
                .build();

            FolderInfo folder2 = FolderInfo.builder()
                .id(12L)
                .parentId(10L)
                .groupId("group1")
                .name("Folder 2")
                .owner("owner@example.com")
                .path("/group1/folder2")
                .regDt(now)
                .build();

            List<FileInfo> files = List.of(file1, file2);
            List<FolderInfo> folders = List.of(folder1, folder2);
            Long parentFolderId = 10L;

            // when
            FindFilesServiceResponse response = FindFilesServiceResponse.of(files, folders,
                parentFolderId);

            // then
            assertThat(response).isNotNull();
            assertThat(response.parentFolderId()).isEqualTo(10L);
            assertThat(response.files()).hasSize(2);
            assertThat(response.folders()).hasSize(2);

            // File 검증
            assertThat(response.files().get(0).id()).isEqualTo(1L);
            assertThat(response.files().get(0).name()).isEqualTo("test1.txt");
            assertThat(response.files().get(0).fileLoc()).isEqualTo("/group1/test1.txt");
            assertThat(response.files().get(0).regDt()).isNotNull();

            assertThat(response.files().get(1).id()).isEqualTo(2L);
            assertThat(response.files().get(1).name()).isEqualTo("test2.txt");

            // Folder 검증
            assertThat(response.folders().get(0).id()).isEqualTo(11L);
            assertThat(response.folders().get(0).name()).isEqualTo("Folder 1");
            assertThat(response.folders().get(0).groupId()).isEqualTo("group1");
            assertThat(response.folders().get(0).regDt()).isNotNull();

            assertThat(response.folders().get(1).id()).isEqualTo(12L);
            assertThat(response.folders().get(1).name()).isEqualTo("Folder 2");
        }

        @Test
        @DisplayName("[success] 빈 리스트로 Response를 생성한다")
        void success_emptyLists() {
            // given
            List<FileInfo> files = List.of();
            List<FolderInfo> folders = List.of();
            Long parentFolderId = 10L;

            // when
            FindFilesServiceResponse response = FindFilesServiceResponse.of(files, folders,
                parentFolderId);

            // then
            assertThat(response).isNotNull();
            assertThat(response.parentFolderId()).isEqualTo(10L);
            assertThat(response.files()).isEmpty();
            assertThat(response.folders()).isEmpty();
        }

        @Test
        @DisplayName("[success] parentFolderId가 null이어도 Response를 생성한다")
        void success_nullParentFolderId() {
            // given
            List<FileInfo> files = List.of();
            List<FolderInfo> folders = List.of();
            Long parentFolderId = null;

            // when
            FindFilesServiceResponse response = FindFilesServiceResponse.of(files, folders,
                parentFolderId);

            // then
            assertThat(response).isNotNull();
            assertThat(response.parentFolderId()).isNull();
            assertThat(response.files()).isEmpty();
            assertThat(response.folders()).isEmpty();
        }
    }

    @Nested
    @DisplayName("[FolderResponseItem.of] Folder로 FolderResponseItem 생성")
    class Describe_FolderResponseItem_of {

        @Test
        @DisplayName("[success] Folder로 FolderResponseItem을 생성한다")
        void success() {
            // given
            LocalDateTime now = LocalDateTime.now();
            FolderInfo folder = FolderInfo.builder()
                .id(1L)
                .parentId(0L)
                .groupId("group1")
                .name("Test Folder")
                .owner("owner@example.com")
                .path("/group1/test")
                .regDt(now)
                .build();

            // when
            FindFilesServiceResponse.FolderResponseItem item =
                FindFilesServiceResponse.FolderResponseItem.of(folder);

            // then
            assertThat(item).isNotNull();
            assertThat(item.id()).isEqualTo(1L);
            assertThat(item.name()).isEqualTo("Test Folder");
            assertThat(item.groupId()).isEqualTo("group1");
            assertThat(item.regDt()).isNotNull();
        }

        @Test
        @DisplayName("[success] PRIVATE 폴더로 FolderResponseItem을 생성한다")
        void success_privateFolder() {
            // given
            LocalDateTime now = LocalDateTime.now();
            FolderInfo folder = FolderInfo.builder()
                .id(2L)
                .parentId(1L)
                .groupId("group2")
                .name("Private Folder")
                .owner("owner@example.com")
                .path("/group2/private")
                .regDt(now)
                .build();

            // when
            FindFilesServiceResponse.FolderResponseItem item =
                FindFilesServiceResponse.FolderResponseItem.of(folder);

            // then
            assertThat(item).isNotNull();
            assertThat(item.id()).isEqualTo(2L);
            assertThat(item.name()).isEqualTo("Private Folder");
            assertThat(item.groupId()).isEqualTo("group2");
        }
    }

    @Nested
    @DisplayName("[FileResponseItem.of] File로 FileResponseItem 생성")
    class Describe_FileResponseItem_of {

        @Test
        @DisplayName("[success] File로 FileResponseItem을 생성한다")
        void success() {
            // given
            LocalDateTime now = LocalDateTime.now();
            FileInfo file = FileInfo.builder()
                .id(1L)
                .folderId(10L)
                .fileName("test.txt")
                .fileLoc("/group1/test.txt")
                .regDt(now)
                .build();

            // when
            FindFilesServiceResponse.FileResponseItem item =
                FindFilesServiceResponse.FileResponseItem.of(file);

            // then
            assertThat(item).isNotNull();
            assertThat(item.id()).isEqualTo(1L);
            assertThat(item.name()).isEqualTo("test.txt");
            assertThat(item.fileLoc()).isEqualTo("/group1/test.txt");
            assertThat(item.regDt()).isNotNull();
        }

        @Test
        @DisplayName("[success] 다양한 파일 확장자로 FileResponseItem을 생성한다")
        void success_variousExtensions() {
            // given
            LocalDateTime now = LocalDateTime.now();

            FileInfo pdfFile = FileInfo.builder()
                .id(1L)
                .folderId(10L)
                .fileName("document.pdf")
                .fileLoc("/group1/document.pdf")
                .regDt(now)
                .build();

            FileInfo imageFile = FileInfo.builder()
                .id(2L)
                .folderId(10L)
                .fileName("photo.jpg")
                .fileLoc("/group1/photo.jpg")
                .regDt(now)
                .build();

            // when
            FindFilesServiceResponse.FileResponseItem pdfItem =
                FindFilesServiceResponse.FileResponseItem.of(pdfFile);
            FindFilesServiceResponse.FileResponseItem imageItem =
                FindFilesServiceResponse.FileResponseItem.of(imageFile);

            // then
            assertThat(pdfItem.name()).isEqualTo("document.pdf");
            assertThat(pdfItem.fileLoc()).isEqualTo("/group1/document.pdf");

            assertThat(imageItem.name()).isEqualTo("photo.jpg");
            assertThat(imageItem.fileLoc()).isEqualTo("/group1/photo.jpg");
        }
    }

    @Nested
    @DisplayName("[builder] Builder로 Response 생성")
    class Describe_builder {

        @Test
        @DisplayName("[success] Builder로 Response를 생성한다")
        void success() {
            // given
            FindFilesServiceResponse.FileResponseItem fileItem =
                FindFilesServiceResponse.FileResponseItem.builder()
                    .id(1L)
                    .name("test.txt")
                    .fileLoc("/group1/test.txt")
                    .regDt("2024-01-01 00:00:00")
                    .build();

            FindFilesServiceResponse.FolderResponseItem folderItem =
                FindFilesServiceResponse.FolderResponseItem.builder()
                    .id(2L)
                    .name("Test Folder")
                    .groupId("group1")
                    .regDt("2024-01-01 00:00:00")
                    .build();

            // when
            FindFilesServiceResponse response = FindFilesServiceResponse.builder()
                .parentFolderId(10L)
                .files(List.of(fileItem))
                .folders(List.of(folderItem))
                .build();

            // then
            assertThat(response).isNotNull();
            assertThat(response.parentFolderId()).isEqualTo(10L);
            assertThat(response.files()).hasSize(1);
            assertThat(response.folders()).hasSize(1);
            assertThat(response.files().get(0).name()).isEqualTo("test.txt");
            assertThat(response.folders().get(0).name()).isEqualTo("Test Folder");
        }
    }
}
