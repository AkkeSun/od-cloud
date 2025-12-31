package com.odcloud.application.schedule.service.register_schedule;

public record RegisterScheduleServiceResponse(
    Boolean result
) {

    public static RegisterScheduleServiceResponse ofSuccess() {
        return new RegisterScheduleServiceResponse(Boolean.TRUE);
    }
}
