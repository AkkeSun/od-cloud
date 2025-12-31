package com.odcloud.adapter.in.controller.schedule.find_schedules;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.application.schedule.service.find_schedules.FindSchedulesServiceResponse;
import com.odcloud.domain.model.Schedule;
import java.time.LocalDateTime;
import java.util.Arrays;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FindSchedulesResponseTest {

    @Nested
    @DisplayName("[of] ServiceResponse를 Response로 변환하는 정적 팩토리 메서드")
    class Describe_of {

        @Test
        @DisplayName("[success] ServiceResponse를 Response로 변환한다")
        void success() {
            // given
            Schedule schedule1 = Schedule.builder()
                .id(1L)
                .writerEmail("user@example.com")
                .groupId(null)
                .content("회의 1")
                .startDt(LocalDateTime.of(2025, 1, 15, 10, 0))
                .notificationDt(null)
                .notificationYn("N")
                .modDt(null)
                .regDt(LocalDateTime.now())
                .build();

            Schedule schedule2 = Schedule.builder()
                .id(2L)
                .writerEmail("user@example.com")
                .groupId("group-1")
                .content("회의 2")
                .startDt(LocalDateTime.of(2025, 1, 15, 14, 0))
                .notificationDt(null)
                .notificationYn("N")
                .modDt(null)
                .regDt(LocalDateTime.now())
                .build();

            FindSchedulesServiceResponse serviceResponse = FindSchedulesServiceResponse.of(
                Arrays.asList(schedule1, schedule2));

            // when
            FindSchedulesResponse response = FindSchedulesResponse.of(serviceResponse);

            // then
            assertThat(response).isNotNull();
            assertThat(response.schedules()).hasSize(2);
            assertThat(response.schedules().get(0).id()).isEqualTo(1L);
            assertThat(response.schedules().get(0).content()).isEqualTo("회의 1");
            assertThat(response.schedules().get(1).id()).isEqualTo(2L);
            assertThat(response.schedules().get(1).content()).isEqualTo("회의 2");
            assertThat(response.schedules().get(1).groupId()).isEqualTo("group-1");
        }
    }
}
