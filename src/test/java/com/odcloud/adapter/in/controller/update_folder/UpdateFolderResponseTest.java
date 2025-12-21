package com.odcloud.adapter.in.controller.update_folder;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.application.service.update_folder.UpdateFolderServiceResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UpdateFolderResponseTest {

    @Nested
    @DisplayName("[of] ServiceResponse를 Response로 변환하는 정적 팩토리 메서드")
    class Describe_of {

        @Test
        @DisplayName("[success] ServiceResponse를 Response로 변환한다")
        void success() {
            // given
            UpdateFolderServiceResponse serviceResponse =
                UpdateFolderServiceResponse.ofSuccess();

            // when
            UpdateFolderResponse response = UpdateFolderResponse.of(serviceResponse);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();
        }

        @Test
        @DisplayName("[success] false 값을 포함한 ServiceResponse를 Response로 변환한다")
        void success_falseValue() {
            // given
            UpdateFolderServiceResponse serviceResponse =
                new UpdateFolderServiceResponse(false);

            // when
            UpdateFolderResponse response = UpdateFolderResponse.of(serviceResponse);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isFalse();
        }
    }

    @Nested
    @DisplayName("[builder] Builder 패턴 테스트")
    class Describe_builder {

        @Test
        @DisplayName("[success] Builder로 Response를 생성한다")
        void success() {
            // when
            UpdateFolderResponse response = UpdateFolderResponse.builder()
                .result(true)
                .build();

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();
        }

        @Test
        @DisplayName("[success] false 값으로 Response를 생성한다")
        void success_falseValue() {
            // when
            UpdateFolderResponse response = UpdateFolderResponse.builder()
                .result(false)
                .build();

            // then
            assertThat(response.result()).isFalse();
        }
    }

    @Nested
    @DisplayName("[accessor] Response accessor 메서드 테스트")
    class Describe_accessor {

        @Test
        @DisplayName("[success] result()로 값을 조회한다")
        void success() {
            // given
            UpdateFolderResponse response = UpdateFolderResponse.builder()
                .result(true)
                .build();

            // when
            Boolean result = response.result();

            // then
            assertThat(result).isTrue();
        }
    }
}
