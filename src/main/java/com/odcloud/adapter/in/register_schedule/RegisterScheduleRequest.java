package com.odcloud.adapter.in.register_schedule;

import com.odcloud.application.port.in.command.RegisterScheduleCommand;
import com.odcloud.domain.model.ScheduleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
class RegisterScheduleRequest {

    @NotBlank(message = "제목은 필수값입니다")
    private String title;

    private String description;

    @NotNull(message = "시작일시는 필수값입니다")
    private LocalDateTime startDt;

    @NotNull(message = "종료일시는 필수값입니다")
    private LocalDateTime endDt;

    @NotNull(message = "일정 타입은 필수값입니다")
    private ScheduleType scheduleType;

    @NotNull(message = "계정 ID는 필수값입니다")
    private Long accountId;

    private Long groupId;

    private Boolean notificationEnabled;

    private Integer notificationMinutes;

    RegisterScheduleCommand toCommand() {
        return RegisterScheduleCommand.builder()
            .title(title)
            .description(description)
            .startDt(startDt)
            .endDt(endDt)
            .scheduleType(scheduleType)
            .accountId(accountId)
            .groupId(groupId)
            .notificationEnabled(notificationEnabled)
            .notificationMinutes(notificationMinutes)
            .build();
    }
}
