package com.odcloud.adapter.in.update_schedule;

import com.odcloud.application.port.in.command.UpdateScheduleCommand;
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
class UpdateScheduleRequest {

    @NotBlank(message = "제목은 필수값입니다")
    private String title;

    private String description;

    @NotNull(message = "시작일시는 필수값입니다")
    private LocalDateTime startDt;

    @NotNull(message = "종료일시는 필수값입니다")
    private LocalDateTime endDt;

    private Boolean notificationEnabled;

    private Integer notificationMinutes;

    UpdateScheduleCommand toCommand(Long scheduleId, Long accountId) {
        return UpdateScheduleCommand.builder()
            .scheduleId(scheduleId)
            .accountId(accountId)
            .title(title)
            .description(description)
            .startDt(startDt)
            .endDt(endDt)
            .notificationEnabled(notificationEnabled)
            .notificationMinutes(notificationMinutes)
            .build();
    }
}
