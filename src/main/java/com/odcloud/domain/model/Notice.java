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
public class Notice {

    private Long id;
    private Long groupId;
    private String title;
    private String content;
    private String writerEmail;
    private LocalDateTime regDt;
    private LocalDateTime modDt;

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
        this.modDt = LocalDateTime.now();
    }
}
