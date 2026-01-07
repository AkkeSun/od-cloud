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
                .groupId(1L)
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
            assertThat(entity.getGroupId()).isEqualTo(1L);
            assertThat(entity.getTitle()).isEqualTo("테스트 공지사항");
            assertThat(entity.getContent()).isEqualTo("공지사항 내용입니다.");
            assertThat(entity.getWriterEmail()).isEqualTo("writer@example.com");
        }

        @Test
        @DisplayName("[success] 모든 필드가 올바르게 저장된다")
        void success_allFields() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Notice notice = Notice.builder()
                .groupId(1L)
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
            assertThat(entity.getGroupId()).isEqualTo(1L);
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
            Long groupId = 1L;
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
            Long groupId = 1L;
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
            Long groupId = 1L;
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
            Long targetGroupId = 1L;
            Long otherGroupId = 2L;
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
            Long groupId = 1L;

            // when
            List<Notice> result = adapter.findByGroupId(groupId, 10);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("[success] limit이 1인 경우 1개만 반환한다")
        void success_limitOne() {
            // given
            Long groupId = 1L;
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
            Long groupId = 1L;
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

    @Nested
    @DisplayName("[findById] ID로 공지사항을 조회하는 메소드")
    class Describe_findById {

        @Test
        @DisplayName("[success] ID로 공지사항을 조회한다")
        void success() {
            // given
            LocalDateTime now = LocalDateTime.now();
            NoticeEntity entity = NoticeEntity.builder()
                .groupId(1L)
                .title("테스트 공지")
                .content("테스트 내용")
                .writerEmail("test@example.com")
                .regDt(now)
                .build();
            entityManager.persist(entity);
            entityManager.flush();
            entityManager.clear();

            // when
            Notice result = adapter.findById(entity.getId());

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(entity.getId());
            assertThat(result.getGroupId()).isEqualTo(1L);
            assertThat(result.getTitle()).isEqualTo("테스트 공지");
            assertThat(result.getContent()).isEqualTo("테스트 내용");
            assertThat(result.getWriterEmail()).isEqualTo("test@example.com");
            assertThat(result.getRegDt()).isEqualTo(now);
        }

        @Test
        @DisplayName("[error] 존재하지 않는 ID로 조회 시 예외를 발생시킨다")
        void error_notFound() {
            // when & then
            org.junit.jupiter.api.Assertions.assertThrows(
                com.odcloud.infrastructure.exception.CustomBusinessException.class,
                () -> adapter.findById(999L)
            );
        }
    }

    @Nested
    @DisplayName("[delete] 공지사항을 삭제하는 메소드")
    class Describe_delete {

        @Test
        @DisplayName("[success] 공지사항을 삭제한다")
        void success() {
            // given
            NoticeEntity entity = NoticeEntity.builder()
                .groupId(1L)
                .title("삭제될 공지")
                .content("내용")
                .writerEmail("test@example.com")
                .regDt(LocalDateTime.now())
                .build();
            entityManager.persist(entity);
            entityManager.flush();
            entityManager.clear();

            Long noticeId = entity.getId();
            Notice notice = Notice.builder()
                .id(noticeId)
                .groupId(1L)
                .build();

            // when
            adapter.delete(notice);
            entityManager.flush();
            entityManager.clear();

            // then
            NoticeEntity deletedEntity = entityManager.find(NoticeEntity.class, noticeId);
            assertThat(deletedEntity).isNull();
        }

        @Test
        @DisplayName("[success] 존재하지 않는 ID로 삭제 시도해도 에러가 발생하지 않는다")
        void success_notFound() {
            // given
            Notice notice = Notice.builder()
                .id(999L)
                .groupId(1L)
                .build();

            // when & then
            org.junit.jupiter.api.Assertions.assertDoesNotThrow(
                () -> adapter.delete(notice)
            );
        }

        @Test
        @DisplayName("[success] 삭제 후 다른 공지사항은 남아있다")
        void success_otherNoticesRemain() {
            // given
            NoticeEntity entity1 = NoticeEntity.builder()
                .groupId(1L)
                .title("삭제될 공지")
                .content("내용 1")
                .writerEmail("test@example.com")
                .regDt(LocalDateTime.now())
                .build();

            NoticeEntity entity2 = NoticeEntity.builder()
                .groupId(1L)
                .title("남을 공지")
                .content("내용 2")
                .writerEmail("test@example.com")
                .regDt(LocalDateTime.now())
                .build();

            entityManager.persist(entity1);
            entityManager.persist(entity2);
            entityManager.flush();
            entityManager.clear();

            Notice notice = Notice.builder()
                .id(entity1.getId())
                .groupId(1L)
                .build();

            // when
            adapter.delete(notice);
            entityManager.flush();
            entityManager.clear();

            // then
            NoticeEntity deletedEntity = entityManager.find(NoticeEntity.class, entity1.getId());
            NoticeEntity remainingEntity = entityManager.find(NoticeEntity.class,
                entity2.getId());

            assertThat(deletedEntity).isNull();
            assertThat(remainingEntity).isNotNull();
            assertThat(remainingEntity.getTitle()).isEqualTo("남을 공지");
        }
    }

    @Nested
    @DisplayName("[update] 공지사항을 수정하는 메소드")
    class Describe_update {

        @Test
        @DisplayName("[success] 공지사항을 수정한다")
        void success() {
            // given
            NoticeEntity entity = NoticeEntity.builder()
                .groupId(1L)
                .title("원본 제목")
                .content("원본 내용")
                .writerEmail("test@example.com")
                .regDt(LocalDateTime.now())
                .build();
            entityManager.persist(entity);
            entityManager.flush();
            entityManager.clear();

            Notice notice = Notice.builder()
                .id(entity.getId())
                .groupId(1L)
                .title("수정된 제목")
                .content("수정된 내용")
                .writerEmail("test@example.com")
                .regDt(entity.getRegDt())
                .build();

            // when
            adapter.update(notice);
            entityManager.flush();
            entityManager.clear();

            // then
            NoticeEntity updatedEntity = entityManager.find(NoticeEntity.class, entity.getId());
            assertThat(updatedEntity).isNotNull();
            assertThat(updatedEntity.getTitle()).isEqualTo("수정된 제목");
            assertThat(updatedEntity.getContent()).isEqualTo("수정된 내용");
            assertThat(updatedEntity.getGroupId()).isEqualTo(1L);
            assertThat(updatedEntity.getWriterEmail()).isEqualTo("test@example.com");
        }

        @Test
        @DisplayName("[success] 제목만 수정된다")
        void success_titleOnly() {
            // given
            NoticeEntity entity = NoticeEntity.builder()
                .groupId(1L)
                .title("원본 제목")
                .content("원본 내용")
                .writerEmail("test@example.com")
                .regDt(LocalDateTime.now())
                .build();
            entityManager.persist(entity);
            entityManager.flush();
            entityManager.clear();

            Notice notice = Notice.builder()
                .id(entity.getId())
                .groupId(1L)
                .title("수정된 제목")
                .content("원본 내용")
                .writerEmail("test@example.com")
                .regDt(entity.getRegDt())
                .build();

            // when
            adapter.update(notice);
            entityManager.flush();
            entityManager.clear();

            // then
            NoticeEntity updatedEntity = entityManager.find(NoticeEntity.class, entity.getId());
            assertThat(updatedEntity).isNotNull();
            assertThat(updatedEntity.getTitle()).isEqualTo("수정된 제목");
            assertThat(updatedEntity.getContent()).isEqualTo("원본 내용");
        }

        @Test
        @DisplayName("[success] 내용만 수정된다")
        void success_contentOnly() {
            // given
            NoticeEntity entity = NoticeEntity.builder()
                .groupId(1L)
                .title("원본 제목")
                .content("원본 내용")
                .writerEmail("test@example.com")
                .regDt(LocalDateTime.now())
                .build();
            entityManager.persist(entity);
            entityManager.flush();
            entityManager.clear();

            Notice notice = Notice.builder()
                .id(entity.getId())
                .groupId(1L)
                .title("원본 제목")
                .content("수정된 내용")
                .writerEmail("test@example.com")
                .regDt(entity.getRegDt())
                .build();

            // when
            adapter.update(notice);
            entityManager.flush();
            entityManager.clear();

            // then
            NoticeEntity updatedEntity = entityManager.find(NoticeEntity.class, entity.getId());
            assertThat(updatedEntity).isNotNull();
            assertThat(updatedEntity.getTitle()).isEqualTo("원본 제목");
            assertThat(updatedEntity.getContent()).isEqualTo("수정된 내용");
        }

        @Test
        @DisplayName("[success] 수정 후 다른 공지사항은 영향받지 않는다")
        void success_otherNoticesUnaffected() {
            // given
            NoticeEntity entity1 = NoticeEntity.builder()
                .groupId(1L)
                .title("수정될 공지")
                .content("내용 1")
                .writerEmail("test@example.com")
                .regDt(LocalDateTime.now())
                .build();

            NoticeEntity entity2 = NoticeEntity.builder()
                .groupId(1L)
                .title("원본 공지")
                .content("내용 2")
                .writerEmail("test@example.com")
                .regDt(LocalDateTime.now())
                .build();

            entityManager.persist(entity1);
            entityManager.persist(entity2);
            entityManager.flush();
            entityManager.clear();

            Notice notice = Notice.builder()
                .id(entity1.getId())
                .groupId(1L)
                .title("수정된 제목")
                .content("수정된 내용")
                .writerEmail("test@example.com")
                .regDt(entity1.getRegDt())
                .build();

            // when
            adapter.update(notice);
            entityManager.flush();
            entityManager.clear();

            // then
            NoticeEntity updatedEntity = entityManager.find(NoticeEntity.class, entity1.getId());
            NoticeEntity unchangedEntity = entityManager.find(NoticeEntity.class, entity2.getId());

            assertThat(updatedEntity.getTitle()).isEqualTo("수정된 제목");
            assertThat(unchangedEntity.getTitle()).isEqualTo("원본 공지");
            assertThat(unchangedEntity.getContent()).isEqualTo("내용 2");
        }
    }
}
