package com.odcloud.adapter.out.persistence.jpa;

import static com.odcloud.adapter.out.persistence.jpa.QScheduleEntity.scheduleEntity;

import com.odcloud.domain.model.Schedule;
import com.odcloud.domain.model.ScheduleType;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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

    public Optional<Schedule> findById(Long id) {
        return Optional.ofNullable(queryFactory
            .select(Projections.constructor(Schedule.class,
                scheduleEntity.id,
                scheduleEntity.title,
                scheduleEntity.description,
                scheduleEntity.startDt,
                scheduleEntity.endDt,
                scheduleEntity.scheduleType,
                scheduleEntity.accountId,
                scheduleEntity.groupId,
                scheduleEntity.notificationEnabled,
                scheduleEntity.notificationMinutes,
                scheduleEntity.modDt,
                scheduleEntity.regDt
            ))
            .from(scheduleEntity)
            .where(scheduleEntity.id.eq(id))
            .fetchOne());
    }

    public List<Schedule> findByConditions(Long accountId, ScheduleType scheduleType,
        LocalDateTime startDate, LocalDateTime endDate, List<Long> groupIds) {
        return queryFactory
            .select(Projections.constructor(Schedule.class,
                scheduleEntity.id,
                scheduleEntity.title,
                scheduleEntity.description,
                scheduleEntity.startDt,
                scheduleEntity.endDt,
                scheduleEntity.scheduleType,
                scheduleEntity.accountId,
                scheduleEntity.groupId,
                scheduleEntity.notificationEnabled,
                scheduleEntity.notificationMinutes,
                scheduleEntity.modDt,
                scheduleEntity.regDt
            ))
            .from(scheduleEntity)
            .where(
                accountIdEq(accountId),
                scheduleTypeEq(scheduleType),
                dateRangeBetween(startDate, endDate),
                groupIdsIn(groupIds)
            )
            .orderBy(scheduleEntity.startDt.asc())
            .fetch();
    }

    @Transactional
    public void deleteById(Long id) {
        queryFactory
            .delete(scheduleEntity)
            .where(scheduleEntity.id.eq(id))
            .execute();
    }

    public boolean existsByIdAndAccountId(Long id, Long accountId) {
        return queryFactory
            .selectOne()
            .from(scheduleEntity)
            .where(
                scheduleEntity.id.eq(id),
                scheduleEntity.accountId.eq(accountId)
            )
            .fetchOne() != null;
    }

    private BooleanExpression accountIdEq(Long accountId) {
        return accountId != null ? scheduleEntity.accountId.eq(accountId) : null;
    }

    private BooleanExpression scheduleTypeEq(ScheduleType scheduleType) {
        return scheduleType != null ? scheduleEntity.scheduleType.eq(scheduleType) : null;
    }

    private BooleanExpression dateRangeBetween(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            return null;
        }
        // 일정의 시작일 또는 종료일이 조회 기간에 포함되는 경우
        return scheduleEntity.startDt.before(endDate)
            .and(scheduleEntity.endDt.after(startDate));
    }

    private BooleanExpression groupIdsIn(List<Long> groupIds) {
        return groupIds != null && !groupIds.isEmpty()
            ? scheduleEntity.groupId.in(groupIds)
            : null;
    }
}
