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
public class Answer {

    private Long id;
    private Long questionId;
    private String writerEmail;
    private String writerNickname;
    private String content;
    private LocalDateTime modDt;
    private LocalDateTime regDt;

    public void updateContent(String content) {
        this.content = content;
        this.modDt = LocalDateTime.now();
    }
}
