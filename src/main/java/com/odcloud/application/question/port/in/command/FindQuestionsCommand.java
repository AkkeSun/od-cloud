package com.odcloud.application.question.port.in.command;

import lombok.Builder;

@Builder
public record FindQuestionsCommand(
    Integer page,
    Integer size
) {

}
