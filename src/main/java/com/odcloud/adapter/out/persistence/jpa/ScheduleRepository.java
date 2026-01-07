package com.odcloud.adapter.out.persistence.jpa;

import static com.odcloud.adapter.out.persistence.jpa.QScheduleEntity.scheduleEntity;

import com.odcloud.application.schedule.port.in.command.FindSchedulesCommand;
import com.odcloud.domain.model.Schedule;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
class ScheduleRepository {

    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;

    @Transactional
    public void save(Schedule schedule) {
        if (schedule.getId() == null) {
            entityManager.persist(ScheduleEntity.of(schedule));
        } else {
            entityManager.merge(ScheduleEntity.of(schedule));
        }
    }

    public ScheduleEntity findById(Long scheduleId) {
        return entityManager.find(ScheduleEntity.class, scheduleId);
    }

    @Transactional
    public void delete(Schedule schedule) {
        queryFactory
            .delete(scheduleEntity)
            .where(scheduleEntity.id.eq(schedule.getId()))
            .execute();
    }

    public List<ScheduleEntity> findSchedules(FindSchedulesCommand command) {
        return queryFactory
            .selectFrom(scheduleEntity)
            .where(
                scheduleEntity.startDt.between(command.getStartDateTime(),
                    command.getEndDateTime()),
                buildFilterCondition(command)
            )
            .orderBy(scheduleEntity.startDt.asc())
            .fetch();
    }

    private BooleanExpression buildFilterCondition(FindSchedulesCommand command) {
        Long groupId = command.groupId();
        String email = command.account().getEmail();
        List<Long> groupIds = command.account().getGroupIds();

        if (groupId == null) {
            return scheduleEntity.writerEmail.eq(email)
                .and(scheduleEntity.groupId.isNull()).or(scheduleEntity.groupId.in(groupIds));
        }

        if (groupId == 0L) {
            return scheduleEntity.writerEmail.eq(command.account().getEmail())
                .and(scheduleEntity.groupId.isNull());
        }
        return scheduleEntity.groupId.eq(groupId);
    }

    public List<ScheduleEntity> findSchedulesForNotification(LocalDateTime currentTime) {
        return queryFactory
            .selectFrom(scheduleEntity)
            .where(
                scheduleEntity.notificationDt.loe(currentTime),
                scheduleEntity.notificationYn.eq("N"),
                scheduleEntity.startDt.goe(currentTime)
            )
            .fetch();
    }

    @Transactional
    public void updateNotificationYn(List<Long> scheduleIds) {
        queryFactory
            .update(scheduleEntity)
            .set(scheduleEntity.notificationYn, "Y")
            .where(scheduleEntity.id.in(scheduleIds))
            .execute();
    }

    public List<ScheduleEntity> findByWriterEmailAndGroupIdIsNull(String writerEmail) {
        return queryFactory
            .selectFrom(scheduleEntity)
            .where(
                scheduleEntity.writerEmail.eq(writerEmail)
                    .and(scheduleEntity.groupId.isNull())
            )
            .fetch();
    }

    public List<ScheduleEntity> findByGroupId(Long groupId) {
        return queryFactory
            .selectFrom(scheduleEntity)
            .where(scheduleEntity.groupId.eq(groupId))
            .fetch();
    }
}
