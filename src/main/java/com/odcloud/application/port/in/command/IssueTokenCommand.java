package com.odcloud.application.port.in.command;

import lombok.Builder;

@Builder
public record IssueTokenCommand(
    String username,
    String opt
) {

}
