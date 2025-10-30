package com.odcloud.application.port.in.command;

import lombok.Builder;

@Builder
public record IssueTempTokenCommand(
    String username,
    String password
) {

}
