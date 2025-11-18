package com.odcloud.adapter.out.persistence.jpa;

import com.odcloud.domain.model.Schedule;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

    @Column(name = "WRITER_EMAIL")
    private String writerEmail;

    @Column(name = "GROUP_ID")
    private String groupId;

    @Column(name = "CONTENT")
    private String content;

    @Column(name = "START_DT")
    private LocalDateTime startDt;

    @Column(name = "END_DT")
    private LocalDateTime endDt;

    @Column(name = "NOTIFICATION_DT")
    private LocalDateTime notificationDt;

    @Column(name = "NOTIFICATION_YN")
    private String notificationYn;

    @Column(name = "MOD_DT")
    private LocalDateTime modDt;

    @Column(name = "REG_DT")
    private LocalDateTime regDt;

    static ScheduleEntity of(Schedule schedule) {
        return ScheduleEntity.builder()
            .id(schedule.getId())
            .writerEmail(schedule.getWriterEmail())
            .groupId(schedule.getGroupId())
            .content(schedule.getContent())
            .notificationDt(schedule.getNotificationDt())
            .notificationYn(schedule.getNotificationYn())
            .startDt(schedule.getStartDt())
            .endDt(schedule.getEndDt())
            .modDt(schedule.getModDt())
            .regDt(schedule.getRegDt())
            .build();
    }

    Schedule toDomain() {
        return Schedule.builder()
            .id(id)
            .writerEmail(writerEmail)
            .groupId(groupId)
            .content(content)
            .notificationDt(notificationDt)
            .notificationYn(notificationYn)
            .startDt(startDt)
            .endDt(endDt)
            .modDt(modDt)
            .regDt(regDt)
            .build();
    }
}
