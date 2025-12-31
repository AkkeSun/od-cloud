package com.odcloud.domain.model;

import com.odcloud.application.question.port.in.command.RegisterAnswerCommand;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Answer {

    private Long id;
    private Long questionId;
    private String writerEmail;
    private String writerNickname;
    private String content;
    private LocalDateTime modDt;
    private LocalDateTime regDt;

    public static Answer create(RegisterAnswerCommand command) {
        return Answer.builder()
            .questionId(command.questionId())
            .writerEmail(command.account().getEmail())
            .writerNickname(command.account().getNickname())
            .content(command.content())
            .regDt(LocalDateTime.now())
            .build();
    }

    public void updateContent(String content) {
        this.content = content;
        this.modDt = LocalDateTime.now();
    }
}
