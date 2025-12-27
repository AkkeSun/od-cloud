package com.odcloud.adapter.out.persistence.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.IntegrationTestSupport;
import com.odcloud.domain.model.Notice;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class NoticeStorageAdapterTest extends IntegrationTestSupport {

    @Autowired
    NoticeStorageAdapter adapter;

    @Autowired
    EntityManager entityManager;

    @AfterEach
    void tearDown() {
        entityManager.createQuery("DELETE FROM NoticeEntity").executeUpdate();
        entityManager.flush();
        entityManager.clear();
    }

    @Nested
    @DisplayName("[save] 공지사항을 저장하는 메소드")
    class Describe_save {

        @Test
        @DisplayName("[success] 신규 공지사항을 저장한다")
        void success() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Notice notice = Notice.builder()
                .groupId("test-group")
                .title("테스트 공지사항")
                .content("공지사항 내용입니다.")
                .writerEmail("writer@example.com")
                .regDt(now)
                .build();

            // when
            Notice savedNotice = adapter.save(notice);
            entityManager.flush();
            entityManager.clear();

            // then
            assertThat(savedNotice).isNotNull();
            assertThat(savedNotice.getId()).isNotNull();

            NoticeEntity entity = entityManager.find(NoticeEntity.class, savedNotice.getId());
            assertThat(entity).isNotNull();
            assertThat(entity.getGroupId()).isEqualTo("test-group");
            assertThat(entity.getTitle()).isEqualTo("테스트 공지사항");
            assertThat(entity.getContent()).isEqualTo("공지사항 내용입니다.");
            assertThat(entity.getWriterEmail()).isEqualTo("writer@example.com");
        }

        @Test
        @DisplayName("[success] 기존 공지사항을 업데이트한다")
        void success_update() {
            // given
            LocalDateTime now = LocalDateTime.now();
            NoticeEntity existingEntity = NoticeEntity.builder()
                .groupId("test-group")
                .title("기존 제목")
                .content("기존 내용")
                .writerEmail("writer@example.com")
                .regDt(now)
                .build();
            entityManager.persist(existingEntity);
            entityManager.flush();
            entityManager.clear();

            Notice updatedNotice = Notice.builder()
                .id(existingEntity.getId())
                .groupId("test-group")
                .title("수정된 제목")
                .content("수정된 내용")
                .writerEmail("writer@example.com")
                .regDt(now)
                .build();

            // when
            Notice result = adapter.save(updatedNotice);
            entityManager.flush();
            entityManager.clear();

            // then
            NoticeEntity entity = entityManager.find(NoticeEntity.class, existingEntity.getId());
            assertThat(entity).isNotNull();
            assertThat(entity.getTitle()).isEqualTo("수정된 제목");
            assertThat(entity.getContent()).isEqualTo("수정된 내용");
        }

        @Test
        @DisplayName("[success] 모든 필드가 올바르게 저장된다")
        void success_allFields() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Notice notice = Notice.builder()
                .groupId("group-123")
                .title("완전한 공지사항")
                .content("모든 필드가 포함된 내용")
                .writerEmail("admin@example.com")
                .regDt(now)
                .build();

            // when
            Notice savedNotice = adapter.save(notice);
            entityManager.flush();
            entityManager.clear();

            // then
            NoticeEntity entity = entityManager.find(NoticeEntity.class, savedNotice.getId());
            assertThat(entity.getGroupId()).isEqualTo("group-123");
            assertThat(entity.getTitle()).isEqualTo("완전한 공지사항");
            assertThat(entity.getContent()).isEqualTo("모든 필드가 포함된 내용");
            assertThat(entity.getWriterEmail()).isEqualTo("admin@example.com");
            assertThat(entity.getRegDt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("[findByGroupId] 그룹 ID로 공지사항 목록을 조회하는 메소드")
    class Describe_findByGroupId {

        @Test
        @DisplayName("[success] 그룹 ID로 공지사항을 조회한다")
        void success() {
            // given
            String groupId = "test-group";
            LocalDateTime now = LocalDateTime.now();

            NoticeEntity notice1 = NoticeEntity.builder()
                .groupId(groupId)
                .title("공지사항 1")
                .content("내용 1")
                .writerEmail("writer@example.com")
                .regDt(now.minusDays(2))
                .build();

            NoticeEntity notice2 = NoticeEntity.builder()
                .groupId(groupId)
                .title("공지사항 2")
                .content("내용 2")
                .writerEmail("writer@example.com")
                .regDt(now.minusDays(1))
                .build();

            entityManager.persist(notice1);
            entityManager.persist(notice2);
            entityManager.flush();
            entityManager.clear();

            // when
            List<Notice> result = adapter.findByGroupId(groupId, 10);

            // then
            assertThat(result).hasSize(2);
            assertThat(result)
                .extracting(Notice::getTitle)
                .containsExactly("공지사항 2", "공지사항 1");
        }

        @Test
        @DisplayName("[success] 최신 등록일 순으로 정렬된다")
        void success_orderedByRegDtDesc() {
            // given
            String groupId = "test-group";
            LocalDateTime now = LocalDateTime.now();

            for (int i = 1; i <= 5; i++) {
                NoticeEntity notice = NoticeEntity.builder()
                    .groupId(groupId)
                    .title("공지사항 " + i)
                    .content("내용 " + i)
                    .writerEmail("writer@example.com")
                    .regDt(now.minusDays(5 - i))
                    .build();
                entityManager.persist(notice);
            }
            entityManager.flush();
            entityManager.clear();

            // when
            List<Notice> result = adapter.findByGroupId(groupId, 10);

            // then
            assertThat(result).hasSize(5);
            assertThat(result.get(0).getTitle()).isEqualTo("공지사항 5");
            assertThat(result.get(1).getTitle()).isEqualTo("공지사항 4");
            assertThat(result.get(2).getTitle()).isEqualTo("공지사항 3");
            assertThat(result.get(3).getTitle()).isEqualTo("공지사항 2");
            assertThat(result.get(4).getTitle()).isEqualTo("공지사항 1");
        }

        @Test
        @DisplayName("[success] limit 파라미터가 적용된다")
        void success_limit() {
            // given
            String groupId = "test-group";
            LocalDateTime now = LocalDateTime.now();

            for (int i = 1; i <= 10; i++) {
                NoticeEntity notice = NoticeEntity.builder()
                    .groupId(groupId)
                    .title("공지사항 " + i)
                    .content("내용 " + i)
                    .writerEmail("writer@example.com")
                    .regDt(now.minusDays(10 - i))
                    .build();
                entityManager.persist(notice);
            }
            entityManager.flush();
            entityManager.clear();

            // when
            List<Notice> result = adapter.findByGroupId(groupId, 5);

            // then
            assertThat(result).hasSize(5);
            assertThat(result.get(0).getTitle()).isEqualTo("공지사항 10");
            assertThat(result.get(4).getTitle()).isEqualTo("공지사항 6");
        }

        @Test
        @DisplayName("[success] 다른 그룹의 공지사항은 조회되지 않는다")
        void success_filterByGroupId() {
            // given
            String targetGroupId = "target-group";
            String otherGroupId = "other-group";
            LocalDateTime now = LocalDateTime.now();

            NoticeEntity targetNotice = NoticeEntity.builder()
                .groupId(targetGroupId)
                .title("대상 그룹 공지")
                .content("대상 내용")
                .writerEmail("writer@example.com")
                .regDt(now)
                .build();

            NoticeEntity otherNotice = NoticeEntity.builder()
                .groupId(otherGroupId)
                .title("다른 그룹 공지")
                .content("다른 내용")
                .writerEmail("writer@example.com")
                .regDt(now)
                .build();

            entityManager.persist(targetNotice);
            entityManager.persist(otherNotice);
            entityManager.flush();
            entityManager.clear();

            // when
            List<Notice> result = adapter.findByGroupId(targetGroupId, 10);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getTitle()).isEqualTo("대상 그룹 공지");
        }

        @Test
        @DisplayName("[success] 공지사항이 없는 경우 빈 리스트를 반환한다")
        void success_emptyList() {
            // given
            String groupId = "empty-group";

            // when
            List<Notice> result = adapter.findByGroupId(groupId, 10);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("[success] limit이 1인 경우 1개만 반환한다")
        void success_limitOne() {
            // given
            String groupId = "test-group";
            LocalDateTime now = LocalDateTime.now();

            NoticeEntity notice1 = NoticeEntity.builder()
                .groupId(groupId)
                .title("공지사항 1")
                .content("내용 1")
                .writerEmail("writer@example.com")
                .regDt(now.minusDays(1))
                .build();

            NoticeEntity notice2 = NoticeEntity.builder()
                .groupId(groupId)
                .title("공지사항 2")
                .content("내용 2")
                .writerEmail("writer@example.com")
                .regDt(now)
                .build();

            entityManager.persist(notice1);
            entityManager.persist(notice2);
            entityManager.flush();
            entityManager.clear();

            // when
            List<Notice> result = adapter.findByGroupId(groupId, 1);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getTitle()).isEqualTo("공지사항 2");
        }

        @Test
        @DisplayName("[success] Domain 객체로 올바르게 변환된다")
        void success_toDomain() {
            // given
            String groupId = "test-group";
            LocalDateTime now = LocalDateTime.now();

            NoticeEntity entity = NoticeEntity.builder()
                .groupId(groupId)
                .title("테스트 공지")
                .content("테스트 내용")
                .writerEmail("test@example.com")
                .regDt(now)
                .build();
            entityManager.persist(entity);
            entityManager.flush();
            entityManager.clear();

            // when
            List<Notice> result = adapter.findByGroupId(groupId, 10);

            // then
            assertThat(result).hasSize(1);
            Notice notice = result.get(0);
            assertThat(notice.getId()).isNotNull();
            assertThat(notice.getGroupId()).isEqualTo(groupId);
            assertThat(notice.getTitle()).isEqualTo("테스트 공지");
            assertThat(notice.getContent()).isEqualTo("테스트 내용");
            assertThat(notice.getWriterEmail()).isEqualTo("test@example.com");
            assertThat(notice.getRegDt()).isEqualTo(now);
        }
    }
}
