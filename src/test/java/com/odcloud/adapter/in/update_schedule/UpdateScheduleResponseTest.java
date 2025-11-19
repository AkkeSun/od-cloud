package com.odcloud.adapter.in.update_schedule;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.application.service.update_schedule.UpdateScheduleServiceResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UpdateScheduleResponseTest {

    @Nested
    @DisplayName("[of] ServiceResponse를 Response로 변환하는 정적 팩토리 메서드")
    class Describe_of {

        @Test
        @DisplayName("[success] ServiceResponse를 Response로 변환한다")
        void success() {
            // given
            UpdateScheduleServiceResponse serviceResponse =
                UpdateScheduleServiceResponse.ofSuccess();

            // when
            UpdateScheduleResponse response = UpdateScheduleResponse.of(serviceResponse);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();
        }

        @Test
        @DisplayName("[success] false 값을 포함한 ServiceResponse를 Response로 변환한다")
        void success_falseValue() {
            // given
            UpdateScheduleServiceResponse serviceResponse =
                new UpdateScheduleServiceResponse(false);

            // when
            UpdateScheduleResponse response = UpdateScheduleResponse.of(serviceResponse);

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
            UpdateScheduleResponse response = UpdateScheduleResponse.builder()
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
            UpdateScheduleResponse response = UpdateScheduleResponse.builder()
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
            UpdateScheduleResponse response1 = UpdateScheduleResponse.builder()
                .result(true)
                .build();

            UpdateScheduleResponse response2 = UpdateScheduleResponse.builder()
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
            UpdateScheduleResponse response1 = UpdateScheduleResponse.builder()
                .result(true)
                .build();

            UpdateScheduleResponse response2 = UpdateScheduleResponse.builder()
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
            UpdateScheduleResponse response = UpdateScheduleResponse.builder()
                .result(true)
                .build();

            // when
            boolean result = response.result();

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
            UpdateScheduleResponse response = UpdateScheduleResponse.builder()
                .result(true)
                .build();

            // when
            String result = response.toString();

            // then
            assertThat(result).isNotNull();
            assertThat(result).contains("UpdateScheduleResponse");
            assertThat(result).contains("true");
        }
    }
}
