package com.odcloud.adapter.in.find_questions;

import com.odcloud.application.port.in.command.FindQuestionsCommand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
class FindQuestionsRequest {

    private Integer page;

    private Integer size;

    FindQuestionsCommand toCommand() {
        return FindQuestionsCommand.builder()
            .page(page != null ? page : 0)
            .size(size != null ? size : 10)
            .build();
    }
}
