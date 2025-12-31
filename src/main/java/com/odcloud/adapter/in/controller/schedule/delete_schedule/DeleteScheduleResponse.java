package com.odcloud.adapter.in.controller.schedule.delete_schedule;

import com.odcloud.application.schedule.service.delete_schedule.DeleteScheduleServiceResponse;
import lombok.Builder;

@Builder
record DeleteScheduleResponse(
    Boolean result
) {

    static DeleteScheduleResponse of(DeleteScheduleServiceResponse response) {
        return new DeleteScheduleResponse(response.result());
    }
}
