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
@Table(name = "schedule")
class ScheduleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "writer_email")
    private String writerEmail;

    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "content")
    private String content;

    @Column(name = "start_dt")
    private LocalDateTime startDt;

    @Column(name = "notification_dt")
    private LocalDateTime notificationDt;

    @Column(name = "notification_yn")
    private String notificationYn;

    @Column(name = "mod_dt")
    private LocalDateTime modDt;

    @Column(name = "reg_dt")
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
            .modDt(modDt)
            .regDt(regDt)
            .build();
    }
}
