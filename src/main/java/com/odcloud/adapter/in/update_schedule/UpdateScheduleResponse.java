package com.odcloud.adapter.in.update_schedule;

import com.odcloud.application.service.update_schedule.UpdateScheduleServiceResponse;

record UpdateScheduleResponse(
    Boolean result
) {

    static UpdateScheduleResponse of(UpdateScheduleServiceResponse response) {
        return new UpdateScheduleResponse(response.result());
    }
}
