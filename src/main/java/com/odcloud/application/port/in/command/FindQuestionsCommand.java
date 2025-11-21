package com.odcloud.application.port.in.command;

import lombok.Builder;

@Builder
public record FindQuestionsCommand(
    Integer page,
    Integer size
) {

}
