package com.odcloud.adapter.in.download_file;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.application.port.in.command.DownloadFilesCommand;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class DownloadFilesRequestTest {

    @Nested
    @DisplayName("[toCommand] Command 변환")
    class Describe_toCommand {

        @Test
        @DisplayName("[success] Request를 Command로 변환한다")
        void success() {
            // given
            DownloadFilesRequest request = DownloadFilesRequest.builder()
                .fileIds(List.of(1L, 2L, 3L))
                .build();

            // when
            DownloadFilesCommand command = request.toCommand();

            // then
            assertThat(command.fileIds()).containsExactly(1L, 2L, 3L);
        }

        @Test
        @DisplayName("[success] 빈 파일 ID 목록도 변환할 수 있다")
        void success_emptyList() {
            // given
            DownloadFilesRequest request = DownloadFilesRequest.builder()
                .fileIds(List.of())
                .build();

            // when
            DownloadFilesCommand command = request.toCommand();

            // then
            assertThat(command.fileIds()).isEmpty();
        }
    }
}
