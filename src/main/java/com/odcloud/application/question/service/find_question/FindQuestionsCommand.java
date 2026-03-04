package com.odcloud.application.question.service.find_question;

import lombok.Builder;

@Builder
public record FindQuestionsCommand(
    Integer page,
    Integer size
) {

}
