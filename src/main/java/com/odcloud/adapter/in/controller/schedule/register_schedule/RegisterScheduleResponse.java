package com.odcloud.adapter.in.controller.schedule.register_schedule;

import com.odcloud.application.schedule.service.register_schedule.RegisterScheduleServiceResponse;
import lombok.Builder;

@Builder
record RegisterScheduleResponse(
    Boolean result
) {

    static RegisterScheduleResponse of(RegisterScheduleServiceResponse response) {
        return new RegisterScheduleResponse(response.result());
    }
}
