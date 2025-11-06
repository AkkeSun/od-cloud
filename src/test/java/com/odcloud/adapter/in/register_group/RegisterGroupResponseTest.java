package com.odcloud.adapter.in.register_group;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.application.service.register_group.RegisterGroupServiceResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RegisterGroupResponseTest {

    @Nested
    @DisplayName("[of] ServiceResponse를 Response로 변환하는 정적 팩토리 메서드")
    class Describe_of {

        @Test
        @DisplayName("[success] ServiceResponse를 Response로 변환한다")
        void success() {
            // given
            RegisterGroupServiceResponse serviceResponse = RegisterGroupServiceResponse.ofSuccess();

            // when
            RegisterGroupResponse response = RegisterGroupResponse.of(serviceResponse);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();
        }

        @Test
        @DisplayName("[success] result가 false인 ServiceResponse를 변환한다")
        void success_resultFalse() {
            // given
            RegisterGroupServiceResponse serviceResponse = new RegisterGroupServiceResponse(false);

            // when
            RegisterGroupResponse response = RegisterGroupResponse.of(serviceResponse);

            // then
            assertThat(response.result()).isFalse();
        }

        @Test
        @DisplayName("[success] null result를 가진 ServiceResponse를 변환한다")
        void success_nullResult() {
            // given
            RegisterGroupServiceResponse serviceResponse = new RegisterGroupServiceResponse(null);

            // when
            RegisterGroupResponse response = RegisterGroupResponse.of(serviceResponse);

            // then
            assertThat(response.result()).isNull();
        }
    }

    @Nested
    @DisplayName("[builder] Response 빌더 패턴 테스트")
    class Describe_builder {

        @Test
        @DisplayName("[success] result를 설정하여 Response를 생성한다")
        void success() {
            // when
            RegisterGroupResponse response = RegisterGroupResponse.builder()
                .result(true)
                .build();

            // then
            assertThat(response.result()).isTrue();
        }

        @Test
        @DisplayName("[success] null 값으로 Response를 생성한다")
        void success_nullValue() {
            // when
            RegisterGroupResponse response = RegisterGroupResponse.builder()
                .result(null)
                .build();

            // then
            assertThat(response.result()).isNull();
        }

        @Test
        @DisplayName("[success] result가 false인 Response를 생성한다")
        void success_resultFalse() {
            // when
            RegisterGroupResponse response = RegisterGroupResponse.builder()
                .result(false)
                .build();

            // then
            assertThat(response.result()).isFalse();
        }
    }

    @Nested
    @DisplayName("[record] Record 불변성 테스트")
    class Describe_immutability {

        @Test
        @DisplayName("[success] Response는 불변 객체이다")
        void success() {
            // given
            RegisterGroupResponse response1 = RegisterGroupResponse.builder()
                .result(true)
                .build();

            RegisterGroupResponse response2 = RegisterGroupResponse.builder()
                .result(true)
                .build();

            // when & then
            assertThat(response1).isEqualTo(response2);
            assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        }

        @Test
        @DisplayName("[success] 다른 값을 가진 Response는 동일하지 않다")
        void success_notEquals() {
            // given
            RegisterGroupResponse response1 = RegisterGroupResponse.builder()
                .result(true)
                .build();

            RegisterGroupResponse response2 = RegisterGroupResponse.builder()
                .result(false)
                .build();

            // when & then
            assertThat(response1).isNotEqualTo(response2);
        }
    }
}
