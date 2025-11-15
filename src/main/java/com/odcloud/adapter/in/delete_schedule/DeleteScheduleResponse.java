package com.odcloud.adapter.in.delete_schedule;

import com.odcloud.application.service.delete_schedule.DeleteScheduleServiceResponse;

record DeleteScheduleResponse(
    Boolean result
) {

    static DeleteScheduleResponse of(DeleteScheduleServiceResponse response) {
        return new DeleteScheduleResponse(response.result());
    }
}
