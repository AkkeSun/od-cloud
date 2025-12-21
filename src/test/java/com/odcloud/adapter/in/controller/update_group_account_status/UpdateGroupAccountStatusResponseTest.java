package com.odcloud.adapter.in.controller.update_group_account_status;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.application.service.update_group_account_status.UpdateGroupAccountStatusServiceResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UpdateGroupAccountStatusResponseTest {

    @Nested
    @DisplayName("[of] ServiceResponse를 Response로 변환하는 정적 팩토리 메서드")
    class Describe_of {

        @Test
        @DisplayName("[success] ServiceResponse를 Response로 변환한다")
        void success() {
            // given
            UpdateGroupAccountStatusServiceResponse serviceResponse =
                UpdateGroupAccountStatusServiceResponse.ofSuccess();

            // when
            UpdateGroupAccountStatusResponse response = UpdateGroupAccountStatusResponse.of(
                serviceResponse);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();
        }

        @Test
        @DisplayName("[success] false 값을 포함한 ServiceResponse를 Response로 변환한다")
        void success_falseValue() {
            // given
            UpdateGroupAccountStatusServiceResponse serviceResponse =
                new UpdateGroupAccountStatusServiceResponse(false);

            // when
            UpdateGroupAccountStatusResponse response = UpdateGroupAccountStatusResponse.of(
                serviceResponse);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isFalse();
        }

        @Test
        @DisplayName("[success] null 값을 포함한 ServiceResponse를 Response로 변환한다")
        void success_nullValue() {
            // given
            UpdateGroupAccountStatusServiceResponse serviceResponse =
                new UpdateGroupAccountStatusServiceResponse(null);

            // when
            UpdateGroupAccountStatusResponse response = UpdateGroupAccountStatusResponse.of(
                serviceResponse);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isNull();
        }
    }

    @Nested
    @DisplayName("[constructor] Response 생성자 테스트")
    class Describe_constructor {

        @Test
        @DisplayName("[success] result로 Response를 생성한다")
        void success() {
            // when
            UpdateGroupAccountStatusResponse response = new UpdateGroupAccountStatusResponse(true);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();
        }

        @Test
        @DisplayName("[success] null 값으로 Response를 생성한다")
        void success_nullValue() {
            // when
            UpdateGroupAccountStatusResponse response = new UpdateGroupAccountStatusResponse(null);

            // then
            assertThat(response.result()).isNull();
        }
    }

    @Nested
    @DisplayName("[record] Record 불변성 테스트")
    class Describe_immutability {

        @Test
        @DisplayName("[success] Response는 불변 객체이다")
        void success() {
            // given
            UpdateGroupAccountStatusResponse response1 = new UpdateGroupAccountStatusResponse(
                true);
            UpdateGroupAccountStatusResponse response2 = new UpdateGroupAccountStatusResponse(
                true);

            // when & then
            assertThat(response1).isEqualTo(response2);
            assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        }

        @Test
        @DisplayName("[success] 다른 값으로 생성된 Response는 동등하지 않다")
        void success_notEqual() {
            // given
            UpdateGroupAccountStatusResponse response1 = new UpdateGroupAccountStatusResponse(
                true);
            UpdateGroupAccountStatusResponse response2 = new UpdateGroupAccountStatusResponse(
                false);

            // when & then
            assertThat(response1).isNotEqualTo(response2);
        }
    }

    @Nested
    @DisplayName("[accessor] Response accessor 메서드 테스트")
    class Describe_accessor {

        @Test
        @DisplayName("[success] result()로 값을 조회한다")
        void success() {
            // given
            UpdateGroupAccountStatusResponse response = new UpdateGroupAccountStatusResponse(true);

            // when
            Boolean result = response.result();

            // then
            assertThat(result).isTrue();
        }
    }

    @Nested
    @DisplayName("[toString] Response toString 메서드 테스트")
    class Describe_toString {

        @Test
        @DisplayName("[success] toString()으로 문자열 표현을 반환한다")
        void success() {
            // given
            UpdateGroupAccountStatusResponse response = new UpdateGroupAccountStatusResponse(true);

            // when
            String result = response.toString();

            // then
            assertThat(result).isNotNull();
            assertThat(result).contains("UpdateGroupAccountStatusResponse");
            assertThat(result).contains("true");
        }
    }
}
