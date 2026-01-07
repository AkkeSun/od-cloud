package com.odcloud.application.schedule.service.find_schedules;

import com.odcloud.domain.model.Schedule;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record FindSchedulesServiceResponse(
    List<FindSchedulesServiceResponseItem> schedules
) {

    public static FindSchedulesServiceResponse of(List<Schedule> schedules) {
        return FindSchedulesServiceResponse.builder()
            .schedules(schedules.stream()
                .map(FindSchedulesServiceResponseItem::of)
                .toList())
            .build();
    }

    @Builder
    public record FindSchedulesServiceResponseItem(
        Long id,
        String writerEmail,
        Long groupId,
        String content,
        LocalDateTime startDt,
        LocalDateTime notificationDt
    ) {

        public static FindSchedulesServiceResponseItem of(Schedule schedule) {
            return FindSchedulesServiceResponseItem.builder()
                .id(schedule.getId())
                .writerEmail(schedule.getWriterEmail())
                .groupId(schedule.getGroupId())
                .content(schedule.getContent())
                .startDt(schedule.getStartDt())
                .notificationDt(schedule.getNotificationDt())
                .build();
        }
    }
}
