package com.odcloud.application.group.service.update_group;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UpdateGroupServiceResponseTest {

    @Nested
    @DisplayName("[constructor] Constructor 테스트")
    class Describe_constructor {

        @Test
        @DisplayName("[success] Constructor로 UpdateGroupServiceResponse를 생성한다")
        void success() {
            // when
            UpdateGroupServiceResponse response = new UpdateGroupServiceResponse(
                Boolean.TRUE
            );

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();
        }

        @Test
        @DisplayName("[success] null 값으로 Constructor로 UpdateGroupServiceResponse를 생성한다")
        void success_nullValue() {
            // when
            UpdateGroupServiceResponse response = new UpdateGroupServiceResponse(
                null
            );

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isNull();
        }
    }
}
