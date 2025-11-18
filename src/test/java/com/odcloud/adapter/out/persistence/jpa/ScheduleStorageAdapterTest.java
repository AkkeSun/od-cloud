package com.odcloud.adapter.out.persistence.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.IntegrationTestSupport;
import com.odcloud.domain.model.Schedule;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class ScheduleStorageAdapterTest extends IntegrationTestSupport {

    @Autowired
    ScheduleStorageAdapter adapter;

    @Autowired
    EntityManager entityManager;

    @AfterEach
    void tearDown() {
        entityManager.createQuery("DELETE FROM ScheduleEntity").executeUpdate();
        entityManager.flush();
        entityManager.clear();
    }

    @Nested
    @DisplayName("[save] 스케줄을 저장하는 메소드")
    class Describe_save {

        @Test
        @DisplayName("[success] 신규 개인 스케줄을 저장한다")
        void success_newPersonalSchedule() {
            // given
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startDt = LocalDateTime.of(2025, 1, 1, 10, 0);
            LocalDateTime endDt = LocalDateTime.of(2025, 1, 1, 11, 0);
            LocalDateTime notificationDt = LocalDateTime.of(2025, 1, 1, 9, 50);

            Schedule schedule = Schedule.builder()
                .writerEmail("user@example.com")
                .content("개인 회의")
                .startDt(startDt)
                .endDt(endDt)
                .notificationDt(notificationDt)
                .regDt(now)
                .build();

            // when
            adapter.save(schedule);
            entityManager.flush();
            entityManager.clear();

            // then
            ScheduleEntity savedEntity = entityManager
                .createQuery("SELECT s FROM ScheduleEntity s WHERE s.writerEmail = :email", ScheduleEntity.class)
                .setParameter("email", "user@example.com")
                .getSingleResult();

            assertThat(savedEntity).isNotNull();
            assertThat(savedEntity.getWriterEmail()).isEqualTo("user@example.com");
            assertThat(savedEntity.getContent()).isEqualTo("개인 회의");
            assertThat(savedEntity.getStartDt()).isEqualTo(startDt);
            assertThat(savedEntity.getEndDt()).isEqualTo(endDt);
            assertThat(savedEntity.getNotificationDt()).isEqualTo(notificationDt);
            assertThat(savedEntity.getGroupId()).isNull();
        }

        @Test
        @DisplayName("[success] 신규 그룹 스케줄을 저장한다")
        void success_newGroupSchedule() {
            // given
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startDt = LocalDateTime.of(2025, 1, 1, 14, 0);
            LocalDateTime endDt = LocalDateTime.of(2025, 1, 1, 15, 0);

            Schedule schedule = Schedule.builder()
                .writerEmail("user@example.com")
                .content("그룹 회의")
                .startDt(startDt)
                .endDt(endDt)
                .groupId("group-123")
                .regDt(now)
                .build();

            // when
            adapter.save(schedule);
            entityManager.flush();
            entityManager.clear();

            // then
            ScheduleEntity savedEntity = entityManager
                .createQuery("SELECT s FROM ScheduleEntity s WHERE s.groupId = :groupId", ScheduleEntity.class)
                .setParameter("groupId", "group-123")
                .getSingleResult();

            assertThat(savedEntity).isNotNull();
            assertThat(savedEntity.getGroupId()).isEqualTo("group-123");
            assertThat(savedEntity.getContent()).isEqualTo("그룹 회의");
            assertThat(savedEntity.getWriterEmail()).isEqualTo("user@example.com");
        }

        @Test
        @DisplayName("[success] 알림 시간 없이 스케줄을 저장한다")
        void success_withoutNotification() {
            // given
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startDt = LocalDateTime.of(2025, 1, 1, 10, 0);
            LocalDateTime endDt = LocalDateTime.of(2025, 1, 1, 11, 0);

            Schedule schedule = Schedule.builder()
                .writerEmail("user@example.com")
                .content("회의")
                .startDt(startDt)
                .endDt(endDt)
                .notificationDt(null)
                .regDt(now)
                .build();

            // when
            adapter.save(schedule);
            entityManager.flush();
            entityManager.clear();

            // then
            ScheduleEntity savedEntity = entityManager
                .createQuery("SELECT s FROM ScheduleEntity s WHERE s.writerEmail = :email", ScheduleEntity.class)
                .setParameter("email", "user@example.com")
                .getSingleResult();

            assertThat(savedEntity).isNotNull();
            assertThat(savedEntity.getNotificationDt()).isNull();
        }

        @Test
        @DisplayName("[success] 기존 스케줄을 업데이트한다")
        void success_updateSchedule() {
            // given
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startDt = LocalDateTime.of(2025, 1, 1, 10, 0);
            LocalDateTime endDt = LocalDateTime.of(2025, 1, 1, 11, 0);

            ScheduleEntity existingSchedule = ScheduleEntity.builder()
                .writerEmail("user@example.com")
                .content("기존 회의")
                .startDt(startDt)
                .endDt(endDt)
                .regDt(now)
                .build();
            entityManager.persist(existingSchedule);
            entityManager.flush();
            entityManager.clear();

            LocalDateTime newStartDt = LocalDateTime.of(2025, 1, 1, 14, 0);
            LocalDateTime newEndDt = LocalDateTime.of(2025, 1, 1, 15, 0);
            LocalDateTime modDt = LocalDateTime.now();

            Schedule updatedSchedule = Schedule.builder()
                .id(existingSchedule.getId())
                .writerEmail("user@example.com")
                .content("수정된 회의")
                .startDt(newStartDt)
                .endDt(newEndDt)
                .modDt(modDt)
                .regDt(now)
                .build();

            // when
            adapter.save(updatedSchedule);
            entityManager.flush();
            entityManager.clear();

            // then
            ScheduleEntity savedEntity = entityManager.find(ScheduleEntity.class, existingSchedule.getId());
            assertThat(savedEntity).isNotNull();
            assertThat(savedEntity.getContent()).isEqualTo("수정된 회의");
            assertThat(savedEntity.getStartDt()).isEqualTo(newStartDt);
            assertThat(savedEntity.getEndDt()).isEqualTo(newEndDt);
        }

        @Test
        @DisplayName("[success] 여러 스케줄을 저장한다")
        void success_multipleSchedules() {
            // given
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startDt1 = LocalDateTime.of(2025, 1, 1, 10, 0);
            LocalDateTime endDt1 = LocalDateTime.of(2025, 1, 1, 11, 0);

            Schedule schedule1 = Schedule.builder()
                .writerEmail("user1@example.com")
                .content("회의 1")
                .startDt(startDt1)
                .endDt(endDt1)
                .regDt(now)
                .build();

            LocalDateTime startDt2 = LocalDateTime.of(2025, 1, 1, 14, 0);
            LocalDateTime endDt2 = LocalDateTime.of(2025, 1, 1, 15, 0);

            Schedule schedule2 = Schedule.builder()
                .writerEmail("user2@example.com")
                .content("회의 2")
                .startDt(startDt2)
                .endDt(endDt2)
                .regDt(now)
                .build();

            // when
            adapter.save(schedule1);
            adapter.save(schedule2);
            entityManager.flush();
            entityManager.clear();

            // then
            long count = entityManager
                .createQuery("SELECT COUNT(s) FROM ScheduleEntity s", Long.class)
                .getSingleResult();

            assertThat(count).isEqualTo(2);
        }

        @Test
        @DisplayName("[success] 모든 필드가 채워진 스케줄을 저장한다")
        void success_allFields() {
            // given
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startDt = LocalDateTime.of(2025, 1, 1, 10, 0);
            LocalDateTime endDt = LocalDateTime.of(2025, 1, 1, 11, 0);
            LocalDateTime notificationDt = LocalDateTime.of(2025, 1, 1, 9, 50);
            LocalDateTime modDt = LocalDateTime.now();

            Schedule schedule = Schedule.builder()
                .writerEmail("user@example.com")
                .content("전체 필드 회의")
                .startDt(startDt)
                .endDt(endDt)
                .notificationDt(notificationDt)
                .groupId("group-123")
                .modDt(modDt)
                .regDt(now)
                .build();

            // when
            adapter.save(schedule);
            entityManager.flush();
            entityManager.clear();

            // then
            ScheduleEntity savedEntity = entityManager
                .createQuery("SELECT s FROM ScheduleEntity s WHERE s.writerEmail = :email", ScheduleEntity.class)
                .setParameter("email", "user@example.com")
                .getSingleResult();

            assertThat(savedEntity).isNotNull();
            assertThat(savedEntity.getWriterEmail()).isEqualTo("user@example.com");
            assertThat(savedEntity.getContent()).isEqualTo("전체 필드 회의");
            assertThat(savedEntity.getStartDt()).isEqualTo(startDt);
            assertThat(savedEntity.getEndDt()).isEqualTo(endDt);
            assertThat(savedEntity.getNotificationDt()).isEqualTo(notificationDt);
            assertThat(savedEntity.getGroupId()).isEqualTo("group-123");
        }

        @Test
        @DisplayName("[success] ID가 자동으로 생성된다")
        void success_autoGeneratedId() {
            // given
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startDt = LocalDateTime.of(2025, 1, 1, 10, 0);
            LocalDateTime endDt = LocalDateTime.of(2025, 1, 1, 11, 0);

            Schedule schedule = Schedule.builder()
                .writerEmail("user@example.com")
                .content("회의")
                .startDt(startDt)
                .endDt(endDt)
                .regDt(now)
                .build();

            // when
            adapter.save(schedule);
            entityManager.flush();
            entityManager.clear();

            // then
            ScheduleEntity savedEntity = entityManager
                .createQuery("SELECT s FROM ScheduleEntity s WHERE s.writerEmail = :email", ScheduleEntity.class)
                .setParameter("email", "user@example.com")
                .getSingleResult();

            assertThat(savedEntity.getId()).isNotNull();
            assertThat(savedEntity.getId()).isGreaterThan(0);
        }
    }

    @Nested
    @DisplayName("[findById] 스케줄 ID로 스케줄을 조회하는 메소드")
    class Describe_findById {

        @Test
        @DisplayName("[success] 스케줄 ID로 스케줄을 조회한다")
        void success() {
            // given
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startDt = LocalDateTime.of(2025, 1, 1, 10, 0);
            LocalDateTime endDt = LocalDateTime.of(2025, 1, 1, 11, 0);
            LocalDateTime notificationDt = LocalDateTime.of(2025, 1, 1, 9, 50);

            ScheduleEntity entity = ScheduleEntity.builder()
                .writerEmail("user@example.com")
                .content("개인 회의")
                .startDt(startDt)
                .endDt(endDt)
                .notificationDt(notificationDt)
                .notificationYn("N")
                .regDt(now)
                .build();
            entityManager.persist(entity);
            entityManager.flush();
            entityManager.clear();

            // when
            Schedule result = adapter.findById(entity.getId());

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(entity.getId());
            assertThat(result.getWriterEmail()).isEqualTo("user@example.com");
            assertThat(result.getContent()).isEqualTo("개인 회의");
            assertThat(result.getStartDt()).isEqualTo(startDt);
            assertThat(result.getEndDt()).isEqualTo(endDt);
            assertThat(result.getNotificationDt()).isEqualTo(notificationDt);
            assertThat(result.getNotificationYn()).isEqualTo("N");
        }

        @Test
        @DisplayName("[success] 그룹 스케줄을 조회한다")
        void success_groupSchedule() {
            // given
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startDt = LocalDateTime.of(2025, 1, 1, 14, 0);
            LocalDateTime endDt = LocalDateTime.of(2025, 1, 1, 15, 0);

            ScheduleEntity entity = ScheduleEntity.builder()
                .writerEmail("user@example.com")
                .groupId("group-123")
                .content("그룹 회의")
                .startDt(startDt)
                .endDt(endDt)
                .notificationYn("N")
                .regDt(now)
                .build();
            entityManager.persist(entity);
            entityManager.flush();
            entityManager.clear();

            // when
            Schedule result = adapter.findById(entity.getId());

            // then
            assertThat(result).isNotNull();
            assertThat(result.getGroupId()).isEqualTo("group-123");
            assertThat(result.getContent()).isEqualTo("그룹 회의");
        }

        @Test
        @DisplayName("[error] 존재하지 않는 스케줄 ID로 조회 시 예외를 발생시킨다")
        void error_notFound() {
            // when & then
            org.junit.jupiter.api.Assertions.assertThrows(
                com.odcloud.infrastructure.exception.CustomBusinessException.class,
                () -> adapter.findById(999L)
            );
        }
    }

    @Nested
    @Transactional
    @DisplayName("[delete] 스케줄을 삭제하는 메소드")
    class Describe_delete {

        @Test
        @DisplayName("[success] 스케줄을 삭제한다")
        void success() {
            // given
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startDt = LocalDateTime.of(2025, 1, 1, 10, 0);
            LocalDateTime endDt = LocalDateTime.of(2025, 1, 1, 11, 0);

            ScheduleEntity entity = ScheduleEntity.builder()
                .writerEmail("user@example.com")
                .content("개인 회의")
                .startDt(startDt)
                .endDt(endDt)
                .notificationYn("N")
                .regDt(now)
                .build();
            entityManager.persist(entity);
            entityManager.flush();
            Long scheduleId = entity.getId();
            entityManager.clear();

            // when
            adapter.delete(entity.toDomain());
            entityManager.flush();
            entityManager.clear();

            // then
            ScheduleEntity deletedEntity = entityManager.find(ScheduleEntity.class, scheduleId);
            assertThat(deletedEntity).isNull();
        }

        @Test
        @DisplayName("[success] 여러 스케줄 중 특정 스케줄만 삭제한다")
        void success_deleteSpecific() {
            // given
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startDt = LocalDateTime.of(2025, 1, 1, 10, 0);
            LocalDateTime endDt = LocalDateTime.of(2025, 1, 1, 11, 0);

            ScheduleEntity entity1 = ScheduleEntity.builder()
                .writerEmail("user1@example.com")
                .content("회의 1")
                .startDt(startDt)
                .endDt(endDt)
                .notificationYn("N")
                .regDt(now)
                .build();

            ScheduleEntity entity2 = ScheduleEntity.builder()
                .writerEmail("user2@example.com")
                .content("회의 2")
                .startDt(startDt)
                .endDt(endDt)
                .notificationYn("N")
                .regDt(now)
                .build();

            entityManager.persist(entity1);
            entityManager.persist(entity2);
            entityManager.flush();
            Long scheduleId1 = entity1.getId();
            Long scheduleId2 = entity2.getId();
            entityManager.clear();

            // when
            adapter.delete(entity1.toDomain());
            entityManager.flush();
            entityManager.clear();

            // then
            ScheduleEntity deletedEntity = entityManager.find(ScheduleEntity.class, scheduleId1);
            ScheduleEntity remainingEntity = entityManager.find(ScheduleEntity.class, scheduleId2);

            assertThat(deletedEntity).isNull();
            assertThat(remainingEntity).isNotNull();
            assertThat(remainingEntity.getContent()).isEqualTo("회의 2");
        }

        @Test
        @DisplayName("[success] 존재하지 않는 스케줄 삭제 시 예외가 발생하지 않는다")
        void success_deleteNonExistent() {
            // when & then
            org.junit.jupiter.api.Assertions.assertDoesNotThrow(
                () -> {
                    adapter.delete(ScheduleEntity.builder().id(999L).build().toDomain());
                    entityManager.flush();
                }
            );
        }
    }
}
