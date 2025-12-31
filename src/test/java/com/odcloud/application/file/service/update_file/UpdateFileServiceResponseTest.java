package com.odcloud.application.file.service.update_file;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UpdateFileServiceResponseTest {

    @Test
    @DisplayName("성공 응답을 생성한다")
    void ofSuccess() {
        // when
        UpdateFileServiceResponse response = UpdateFileServiceResponse.ofSuccess();

        // then
        assertThat(response.result()).isTrue();
    }

    @Test
    @DisplayName("result 값이 올바르게 설정된다")
    void result() {
        // given
        UpdateFileServiceResponse response = new UpdateFileServiceResponse(Boolean.TRUE);

        // when & then
        assertThat(response.result()).isTrue();
    }
}
