package com.odcloud.application.group.service.update_group;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UpdateGroupResponseTest {

    @Nested
    @DisplayName("[constructor] Constructor 테스트")
    class Describe_constructor {

        @Test
        @DisplayName("[success] Constructor로 UpdateGroupResponse를 생성한다")
        void success() {
            // when
            UpdateGroupResponse response = new UpdateGroupResponse(
                Boolean.TRUE
            );

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();
        }

        @Test
        @DisplayName("[success] null 값으로 Constructor로 UpdateGroupResponse를 생성한다")
        void success_nullValue() {
            // when
            UpdateGroupResponse response = new UpdateGroupResponse(
                null
            );

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isNull();
        }
    }
}
