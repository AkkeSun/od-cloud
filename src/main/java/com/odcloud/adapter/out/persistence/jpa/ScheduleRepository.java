package com.odcloud.adapter.out.persistence.jpa;

import com.odcloud.domain.model.Schedule;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
class ScheduleRepository {

    private final EntityManager entityManager;

    @Transactional
    public void save(Schedule schedule) {
        if (schedule.getId() == null) {
            entityManager.persist(ScheduleEntity.of(schedule));
        } else {
            entityManager.merge(ScheduleEntity.of(schedule));
        }
    }
}
