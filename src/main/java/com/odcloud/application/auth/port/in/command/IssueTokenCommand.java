package com.odcloud.application.auth.port.in.command;

import lombok.Builder;

@Builder
public record IssueTokenCommand(
    String username,
    String opt
) {

}
