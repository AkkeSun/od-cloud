package com.odcloud.application.service.update_schedule;

public record UpdateScheduleServiceResponse(
    Boolean result
) {

    public static UpdateScheduleServiceResponse ofSuccess() {
        return new UpdateScheduleServiceResponse(true);
    }
}
