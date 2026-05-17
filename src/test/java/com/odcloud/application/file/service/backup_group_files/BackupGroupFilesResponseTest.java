package com.odcloud.application.file.service.backup_group_files;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class BackupGroupFilesResponseTest {

    @Nested
    @DisplayName("[BackupGroupFilesResponse] record 생성")
    class Describe_BackupGroupFilesResponse {

        @Test
        @DisplayName("[success] builder로 생성 시 모든 필드가 올바르게 반환된다")
        void success_builderCreation() {
            // when
            BackupGroupFilesResponse response = BackupGroupFilesResponse.builder()
                .totalGroups(10)
                .successCount(7)
                .failCount(2)
                .skipCount(1)
                .build();

            // then
            assertThat(response.totalGroups()).isEqualTo(10);
            assertThat(response.successCount()).isEqualTo(7);
            assertThat(response.failCount()).isEqualTo(2);
            assertThat(response.skipCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("[success] 모든 카운트가 0인 경우 정상 생성된다")
        void success_allZeroCounts() {
            // when
            BackupGroupFilesResponse response = BackupGroupFilesResponse.builder()
                .totalGroups(0)
                .successCount(0)
                .failCount(0)
                .skipCount(0)
                .build();

            // then
            assertThat(response.totalGroups()).isEqualTo(0);
            assertThat(response.successCount()).isEqualTo(0);
            assertThat(response.failCount()).isEqualTo(0);
            assertThat(response.skipCount()).isEqualTo(0);
        }
    }
}
