package com.odcloud.adapter.in.controller.find_schedules;

import com.odcloud.application.service.find_schedules.FindSchedulesServiceResponse;
import com.odcloud.application.service.find_schedules.FindSchedulesServiceResponse.FindSchedulesServiceResponseItem;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
record FindSchedulesResponse(
    List<ScheduleInfo> schedules
) {

    static FindSchedulesResponse of(FindSchedulesServiceResponse response) {
        return FindSchedulesResponse.builder()
            .schedules(response.schedules().stream()
                .map(ScheduleInfo::of)
                .toList())
            .build();
    }

    @Builder
    record ScheduleInfo(
        Long id,
        String writerEmail,
        String groupId,
        String content,
        LocalDateTime startDt,
        LocalDateTime notificationDt
    ) {

        static ScheduleInfo of(FindSchedulesServiceResponseItem dto) {
            return ScheduleInfo.builder()
                .id(dto.id())
                .writerEmail(dto.writerEmail())
                .groupId(dto.groupId())
                .content(dto.content())
                .startDt(dto.startDt())
                .notificationDt(dto.notificationDt())
                .build();
        }
    }
}
