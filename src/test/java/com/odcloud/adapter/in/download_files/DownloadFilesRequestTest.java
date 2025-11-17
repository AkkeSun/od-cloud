package com.odcloud.adapter.in.download_files;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class DownloadFilesRequestTest {

    @Nested
    @DisplayName("[toString] toString 메소드 테스트")
    class Describe_toString {

        @Test
        @DisplayName("[success] 정상적으로 문자열로 변환된다")
        void success() {
            // given
            DownloadFilesRequest request = DownloadFilesRequest.builder()
                .fileIds(List.of(1L, 2L, 3L))
                .build();

            // when
            String result = request.toString();

            // then
            assertThat(result).isNotNull();
            assertThat(result).contains("fileIds");
            assertThat(result).contains("[1,2,3]");
        }
    }

    @Nested
    @DisplayName("[builder] 빌더 패턴 테스트")
    class Describe_builder {

        @Test
        @DisplayName("[success] 빌더로 정상적으로 객체를 생성한다")
        void success() {
            // given
            List<Long> fileIds = List.of(1L, 2L, 3L);

            // when
            DownloadFilesRequest request = DownloadFilesRequest.builder()
                .fileIds(fileIds)
                .build();

            // then
            assertThat(request).isNotNull();
            assertThat(request.getFileIds()).isEqualTo(fileIds);
            assertThat(request.getFileIds()).hasSize(3);
        }

        @Test
        @DisplayName("[success] 단일 파일 ID로 객체를 생성한다")
        void success_singleFile() {
            // given
            List<Long> fileIds = List.of(1L);

            // when
            DownloadFilesRequest request = DownloadFilesRequest.builder()
                .fileIds(fileIds)
                .build();

            // then
            assertThat(request).isNotNull();
            assertThat(request.getFileIds()).hasSize(1);
            assertThat(request.getFileIds().get(0)).isEqualTo(1L);
        }
    }
}
