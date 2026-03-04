package com.odcloud.application.schedule.service.find_schedules;

import com.odcloud.domain.model.Schedule;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record FindSchedulesResponse(
    List<FindSchedulesResponseItem> schedules
) {

    public static FindSchedulesResponse of(List<Schedule> schedules) {
        return FindSchedulesResponse.builder()
            .schedules(schedules.stream()
                .map(FindSchedulesResponseItem::of)
                .toList())
            .build();
    }

    @Builder
    public record FindSchedulesResponseItem(
        Long id,
        String writerEmail,
        Long groupId,
        String content,
        LocalDateTime startDt,
        LocalDateTime notificationDt
    ) {

        public static FindSchedulesResponseItem of(Schedule schedule) {
            return FindSchedulesResponseItem.builder()
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
