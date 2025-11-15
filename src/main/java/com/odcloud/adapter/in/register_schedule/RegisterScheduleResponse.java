package com.odcloud.adapter.in.register_schedule;

import com.odcloud.application.service.register_schedule.RegisterScheduleServiceResponse;

record RegisterScheduleResponse(
    Long scheduleId
) {

    static RegisterScheduleResponse of(RegisterScheduleServiceResponse response) {
        return new RegisterScheduleResponse(response.scheduleId());
    }
}
