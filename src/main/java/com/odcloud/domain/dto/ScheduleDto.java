package com.odcloud.domain.dto;

import com.odcloud.domain.model.Schedule;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ScheduleDto(
    Long id,
    String writerEmail,
    String groupId,
    String content,
    LocalDateTime startDt,
    LocalDateTime endDt,
    LocalDateTime notificationDt,
    String notificationYn,
    LocalDateTime modDt,
    LocalDateTime regDt
) {

    public static ScheduleDto from(Schedule schedule) {
        return ScheduleDto.builder()
            .id(schedule.getId())
            .writerEmail(schedule.getWriterEmail())
            .groupId(schedule.getGroupId())
            .content(schedule.getContent())
            .startDt(schedule.getStartDt())
            .endDt(schedule.getEndDt())
            .notificationDt(schedule.getNotificationDt())
            .notificationYn(schedule.getNotificationYn())
            .modDt(schedule.getModDt())
            .regDt(schedule.getRegDt())
            .build();
    }
}
