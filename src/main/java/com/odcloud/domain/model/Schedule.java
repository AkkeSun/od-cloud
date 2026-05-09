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
public class Schedule {

    private Long id;
    private String writerEmail;
    private Long groupId;
    private String content;
    private LocalDateTime notificationDt;
    private String notificationYn;
    private LocalDateTime startDt;
    private LocalDateTime modDt;
    private LocalDateTime regDt;

    public boolean isGroupSchedule() {
        return groupId != null;
    }

    public void update(String content, LocalDateTime startDt, LocalDateTime notificationDt) {
        this.content = content;
        this.startDt = startDt;
        this.notificationDt = notificationDt;
        this.modDt = LocalDateTime.now();
    }
}
