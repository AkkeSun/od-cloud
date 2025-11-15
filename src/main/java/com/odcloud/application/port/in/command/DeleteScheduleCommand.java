package com.odcloud.application.port.in.command;

import lombok.Builder;

@Builder
public record DeleteScheduleCommand(
    Long scheduleId,
    Long accountId  // 권한 체크용
) {

}
