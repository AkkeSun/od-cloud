package com.odcloud.application.schedule.service.delete_schedule;

public record DeleteScheduleServiceResponse(
    Boolean result
) {

    public static DeleteScheduleServiceResponse ofSuccess() {
        return new DeleteScheduleServiceResponse(Boolean.TRUE);
    }
}
