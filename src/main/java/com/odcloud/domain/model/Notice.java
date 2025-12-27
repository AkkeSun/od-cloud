package com.odcloud.domain.model;

import com.odcloud.application.port.in.command.RegisterNoticeCommand;
import com.odcloud.application.port.in.command.UpdateNoticeCommand;
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
    private String groupId;
    private String title;
    private String content;
    private String writerEmail;
    private LocalDateTime regDt;
    private LocalDateTime modDt;

    public static Notice of(RegisterNoticeCommand command) {
        return Notice.builder()
            .groupId(command.groupId())
            .title(command.title())
            .content(command.content())
            .writerEmail(command.account().getEmail())
            .regDt(LocalDateTime.now())
            .build();
    }

    public void update(UpdateNoticeCommand command) {
        this.title = command.title();
        this.content = command.content();
        this.modDt = LocalDateTime.now();
    }
}
