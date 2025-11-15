package com.odcloud.application.service.register_schedule;

public record RegisterScheduleServiceResponse(
    Long scheduleId
) {

    public static RegisterScheduleServiceResponse of(Long scheduleId) {
        return new RegisterScheduleServiceResponse(scheduleId);
    }
}
