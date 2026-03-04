package com.odcloud.adapter.in.controller.schedule.delete_schedule;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.application.schedule.service.delete_schedule.DeleteScheduleResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class DeleteScheduleResponseTest {

    @Nested
    @DisplayName("[of] ServiceResponse를 Response로 변환하는 정적 팩토리 메서드")
    class Describe_of {

        @Test
        @DisplayName("[success] ServiceResponse를 Response로 변환한다")
        void success() {
            // given
            DeleteScheduleResponse serviceResponse =
                DeleteScheduleResponse.ofSuccess();

            // when
            DeleteScheduleResponse response = DeleteScheduleResponse.of(serviceResponse);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();
        }

        @Test
        @DisplayName("[success] false 값을 포함한 ServiceResponse를 Response로 변환한다")
        void success_falseValue() {
            // given
            DeleteScheduleResponse serviceResponse =
                new DeleteScheduleResponse(false);

            // when
            DeleteScheduleResponse response = DeleteScheduleResponse.of(serviceResponse);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isFalse();
        }

        @Test
        @DisplayName("[success] null 값을 포함한 ServiceResponse를 Response로 변환한다")
        void success_nullValue() {
            // given
            DeleteScheduleResponse serviceResponse =
                new DeleteScheduleResponse(null);

            // when
            DeleteScheduleResponse response = DeleteScheduleResponse.of(serviceResponse);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isNull();
        }
    }

    @Nested
    @DisplayName("[builder] Builder 패턴 테스트")
    class Describe_builder {

        @Test
        @DisplayName("[success] Builder로 Response를 생성한다")
        void success() {
            // when
            DeleteScheduleResponse response = DeleteScheduleResponse.builder()
                .result(true)
                .build();

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();
        }

        @Test
        @DisplayName("[success] null 값으로 Response를 생성한다")
        void success_nullValue() {
            // when
            DeleteScheduleResponse response = DeleteScheduleResponse.builder()
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
        @DisplayName("[success] Response는 불변 객체이다")
        void success() {
            // given
            DeleteScheduleResponse response1 = DeleteScheduleResponse.builder()
                .result(true)
                .build();

            DeleteScheduleResponse response2 = DeleteScheduleResponse.builder()
                .result(true)
                .build();

            // when & then
            assertThat(response1).isEqualTo(response2);
            assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        }

        @Test
        @DisplayName("[success] 다른 값으로 생성된 Response는 동등하지 않다")
        void success_notEqual() {
            // given
            DeleteScheduleResponse response1 = DeleteScheduleResponse.builder()
                .result(true)
                .build();

            DeleteScheduleResponse response2 = DeleteScheduleResponse.builder()
                .result(false)
                .build();

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
            DeleteScheduleResponse response = DeleteScheduleResponse.builder()
                .result(true)
                .build();

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
            DeleteScheduleResponse response = DeleteScheduleResponse.builder()
                .result(true)
                .build();

            // when
            String result = response.toString();

            // then
            assertThat(result).isNotNull();
            assertThat(result).contains("DeleteScheduleResponse");
            assertThat(result).contains("true");
        }
    }
}
