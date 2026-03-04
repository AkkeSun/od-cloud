package com.odcloud.application.schedule.service.delete_schedule;

public record DeleteScheduleResponse(
    Boolean result
) {

    public static DeleteScheduleResponse ofSuccess() {
        return new DeleteScheduleResponse(Boolean.TRUE);
    }
}
