package com.odcloud.adapter.in.controller.schedule.update_schedule;

import com.odcloud.application.service.update_schedule.UpdateScheduleServiceResponse;
import lombok.Builder;

@Builder
record UpdateScheduleResponse(
    boolean result
) {

    static UpdateScheduleResponse of(UpdateScheduleServiceResponse response) {
        return UpdateScheduleResponse.builder()
            .result(response.result())
            .build();
    }
}
