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
    private String title;
    private String description;
    private LocalDateTime startDt;
    private LocalDateTime endDt;
    private ScheduleType scheduleType;
    private Long accountId;  // 일정 소유자 (필수)
    private Long groupId;    // 그룹 일정인 경우 그룹 ID
    private Boolean notificationEnabled;  // 알림 사용 여부
    private Integer notificationMinutes;  // 시작 몇 분 전 알림
    private LocalDateTime modDt;
    private LocalDateTime regDt;

    public static Schedule createPersonal(String title, String description,
        LocalDateTime startDt, LocalDateTime endDt,
        Long accountId, Boolean notificationEnabled, Integer notificationMinutes) {
        return Schedule.builder()
            .title(title)
            .description(description)
            .startDt(startDt)
            .endDt(endDt)
            .scheduleType(ScheduleType.PERSONAL)
            .accountId(accountId)
            .groupId(null)
            .notificationEnabled(notificationEnabled != null ? notificationEnabled : false)
            .notificationMinutes(notificationMinutes)
            .regDt(LocalDateTime.now())
            .build();
    }

    public static Schedule createGroup(String title, String description,
        LocalDateTime startDt, LocalDateTime endDt,
        Long accountId, Long groupId, Boolean notificationEnabled, Integer notificationMinutes) {
        if (groupId == null) {
            throw new IllegalArgumentException("그룹 일정은 groupId가 필수입니다.");
        }
        return Schedule.builder()
            .title(title)
            .description(description)
            .startDt(startDt)
            .endDt(endDt)
            .scheduleType(ScheduleType.GROUP)
            .accountId(accountId)
            .groupId(groupId)
            .notificationEnabled(notificationEnabled != null ? notificationEnabled : false)
            .notificationMinutes(notificationMinutes)
            .regDt(LocalDateTime.now())
            .build();
    }

    public void update(String title, String description,
        LocalDateTime startDt, LocalDateTime endDt,
        Boolean notificationEnabled, Integer notificationMinutes) {
        this.title = title;
        this.description = description;
        this.startDt = startDt;
        this.endDt = endDt;
        this.notificationEnabled = notificationEnabled;
        this.notificationMinutes = notificationMinutes;
        this.modDt = LocalDateTime.now();
    }
}
