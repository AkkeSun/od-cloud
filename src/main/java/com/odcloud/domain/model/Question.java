package com.odcloud.domain.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Question {

    private Long id;
    private String writerEmail;
    private String writerNickname;
    private String title;
    private String content;
    private Boolean answered;
    private LocalDateTime modDt;
    private LocalDateTime regDt;

    public void markAsAnswered() {
        this.answered = true;
        this.modDt = LocalDateTime.now();
    }
}
