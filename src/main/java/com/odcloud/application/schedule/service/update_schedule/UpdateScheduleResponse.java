package com.odcloud.application.schedule.service.update_schedule;

import lombok.Builder;

@Builder
public record UpdateScheduleResponse(
    boolean result
) {

    public static UpdateScheduleResponse ofSuccess() {
        return UpdateScheduleResponse.builder()
            .result(true)
            .build();
    }
}
