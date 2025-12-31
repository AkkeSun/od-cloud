package com.odcloud.application.schedule.service.update_schedule;

import lombok.Builder;

@Builder
public record UpdateScheduleServiceResponse(
    boolean result
) {

    public static UpdateScheduleServiceResponse ofSuccess() {
        return UpdateScheduleServiceResponse.builder()
            .result(true)
            .build();
    }
}
