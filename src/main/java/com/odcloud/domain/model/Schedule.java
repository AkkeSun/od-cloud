package com.odcloud.domain.model;

import com.odcloud.application.port.in.command.RegisterScheduleCommand;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {

    private Long id;
    private String writerEmail;
    private String groupId;
    private String content;
    private LocalDateTime notificationDt;
    private String notificationYn;
    private LocalDateTime startDt;
    private LocalDateTime endDt;
    private LocalDateTime modDt;
    private LocalDateTime regDt;

    public static Schedule of(RegisterScheduleCommand command) {
        return Schedule.builder()
            .writerEmail(command.account().getEmail())
            .content(command.content())
            .startDt(command.startDt())
            .endDt(command.endDt())
            .groupId(command.groupId())
            .notificationDt(command.notificationDt())
            .notificationYn("N")
            .regDt(LocalDateTime.now())
            .build();
    }

    public boolean isGroupSchedule() {
        return groupId != null;
    }
}
