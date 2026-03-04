package com.odcloud.application.schedule.service.register_schedule;

public record RegisterScheduleResponse(
    Boolean result
) {

    public static RegisterScheduleResponse ofSuccess() {
        return new RegisterScheduleResponse(Boolean.TRUE);
    }
}
