package com.odcloud.adapter.out.persistence.jpa;

import com.odcloud.domain.model.Schedule;
import com.odcloud.domain.model.ScheduleType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SCHEDULE")
class ScheduleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "TITLE", nullable = false)
    private String title;

    @Column(name = "DESCRIPTION", length = 1000)
    private String description;

    @Column(name = "START_DT", nullable = false)
    private LocalDateTime startDt;

    @Column(name = "END_DT", nullable = false)
    private LocalDateTime endDt;

    @Enumerated(EnumType.STRING)
    @Column(name = "SCHEDULE_TYPE", nullable = false)
    private ScheduleType scheduleType;

    @Column(name = "ACCOUNT_ID", nullable = false)
    private Long accountId;

    @Column(name = "GROUP_ID")
    private Long groupId;

    @Column(name = "NOTIFICATION_ENABLED")
    private Boolean notificationEnabled;

    @Column(name = "NOTIFICATION_MINUTES")
    private Integer notificationMinutes;

    @Column(name = "MOD_DT")
    private LocalDateTime modDt;

    @Column(name = "REG_DT")
    private LocalDateTime regDt;

    static ScheduleEntity of(Schedule schedule) {
        return ScheduleEntity.builder()
            .id(schedule.getId())
            .title(schedule.getTitle())
            .description(schedule.getDescription())
            .startDt(schedule.getStartDt())
            .endDt(schedule.getEndDt())
            .scheduleType(schedule.getScheduleType())
            .accountId(schedule.getAccountId())
            .groupId(schedule.getGroupId())
            .notificationEnabled(schedule.getNotificationEnabled())
            .notificationMinutes(schedule.getNotificationMinutes())
            .modDt(schedule.getModDt())
            .regDt(schedule.getRegDt())
            .build();
    }
}
