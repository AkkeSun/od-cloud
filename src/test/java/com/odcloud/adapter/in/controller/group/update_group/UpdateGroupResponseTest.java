package com.odcloud.adapter.in.controller.group.update_group;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.application.group.service.update_group.UpdateGroupServiceResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UpdateGroupResponseTest {

    @Nested
    @DisplayName("[of] ServiceResponse를 Response로 변환하는 정적 팩토리 메서드")
    class Describe_of {

        @Test
        @DisplayName("[success] ServiceResponse를 Response로 변환한다")
        void success() {
            // given
            UpdateGroupServiceResponse serviceResponse = new UpdateGroupServiceResponse(
                Boolean.TRUE
            );

            // when
            UpdateGroupResponse response = UpdateGroupResponse.of(serviceResponse);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();
        }

        @Test
        @DisplayName("[success] false 값을 포함한 ServiceResponse를 Response로 변환한다")
        void success_falseValue() {
            // given
            UpdateGroupServiceResponse serviceResponse = new UpdateGroupServiceResponse(
                Boolean.FALSE
            );

            // when
            UpdateGroupResponse response = UpdateGroupResponse.of(serviceResponse);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isFalse();
        }
    }
}
