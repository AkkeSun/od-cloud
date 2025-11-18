package com.odcloud.application.service.register_schedule;

public record RegisterScheduleServiceResponse(
    Boolean result
) {

    public static RegisterScheduleServiceResponse ofSuccess() {
        return new RegisterScheduleServiceResponse(Boolean.TRUE);
    }
}
