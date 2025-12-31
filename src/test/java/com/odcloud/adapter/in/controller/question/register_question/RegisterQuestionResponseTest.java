package com.odcloud.adapter.in.controller.question.register_question;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.application.question.service.register_question.RegisterQuestionServiceResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RegisterQuestionResponseTest {

    @Nested
    @DisplayName("[of] ServiceResponse로부터 응답을 생성하는 정적 팩토리 메서드")
    class Describe_of {

        @Test
        @DisplayName("[success] ServiceResponse로부터 응답을 생성한다")
        void success() {
            // given
            RegisterQuestionServiceResponse serviceResponse =
                RegisterQuestionServiceResponse.builder()
                    .result(true)
                    .build();

            // when
            RegisterQuestionResponse response = RegisterQuestionResponse.of(serviceResponse);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();
        }

        @Test
        @DisplayName("[success] result가 false인 ServiceResponse로부터 응답을 생성한다")
        void success_falseResult() {
            // given
            RegisterQuestionServiceResponse serviceResponse =
                RegisterQuestionServiceResponse.builder()
                    .result(false)
                    .build();

            // when
            RegisterQuestionResponse response = RegisterQuestionResponse.of(serviceResponse);

            // then
            assertThat(response.result()).isFalse();
        }

        @Test
        @DisplayName("[success] ofSuccess로 생성한 ServiceResponse로부터 응답을 생성한다")
        void success_ofSuccess() {
            // given
            RegisterQuestionServiceResponse serviceResponse =
                RegisterQuestionServiceResponse.ofSuccess();

            // when
            RegisterQuestionResponse response = RegisterQuestionResponse.of(serviceResponse);

            // then
            assertThat(response.result()).isTrue();
        }
    }

    @Nested
    @DisplayName("[builder] Builder 패턴 테스트")
    class Describe_builder {

        @Test
        @DisplayName("[success] Builder로 응답을 생성한다")
        void success() {
            // when
            RegisterQuestionResponse response = RegisterQuestionResponse.builder()
                .result(true)
                .build();

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();
        }

        @Test
        @DisplayName("[success] false 값으로 응답을 생성한다")
        void success_falseResult() {
            // when
            RegisterQuestionResponse response = RegisterQuestionResponse.builder()
                .result(false)
                .build();

            // then
            assertThat(response.result()).isFalse();
        }

        @Test
        @DisplayName("[success] null 값으로 응답을 생성한다")
        void success_nullResult() {
            // when
            RegisterQuestionResponse response = RegisterQuestionResponse.builder()
                .result(null)
                .build();

            // then
            assertThat(response.result()).isNull();
        }
    }

    @Nested
    @DisplayName("[record] Record 불변성 테스트")
    class Describe_immutability {

        @Test
        @DisplayName("[success] 응답은 불변 객체이다")
        void success() {
            // given
            RegisterQuestionResponse response1 = RegisterQuestionResponse.builder()
                .result(true)
                .build();

            RegisterQuestionResponse response2 = RegisterQuestionResponse.builder()
                .result(true)
                .build();

            // when & then
            assertThat(response1).isEqualTo(response2);
            assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        }

        @Test
        @DisplayName("[success] 다른 값으로 생성된 응답은 동등하지 않다")
        void success_notEqual() {
            // given
            RegisterQuestionResponse response1 = RegisterQuestionResponse.builder()
                .result(true)
                .build();

            RegisterQuestionResponse response2 = RegisterQuestionResponse.builder()
                .result(false)
                .build();

            // when & then
            assertThat(response1).isNotEqualTo(response2);
        }
    }

    @Nested
    @DisplayName("[accessor] 응답 accessor 메서드 테스트")
    class Describe_accessor {

        @Test
        @DisplayName("[success] result()로 값을 조회한다")
        void success() {
            // given
            RegisterQuestionResponse response = RegisterQuestionResponse.builder()
                .result(true)
                .build();

            // when
            Boolean result = response.result();

            // then
            assertThat(result).isTrue();
        }
    }

    @Nested
    @DisplayName("[toString] 응답 toString 메서드 테스트")
    class Describe_toString {

        @Test
        @DisplayName("[success] toString()으로 문자열 표현을 반환한다")
        void success() {
            // given
            RegisterQuestionResponse response = RegisterQuestionResponse.builder()
                .result(true)
                .build();

            // when
            String result = response.toString();

            // then
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
        }
    }
}
