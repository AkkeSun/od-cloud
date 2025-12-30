package com.odcloud.adapter.in.controller.file.register_file;

import com.odcloud.application.service.register_file.RegisterFileServiceResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RegisterFileResponseTest {

    @Nested
    @DisplayName("[of] 서비스 응답 객체를 API 응답 객체로 변환한다")
    class Describe_of {

        @Test
        @DisplayName("[success] 서비스 응답 객체가 API 응답 객체로 잘 변환한다")
        void success() {
            // given
            RegisterFileServiceResponse response = RegisterFileServiceResponse.ofSuccess();

            // when
            RegisterFileResponse result = RegisterFileResponse.of(response);

            // then
            assert result.result().equals(response.result());
        }
    }
}