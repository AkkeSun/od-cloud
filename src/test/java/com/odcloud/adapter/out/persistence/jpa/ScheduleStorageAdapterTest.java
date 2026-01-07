package com.odcloud.adapter.out.persistence.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.IntegrationTestSupport;
import com.odcloud.application.schedule.port.in.command.FindSchedulesCommand;
import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.Group;
import com.odcloud.domain.model.Schedule;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
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
            LocalDateTime notificationDt = LocalDateTime.of(2025, 1, 1, 9, 50);

            Schedule schedule = Schedule.builder()
                .writerEmail("user@example.com")
                .content("개인 회의")
                .startDt(startDt)
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
            assertThat(savedEntity.getNotificationDt()).isEqualTo(notificationDt);
            assertThat(savedEntity.getGroupId()).isNull();
        }

        @Test
        @DisplayName("[success] 신규 그룹 스케줄을 저장한다")
        void success_newGroupSchedule() {
            // given
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startDt = LocalDateTime.of(2025, 1, 1, 14, 0);

            Schedule schedule = Schedule.builder()
                .writerEmail("user@example.com")
                .content("그룹 회의")
                .startDt(startDt)
                .groupId(1L)
                .regDt(now)
                .build();

            // when
            adapter.save(schedule);
            entityManager.flush();
            entityManager.clear();

            // then
            ScheduleEntity savedEntity = entityManager
                .createQuery("SELECT s FROM ScheduleEntity s WHERE s.groupId = :groupId", ScheduleEntity.class)
                .setParameter("groupId", 1L)
                .getSingleResult();

            assertThat(savedEntity).isNotNull();
            assertThat(savedEntity.getGroupId()).isEqualTo(1L);
            assertThat(savedEntity.getContent()).isEqualTo("그룹 회의");
            assertThat(savedEntity.getWriterEmail()).isEqualTo("user@example.com");
        }

        @Test
        @DisplayName("[success] 알림 시간 없이 스케줄을 저장한다")
        void success_withoutNotification() {
            // given
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startDt = LocalDateTime.of(2025, 1, 1, 10, 0);

            Schedule schedule = Schedule.builder()
                .writerEmail("user@example.com")
                .content("회의")
                .startDt(startDt)
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

            ScheduleEntity existingSchedule = ScheduleEntity.builder()
                .writerEmail("user@example.com")
                .content("기존 회의")
                .startDt(startDt)
                .regDt(now)
                .build();
            entityManager.persist(existingSchedule);
            entityManager.flush();
            entityManager.clear();

            LocalDateTime newStartDt = LocalDateTime.of(2025, 1, 1, 14, 0);
            LocalDateTime modDt = LocalDateTime.now();

            Schedule updatedSchedule = Schedule.builder()
                .id(existingSchedule.getId())
                .writerEmail("user@example.com")
                .content("수정된 회의")
                .startDt(newStartDt)
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
        }

        @Test
        @DisplayName("[success] 여러 스케줄을 저장한다")
        void success_multipleSchedules() {
            // given
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startDt1 = LocalDateTime.of(2025, 1, 1, 10, 0);

            Schedule schedule1 = Schedule.builder()
                .writerEmail("user1@example.com")
                .content("회의 1")
                .startDt(startDt1)
                .regDt(now)
                .build();

            LocalDateTime startDt2 = LocalDateTime.of(2025, 1, 1, 14, 0);

            Schedule schedule2 = Schedule.builder()
                .writerEmail("user2@example.com")
                .content("회의 2")
                .startDt(startDt2)
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
            LocalDateTime notificationDt = LocalDateTime.of(2025, 1, 1, 9, 50);
            LocalDateTime modDt = LocalDateTime.now();

            Schedule schedule = Schedule.builder()
                .writerEmail("user@example.com")
                .content("전체 필드 회의")
                .startDt(startDt)
                .notificationDt(notificationDt)
                .groupId(1L)
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
            assertThat(savedEntity.getNotificationDt()).isEqualTo(notificationDt);
            assertThat(savedEntity.getGroupId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("[success] ID가 자동으로 생성된다")
        void success_autoGeneratedId() {
            // given
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startDt = LocalDateTime.of(2025, 1, 1, 10, 0);

            Schedule schedule = Schedule.builder()
                .writerEmail("user@example.com")
                .content("회의")
                .startDt(startDt)
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
            LocalDateTime notificationDt = LocalDateTime.of(2025, 1, 1, 9, 50);

            ScheduleEntity entity = ScheduleEntity.builder()
                .writerEmail("user@example.com")
                .content("개인 회의")
                .startDt(startDt)
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
            assertThat(result.getNotificationDt()).isEqualTo(notificationDt);
            assertThat(result.getNotificationYn()).isEqualTo("N");
        }

        @Test
        @DisplayName("[success] 그룹 스케줄을 조회한다")
        void success_groupSchedule() {
            // given
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startDt = LocalDateTime.of(2025, 1, 1, 14, 0);

            ScheduleEntity entity = ScheduleEntity.builder()
                .writerEmail("user@example.com")
                .groupId(1L)
                .content("그룹 회의")
                .startDt(startDt)
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
            assertThat(result.getGroupId()).isEqualTo(1L);
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

            ScheduleEntity entity = ScheduleEntity.builder()
                .writerEmail("user@example.com")
                .content("개인 회의")
                .startDt(startDt)
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

            ScheduleEntity entity1 = ScheduleEntity.builder()
                .writerEmail("user1@example.com")
                .content("회의 1")
                .startDt(startDt)
                .notificationYn("N")
                .regDt(now)
                .build();

            ScheduleEntity entity2 = ScheduleEntity.builder()
                .writerEmail("user2@example.com")
                .content("회의 2")
                .startDt(startDt)
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

    @Nested
    @DisplayName("[findSchedules] 조건에 맞는 스케줄을 조회하는 메소드")
    class Describe_findSchedules {

        @Test
        @DisplayName("[success] 월별 개인 일정을 조회한다 (groupId=0)")
        void success_findMonthlyPrivateSchedules() {
            // given
            LocalDateTime now = LocalDateTime.now();

            // 2025년 1월 개인 일정
            ScheduleEntity schedule1 = ScheduleEntity.builder()
                .writerEmail("user@example.com")
                .content("1월 1일 개인 회의")
                .startDt(LocalDateTime.of(2025, 1, 1, 10, 0))
                .notificationYn("N")
                .regDt(now)
                .build();

            ScheduleEntity schedule2 = ScheduleEntity.builder()
                .writerEmail("user@example.com")
                .content("1월 15일 개인 회의")
                .startDt(LocalDateTime.of(2025, 1, 15, 14, 0))
                .notificationYn("N")
                .regDt(now)
                .build();

            // 그룹 일정 (조회되지 않아야 함)
            ScheduleEntity groupSchedule = ScheduleEntity.builder()
                .writerEmail("owner@example.com")
                .groupId(1L)
                .content("그룹 회의")
                .startDt(LocalDateTime.of(2025, 1, 10, 10, 0))
                .notificationYn("N")
                .regDt(now)
                .build();

            // 2월 일정 (조회되지 않아야 함)
            ScheduleEntity februarySchedule = ScheduleEntity.builder()
                .writerEmail("user@example.com")
                .content("2월 1일 개인 회의")
                .startDt(LocalDateTime.of(2025, 2, 1, 10, 0))
                .notificationYn("N")
                .regDt(now)
                .build();

            entityManager.persist(schedule1);
            entityManager.persist(schedule2);
            entityManager.persist(groupSchedule);
            entityManager.persist(februarySchedule);
            entityManager.flush();
            entityManager.clear();

            Account account = Account.builder()
                .email("user@example.com")
                .groups(Arrays.asList())
                .build();

            FindSchedulesCommand command = FindSchedulesCommand.builder()
                .account(account)
                .baseDate(LocalDate.of(2025, 1, 15))
                .groupId(0L)
                .build();

            // when
            List<Schedule> result = adapter.findSchedules(command);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getContent()).isEqualTo("1월 1일 개인 회의");
            assertThat(result.get(1).getContent()).isEqualTo("1월 15일 개인 회의");
        }

        @Test
        @DisplayName("[success] 특정 그룹 일정만 조회한다 (filterType=그룹명)")
        void success_findSpecificGroupSchedules() {
            // given
            LocalDateTime now = LocalDateTime.now();

            ScheduleEntity groupSchedule1 = ScheduleEntity.builder()
                .writerEmail("owner@example.com")
                .groupId(1L)
                .content("그룹1 회의 1")
                .startDt(LocalDateTime.of(2025, 1, 5, 10, 0))
                .notificationYn("N")
                .regDt(now)
                .build();

            ScheduleEntity groupSchedule2 = ScheduleEntity.builder()
                .writerEmail("owner@example.com")
                .groupId(1L)
                .content("그룹1 회의 2")
                .startDt(LocalDateTime.of(2025, 1, 15, 14, 0))
                .notificationYn("N")
                .regDt(now)
                .build();

            // 다른 그룹 일정 (조회되지 않아야 함)
            ScheduleEntity otherGroupSchedule = ScheduleEntity.builder()
                .writerEmail("owner@example.com")
                .groupId(2L)
                .content("그룹2 회의")
                .startDt(LocalDateTime.of(2025, 1, 10, 10, 0))
                .notificationYn("N")
                .regDt(now)
                .build();

            entityManager.persist(groupSchedule1);
            entityManager.persist(groupSchedule2);
            entityManager.persist(otherGroupSchedule);
            entityManager.flush();
            entityManager.clear();

            Account account = Account.builder()
                .email("user@example.com")
                .groups(Arrays.asList())
                .build();

            FindSchedulesCommand command = FindSchedulesCommand.builder()
                .account(account)
                .baseDate(LocalDate.of(2025, 1, 15))
                .groupId(1L)
                .build();

            // when
            List<Schedule> result = adapter.findSchedules(command);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getGroupId()).isEqualTo(1L);
            assertThat(result.get(1).getGroupId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("[success] 전체 일정을 조회한다 (groupId=null)")
        void success_findAllSchedules() {
            // given
            LocalDateTime now = LocalDateTime.now();

            // 개인 일정
            ScheduleEntity personalSchedule = ScheduleEntity.builder()
                .writerEmail("user@example.com")
                .content("개인 회의")
                .startDt(LocalDateTime.of(2025, 1, 5, 10, 0))
                .notificationYn("N")
                .regDt(now)
                .build();

            // 속한 그룹의 일정
            ScheduleEntity groupSchedule = ScheduleEntity.builder()
                .writerEmail("owner@example.com")
                .groupId(1L)
                .content("그룹 회의")
                .startDt(LocalDateTime.of(2025, 1, 15, 14, 0))
                .notificationYn("N")
                .regDt(now)
                .build();

            // 속하지 않은 그룹의 일정 (조회되지 않아야 함)
            ScheduleEntity otherGroupSchedule = ScheduleEntity.builder()
                .writerEmail("owner@example.com")
                .groupId(2L)
                .content("다른 그룹 회의")
                .startDt(LocalDateTime.of(2025, 1, 10, 10, 0))
                .notificationYn("N")
                .regDt(now)
                .build();

            entityManager.persist(personalSchedule);
            entityManager.persist(groupSchedule);
            entityManager.persist(otherGroupSchedule);
            entityManager.flush();
            entityManager.clear();

            Account account = Account.builder()
                .email("user@example.com")
                .groups(Arrays.asList(Group.of(1L)))
                .build();

            FindSchedulesCommand command = FindSchedulesCommand.builder()
                .account(account)
                .baseDate(LocalDate.of(2025, 1, 15))
                .groupId(null)  // 전체 조회
                .build();

            // when
            List<Schedule> result = adapter.findSchedules(command);

            // then
            assertThat(result).hasSize(2);
        }


        @Test
        @DisplayName("[success] 조건에 맞는 일정이 없으면 빈 목록을 반환한다")
        void success_returnEmptyList() {
            // given
            Account account = Account.builder()
                .email("user@example.com")
                .groups(Arrays.asList())
                .build();

            FindSchedulesCommand command = FindSchedulesCommand.builder()
                .account(account)
                .baseDate(LocalDate.of(2025, 1, 15))
                .groupId(0L)
                .build();

            // when
            List<Schedule> result = adapter.findSchedules(command);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("[success] 시작일 순으로 정렬하여 반환한다")
        void success_orderedByStartDate() {
            // given
            LocalDateTime now = LocalDateTime.now();

            ScheduleEntity schedule3 = ScheduleEntity.builder()
                .writerEmail("user@example.com")
                .content("1월 20일")
                .startDt(LocalDateTime.of(2025, 1, 20, 10, 0))
                .notificationYn("N")
                .regDt(now)
                .build();

            ScheduleEntity schedule1 = ScheduleEntity.builder()
                .writerEmail("user@example.com")
                .content("1월 5일")
                .startDt(LocalDateTime.of(2025, 1, 5, 10, 0))
                .notificationYn("N")
                .regDt(now)
                .build();

            ScheduleEntity schedule2 = ScheduleEntity.builder()
                .writerEmail("user@example.com")
                .content("1월 15일")
                .startDt(LocalDateTime.of(2025, 1, 15, 14, 0))
                .notificationYn("N")
                .regDt(now)
                .build();

            // 순서대로 저장하지 않음
            entityManager.persist(schedule3);
            entityManager.persist(schedule1);
            entityManager.persist(schedule2);
            entityManager.flush();
            entityManager.clear();

            Account account = Account.builder()
                .email("user@example.com")
                .groups(Arrays.asList())
                .build();

            FindSchedulesCommand command = FindSchedulesCommand.builder()
                .account(account)
                .baseDate(LocalDate.of(2025, 1, 15))
                .groupId(0L)
                .build();

            // when
            List<Schedule> result = adapter.findSchedules(command);

            // then
            assertThat(result).hasSize(3);
            assertThat(result.get(0).getContent()).isEqualTo("1월 5일");
            assertThat(result.get(1).getContent()).isEqualTo("1월 15일");
            assertThat(result.get(2).getContent()).isEqualTo("1월 20일");
        }
    }
}
