package com.odcloud.application.service.register_group;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RegisterGroupServiceResponseTest {

    @Nested
    @DisplayName("[ofSuccess] 성공 응답을 생성하는 정적 팩토리 메서드")
    class Describe_ofSuccess {

        @Test
        @DisplayName("[success] 성공 응답을 생성한다")
        void success() {
            // when
            RegisterGroupServiceResponse response = RegisterGroupServiceResponse.ofSuccess();

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();
        }

        @Test
        @DisplayName("[success] 여러 번 호출해도 동일한 값을 반환한다")
        void success_consistent() {
            // when
            RegisterGroupServiceResponse response1 = RegisterGroupServiceResponse.ofSuccess();
            RegisterGroupServiceResponse response2 = RegisterGroupServiceResponse.ofSuccess();

            // then
            assertThat(response1.result()).isEqualTo(response2.result());
            assertThat(response1.result()).isTrue();
        }
    }

    @Nested
    @DisplayName("[constructor] Record 생성자 테스트")
    class Describe_constructor {

        @Test
        @DisplayName("[success] true 값으로 ServiceResponse를 생성한다")
        void success_true() {
            // when
            RegisterGroupServiceResponse response = new RegisterGroupServiceResponse(true);

            // then
            assertThat(response.result()).isTrue();
        }

        @Test
        @DisplayName("[success] false 값으로 ServiceResponse를 생성한다")
        void success_false() {
            // when
            RegisterGroupServiceResponse response = new RegisterGroupServiceResponse(false);

            // then
            assertThat(response.result()).isFalse();
        }

        @Test
        @DisplayName("[success] null 값으로 ServiceResponse를 생성한다")
        void success_null() {
            // when
            RegisterGroupServiceResponse response = new RegisterGroupServiceResponse(null);

            // then
            assertThat(response.result()).isNull();
        }
    }

    @Nested
    @DisplayName("[record] Record 불변성 테스트")
    class Describe_immutability {

        @Test
        @DisplayName("[success] ServiceResponse는 불변 객체이다")
        void success() {
            // given
            RegisterGroupServiceResponse response1 = new RegisterGroupServiceResponse(true);
            RegisterGroupServiceResponse response2 = new RegisterGroupServiceResponse(true);

            // when & then
            assertThat(response1).isEqualTo(response2);
            assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        }

        @Test
        @DisplayName("[success] 다른 값을 가진 ServiceResponse는 동일하지 않다")
        void success_notEquals() {
            // given
            RegisterGroupServiceResponse response1 = new RegisterGroupServiceResponse(true);
            RegisterGroupServiceResponse response2 = new RegisterGroupServiceResponse(false);

            // when & then
            assertThat(response1).isNotEqualTo(response2);
        }

        @Test
        @DisplayName("[success] ofSuccess()로 생성한 응답들은 동일하다")
        void success_ofSuccess_equals() {
            // given
            RegisterGroupServiceResponse response1 = RegisterGroupServiceResponse.ofSuccess();
            RegisterGroupServiceResponse response2 = RegisterGroupServiceResponse.ofSuccess();

            // when & then
            assertThat(response1).isEqualTo(response2);
            assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        }
    }

    @Nested
    @DisplayName("[result] result getter 테스트")
    class Describe_result {

        @Test
        @DisplayName("[success] result 값을 정확히 반환한다")
        void success() {
            // given
            RegisterGroupServiceResponse response = new RegisterGroupServiceResponse(true);

            // when
            Boolean result = response.result();

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("[success] null result 값을 정확히 반환한다")
        void success_null() {
            // given
            RegisterGroupServiceResponse response = new RegisterGroupServiceResponse(null);

            // when
            Boolean result = response.result();

            // then
            assertThat(result).isNull();
        }
    }
}
