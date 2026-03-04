package com.odcloud.application.auth.service.issue_token;

import lombok.Builder;

@Builder
public record IssueTokenCommand(
    String username,
    String opt
) {

}
