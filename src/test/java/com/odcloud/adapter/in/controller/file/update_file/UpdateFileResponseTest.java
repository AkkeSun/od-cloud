package com.odcloud.adapter.in.controller.file.update_file;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.application.service.update_file.UpdateFileServiceResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UpdateFileResponseTest {

    @Test
    @DisplayName("UpdateFileServiceResponse를 UpdateFileResponse로 변환한다")
    void of() {
        // given
        UpdateFileServiceResponse serviceResponse = UpdateFileServiceResponse.ofSuccess();

        // when
        UpdateFileResponse response = UpdateFileResponse.of(serviceResponse);

        // then
        assertThat(response.result()).isTrue();
    }

    @Test
    @DisplayName("성공한 ServiceResponse를 변환하면 result가 true이다")
    void of_success() {
        // given
        UpdateFileServiceResponse serviceResponse = new UpdateFileServiceResponse(Boolean.TRUE);

        // when
        UpdateFileResponse response = UpdateFileResponse.of(serviceResponse);

        // then
        assertThat(response.result()).isTrue();
    }
}
