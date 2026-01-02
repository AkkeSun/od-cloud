package com.odcloud.application.group.service.delete_group;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class DeleteGroupServiceResponseTest {

    @Nested
    @DisplayName("[constructor] Constructor 테스트")
    class Describe_constructor {

        @Test
        @DisplayName("[success] Constructor로 DeleteGroupServiceResponse를 생성한다")
        void success() {
            // when
            DeleteGroupServiceResponse response = new DeleteGroupServiceResponse(Boolean.TRUE);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();
        }

        @Test
        @DisplayName("[success] null 값으로 Constructor로 DeleteGroupServiceResponse를 생성한다")
        void success_nullValue() {
            // when
            DeleteGroupServiceResponse response = new DeleteGroupServiceResponse(null);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isNull();
        }
    }

    @Nested
    @DisplayName("[ofSuccess] 팩토리 메서드 테스트")
    class Describe_ofSuccess {

        @Test
        @DisplayName("[success] ofSuccess()로 성공 응답을 생성한다")
        void success() {
            // when
            DeleteGroupServiceResponse response = DeleteGroupServiceResponse.ofSuccess();

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();
        }
    }

    @Nested
    @DisplayName("[accessor] Record accessor 메서드 테스트")
    class Describe_accessor {

        @Test
        @DisplayName("[success] result()로 result를 조회한다")
        void success_result() {
            // given
            DeleteGroupServiceResponse response = new DeleteGroupServiceResponse(Boolean.TRUE);

            // when
            Boolean result = response.result();

            // then
            assertThat(result).isTrue();
        }
    }

    @Nested
    @DisplayName("[record] Record 불변성 테스트")
    class Describe_immutability {

        @Test
        @DisplayName("[success] DeleteGroupServiceResponse는 불변 객체이다")
        void success() {
            // given
            DeleteGroupServiceResponse response1 = new DeleteGroupServiceResponse(Boolean.TRUE);
            DeleteGroupServiceResponse response2 = new DeleteGroupServiceResponse(Boolean.TRUE);

            // when & then
            assertThat(response1).isEqualTo(response2);
            assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        }

        @Test
        @DisplayName("[success] 다른 값으로 생성된 DeleteGroupServiceResponse는 동등하지 않다")
        void success_notEqual() {
            // given
            DeleteGroupServiceResponse response1 = new DeleteGroupServiceResponse(Boolean.TRUE);
            DeleteGroupServiceResponse response2 = new DeleteGroupServiceResponse(Boolean.FALSE);

            // when & then
            assertThat(response1).isNotEqualTo(response2);
        }
    }

    @Nested
    @DisplayName("[toString] Record toString 메서드 테스트")
    class Describe_toString {

        @Test
        @DisplayName("[success] toString()으로 문자열 표현을 반환한다")
        void success() {
            // given
            DeleteGroupServiceResponse response = new DeleteGroupServiceResponse(Boolean.TRUE);

            // when
            String result = response.toString();

            // then
            assertThat(result).isNotNull();
            assertThat(result).contains("DeleteGroupServiceResponse");
            assertThat(result).contains("true");
        }

        @Test
        @DisplayName("[success] null 값을 포함한 toString()을 반환한다")
        void success_nullValue() {
            // given
            DeleteGroupServiceResponse response = new DeleteGroupServiceResponse(null);

            // when
            String result = response.toString();

            // then
            assertThat(result).isNotNull();
            assertThat(result).contains("DeleteGroupServiceResponse");
            assertThat(result).contains("null");
        }
    }
}
