package com.odcloud.adapter.in.find_files;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.application.service.find_files.FindFilesServiceResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FindFilesResponseTest {

    @Nested
    @DisplayName("[of] ServiceResponse로 Response 생성")
    class Describe_of {

        @Test
        @DisplayName("[success] ServiceResponse로 Response를 생성한다")
        void success() {
            // given
            FindFilesServiceResponse.FileResponseItem fileItem1 =
                FindFilesServiceResponse.FileResponseItem.builder()
                    .id(1L)
                    .name("test1.txt")
                    .fileLoc("/group1/test1.txt")
                    .regDt("2024-01-01 00:00:00")
                    .build();

            FindFilesServiceResponse.FileResponseItem fileItem2 =
                FindFilesServiceResponse.FileResponseItem.builder()
                    .id(2L)
                    .name("test2.txt")
                    .fileLoc("/group1/test2.txt")
                    .regDt("2024-01-02 00:00:00")
                    .build();

            FindFilesServiceResponse.FolderResponseItem folderItem1 =
                FindFilesServiceResponse.FolderResponseItem.builder()
                    .id(11L)
                    .name("Folder 1")
                    .groupId("group1")
                    .regDt("2024-01-01 00:00:00")
                    .build();

            FindFilesServiceResponse.FolderResponseItem folderItem2 =
                FindFilesServiceResponse.FolderResponseItem.builder()
                    .id(12L)
                    .name("Folder 2")
                    .groupId("group1")
                    .regDt("2024-01-02 00:00:00")
                    .build();

            FindFilesServiceResponse serviceResponse = FindFilesServiceResponse.builder()
                .parentFolderId(10L)
                .files(List.of(fileItem1, fileItem2))
                .folders(List.of(folderItem1, folderItem2))
                .build();

            // when
            FindFilesResponse response = FindFilesResponse.of(serviceResponse);

            // then
            assertThat(response).isNotNull();
            assertThat(response.parentFolderId()).isEqualTo(10L);
            assertThat(response.files()).hasSize(2);
            assertThat(response.folders()).hasSize(2);

            // File 검증
            assertThat(response.files().get(0).id()).isEqualTo(1L);
            assertThat(response.files().get(0).name()).isEqualTo("test1.txt");
            assertThat(response.files().get(0).fileLoc()).isEqualTo("/group1/test1.txt");
            assertThat(response.files().get(0).regDt()).isEqualTo("2024-01-01 00:00:00");

            assertThat(response.files().get(1).id()).isEqualTo(2L);
            assertThat(response.files().get(1).name()).isEqualTo("test2.txt");

            // Folder 검증
            assertThat(response.folders().get(0).id()).isEqualTo(11L);
            assertThat(response.folders().get(0).name()).isEqualTo("Folder 1");
            assertThat(response.folders().get(0).groupId()).isEqualTo("group1");
            assertThat(response.folders().get(0).regDt()).isEqualTo("2024-01-01 00:00:00");

            assertThat(response.folders().get(1).id()).isEqualTo(12L);
            assertThat(response.folders().get(1).name()).isEqualTo("Folder 2");
        }

        @Test
        @DisplayName("[success] 빈 ServiceResponse로 Response를 생성한다")
        void success_emptyLists() {
            // given
            FindFilesServiceResponse serviceResponse = FindFilesServiceResponse.builder()
                .parentFolderId(10L)
                .files(List.of())
                .folders(List.of())
                .build();

            // when
            FindFilesResponse response = FindFilesResponse.of(serviceResponse);

            // then
            assertThat(response).isNotNull();
            assertThat(response.parentFolderId()).isEqualTo(10L);
            assertThat(response.files()).isEmpty();
            assertThat(response.folders()).isEmpty();
        }

        @Test
        @DisplayName("[success] parentFolderId가 null인 Response를 생성한다")
        void success_nullParentFolderId() {
            // given
            FindFilesServiceResponse serviceResponse = FindFilesServiceResponse.builder()
                .parentFolderId(null)
                .files(List.of())
                .folders(List.of())
                .build();

            // when
            FindFilesResponse response = FindFilesResponse.of(serviceResponse);

            // then
            assertThat(response).isNotNull();
            assertThat(response.parentFolderId()).isNull();
            assertThat(response.files()).isEmpty();
            assertThat(response.folders()).isEmpty();
        }

        @Test
        @DisplayName("[success] Files만 있는 Response를 생성한다")
        void success_filesOnly() {
            // given
            FindFilesServiceResponse.FileResponseItem fileItem =
                FindFilesServiceResponse.FileResponseItem.builder()
                    .id(1L)
                    .name("test.txt")
                    .fileLoc("/group1/test.txt")
                    .regDt("2024-01-01 00:00:00")
                    .build();

            FindFilesServiceResponse serviceResponse = FindFilesServiceResponse.builder()
                .parentFolderId(10L)
                .files(List.of(fileItem))
                .folders(List.of())
                .build();

            // when
            FindFilesResponse response = FindFilesResponse.of(serviceResponse);

            // then
            assertThat(response.files()).hasSize(1);
            assertThat(response.folders()).isEmpty();
            assertThat(response.files().get(0).name()).isEqualTo("test.txt");
        }

        @Test
        @DisplayName("[success] Folders만 있는 Response를 생성한다")
        void success_foldersOnly() {
            // given
            FindFilesServiceResponse.FolderResponseItem folderItem =
                FindFilesServiceResponse.FolderResponseItem.builder()
                    .id(11L)
                    .name("Test Folder")
                    .groupId("group1")
                    .regDt("2024-01-01 00:00:00")
                    .build();

            FindFilesServiceResponse serviceResponse = FindFilesServiceResponse.builder()
                .parentFolderId(10L)
                .files(List.of())
                .folders(List.of(folderItem))
                .build();

            // when
            FindFilesResponse response = FindFilesResponse.of(serviceResponse);

            // then
            assertThat(response.files()).isEmpty();
            assertThat(response.folders()).hasSize(1);
            assertThat(response.folders().get(0).name()).isEqualTo("Test Folder");
        }
    }

    @Nested
    @DisplayName("[FolderResponse.of] FolderResponseItem으로 FolderResponse 생성")
    class Describe_FolderResponse_of {

        @Test
        @DisplayName("[success] FolderResponseItem으로 FolderResponse를 생성한다")
        void success() {
            // given
            FindFilesServiceResponse.FolderResponseItem item =
                FindFilesServiceResponse.FolderResponseItem.builder()
                    .id(1L)
                    .name("Test Folder")
                    .groupId("group1")
                    .regDt("2024-01-01 00:00:00")
                    .build();

            // when
            FindFilesResponse.FolderResponse response =
                FindFilesResponse.FolderResponse.of(item);

            // then
            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo(1L);
            assertThat(response.name()).isEqualTo("Test Folder");
            assertThat(response.groupId()).isEqualTo("group1");
            assertThat(response.regDt()).isEqualTo("2024-01-01 00:00:00");
        }

        @Test
        @DisplayName("[success] PRIVATE 폴더로 FolderResponse를 생성한다")
        void success_privateFolder() {
            // given
            FindFilesServiceResponse.FolderResponseItem item =
                FindFilesServiceResponse.FolderResponseItem.builder()
                    .id(2L)
                    .name("Private Folder")
                    .groupId("group2")
                    .regDt("2024-01-02 00:00:00")
                    .build();

            // when
            FindFilesResponse.FolderResponse response =
                FindFilesResponse.FolderResponse.of(item);

            // then
            assertThat(response.id()).isEqualTo(2L);
            assertThat(response.name()).isEqualTo("Private Folder");
            assertThat(response.groupId()).isEqualTo("group2");
        }
    }

    @Nested
    @DisplayName("[FileResponse.of] FileResponseItem으로 FileResponse 생성")
    class Describe_FileResponse_of {

        @Test
        @DisplayName("[success] FileResponseItem으로 FileResponse를 생성한다")
        void success() {
            // given
            FindFilesServiceResponse.FileResponseItem item =
                FindFilesServiceResponse.FileResponseItem.builder()
                    .id(1L)
                    .name("test.txt")
                    .fileLoc("/group1/test.txt")
                    .regDt("2024-01-01 00:00:00")
                    .build();

            // when
            FindFilesResponse.FileResponse response =
                FindFilesResponse.FileResponse.of(item);

            // then
            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo(1L);
            assertThat(response.name()).isEqualTo("test.txt");
            assertThat(response.fileLoc()).isEqualTo("/group1/test.txt");
            assertThat(response.regDt()).isEqualTo("2024-01-01 00:00:00");
        }

        @Test
        @DisplayName("[success] 다양한 파일 확장자로 FileResponse를 생성한다")
        void success_variousExtensions() {
            // given
            FindFilesServiceResponse.FileResponseItem pdfItem =
                FindFilesServiceResponse.FileResponseItem.builder()
                    .id(1L)
                    .name("document.pdf")
                    .fileLoc("/group1/document.pdf")
                    .regDt("2024-01-01 00:00:00")
                    .build();

            FindFilesServiceResponse.FileResponseItem imageItem =
                FindFilesServiceResponse.FileResponseItem.builder()
                    .id(2L)
                    .name("photo.jpg")
                    .fileLoc("/group1/photo.jpg")
                    .regDt("2024-01-02 00:00:00")
                    .build();

            // when
            FindFilesResponse.FileResponse pdfResponse =
                FindFilesResponse.FileResponse.of(pdfItem);
            FindFilesResponse.FileResponse imageResponse =
                FindFilesResponse.FileResponse.of(imageItem);

            // then
            assertThat(pdfResponse.name()).isEqualTo("document.pdf");
            assertThat(pdfResponse.fileLoc()).isEqualTo("/group1/document.pdf");

            assertThat(imageResponse.name()).isEqualTo("photo.jpg");
            assertThat(imageResponse.fileLoc()).isEqualTo("/group1/photo.jpg");
        }
    }

    @Nested
    @DisplayName("[builder] Builder로 Response 생성")
    class Describe_builder {

        @Test
        @DisplayName("[success] Builder로 Response를 생성한다")
        void success() {
            // given
            FindFilesResponse.FileResponse fileResponse =
                FindFilesResponse.FileResponse.builder()
                    .id(1L)
                    .name("test.txt")
                    .fileLoc("/group1/test.txt")
                    .regDt("2024-01-01 00:00:00")
                    .build();

            FindFilesResponse.FolderResponse folderResponse =
                FindFilesResponse.FolderResponse.builder()
                    .id(2L)
                    .name("Test Folder")
                    .groupId("group1")
                    .regDt("2024-01-01 00:00:00")
                    .build();

            // when
            FindFilesResponse response = FindFilesResponse.builder()
                .parentFolderId(10L)
                .files(List.of(fileResponse))
                .folders(List.of(folderResponse))
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
