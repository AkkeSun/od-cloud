package com.odcloud.adapter.out.persistence.jpa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.odcloud.IntegrationTestSupport;
import com.odcloud.domain.model.Question;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class QuestionStorageAdapterTest extends IntegrationTestSupport {

    @Autowired
    QuestionStorageAdapter adapter;

    @Autowired
    EntityManager entityManager;

    @AfterEach
    void tearDown() {
        entityManager.createQuery("DELETE FROM AnswerEntity").executeUpdate();
        entityManager.createQuery("DELETE FROM QuestionEntity").executeUpdate();
        entityManager.flush();
        entityManager.clear();
    }

    @Nested
    @DisplayName("[save] 질문을 저장하는 메서드")
    class Describe_save {

        @Test
        @DisplayName("[success] 신규 질문을 저장한다")
        void success() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Question question = Question.builder()
                .writerEmail("test@example.com")
                .writerNickname("tester")
                .title("테스트 제목")
                .content("테스트 내용")
                .answered(false)
                .regDt(now)
                .build();

            // when
            adapter.save(question);
            entityManager.flush();
            entityManager.clear();

            // then
            QuestionEntity savedEntity = entityManager.createQuery(
                    "SELECT q FROM QuestionEntity q WHERE q.writerEmail = :email",
                    QuestionEntity.class)
                .setParameter("email", "test@example.com")
                .getSingleResult();

            assertThat(savedEntity).isNotNull();
            assertThat(savedEntity.getId()).isNotNull();
            assertThat(savedEntity.getWriterEmail()).isEqualTo("test@example.com");
            assertThat(savedEntity.getWriterNickname()).isEqualTo("tester");
            assertThat(savedEntity.getTitle()).isEqualTo("테스트 제목");
            assertThat(savedEntity.getContent()).isEqualTo("테스트 내용");
            assertThat(savedEntity.getAnswered()).isFalse();
            assertThat(savedEntity.getRegDt()).isNotNull();
        }

        @Test
        @DisplayName("[success] 기존 질문을 수정한다")
        void success_update() {
            // given
            LocalDateTime now = LocalDateTime.now();
            QuestionEntity entity = QuestionEntity.builder()
                .writerEmail("test@example.com")
                .writerNickname("tester")
                .title("원래 제목")
                .content("원래 내용")
                .answered(false)
                .regDt(now.minusDays(1))
                .build();

            entityManager.persist(entity);
            entityManager.flush();
            entityManager.clear();

            Question updatedQuestion = Question.builder()
                .id(entity.getId())
                .writerEmail("test@example.com")
                .writerNickname("tester")
                .title("수정된 제목")
                .content("수정된 내용")
                .answered(true)
                .modDt(now)
                .regDt(now.minusDays(1))
                .build();

            // when
            adapter.save(updatedQuestion);
            entityManager.flush();
            entityManager.clear();

            // then
            QuestionEntity savedEntity = entityManager.find(QuestionEntity.class,
                entity.getId());
            assertThat(savedEntity.getTitle()).isEqualTo("수정된 제목");
            assertThat(savedEntity.getContent()).isEqualTo("수정된 내용");
            assertThat(savedEntity.getAnswered()).isTrue();
            assertThat(savedEntity.getModDt()).isNotNull();
        }

        @Test
        @DisplayName("[success] 여러 질문을 저장한다")
        void success_multipleQuestions() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Question question1 = Question.builder()
                .writerEmail("test1@example.com")
                .writerNickname("tester1")
                .title("질문 1")
                .content("내용 1")
                .answered(false)
                .regDt(now)
                .build();

            Question question2 = Question.builder()
                .writerEmail("test2@example.com")
                .writerNickname("tester2")
                .title("질문 2")
                .content("내용 2")
                .answered(false)
                .regDt(now)
                .build();

            // when
            adapter.save(question1);
            adapter.save(question2);
            entityManager.flush();
            entityManager.clear();

            // then
            Long count = entityManager.createQuery(
                    "SELECT COUNT(q) FROM QuestionEntity q", Long.class)
                .getSingleResult();
            assertThat(count).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("[findById] ID로 질문을 조회하는 메서드")
    class Describe_findById {

        @Test
        @DisplayName("[success] ID로 질문을 조회한다")
        void success() {
            // given
            LocalDateTime now = LocalDateTime.now();
            QuestionEntity entity = QuestionEntity.builder()
                .writerEmail("test@example.com")
                .writerNickname("tester")
                .title("테스트 제목")
                .content("테스트 내용")
                .answered(false)
                .regDt(now)
                .build();

            entityManager.persist(entity);
            entityManager.flush();
            entityManager.clear();

            // when
            Question question = adapter.findById(entity.getId());

            // then
            assertThat(question).isNotNull();
            assertThat(question.getId()).isEqualTo(entity.getId());
            assertThat(question.getWriterEmail()).isEqualTo("test@example.com");
            assertThat(question.getWriterNickname()).isEqualTo("tester");
            assertThat(question.getTitle()).isEqualTo("테스트 제목");
            assertThat(question.getContent()).isEqualTo("테스트 내용");
            assertThat(question.getAnswered()).isFalse();
        }

        @Test
        @DisplayName("[error] 존재하지 않는 ID로 조회하면 예외가 발생한다")
        void error_notFound() {
            // when & then
            assertThatThrownBy(() -> adapter.findById(999L))
                .isInstanceOf(CustomBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode",
                    ErrorCode.Business_NOT_FOUND_QUESTION);
        }

        @Test
        @DisplayName("[success] 답변이 달린 질문을 조회한다")
        void success_answeredQuestion() {
            // given
            LocalDateTime now = LocalDateTime.now();
            QuestionEntity entity = QuestionEntity.builder()
                .writerEmail("test@example.com")
                .writerNickname("tester")
                .title("테스트 제목")
                .content("테스트 내용")
                .answered(true)
                .modDt(now.minusDays(1))
                .regDt(now.minusDays(2))
                .build();

            entityManager.persist(entity);
            entityManager.flush();
            entityManager.clear();

            // when
            Question question = adapter.findById(entity.getId());

            // then
            assertThat(question.getAnswered()).isTrue();
            assertThat(question.getModDt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("[findAll] 질문 목록을 조회하는 메서드")
    class Describe_findAll {

        @Test
        @DisplayName("[success] 첫 번째 페이지를 조회한다")
        void success_firstPage() {
            // given
            LocalDateTime now = LocalDateTime.now();
            for (int i = 1; i <= 15; i++) {
                QuestionEntity entity = QuestionEntity.builder()
                    .writerEmail("test" + i + "@example.com")
                    .writerNickname("tester" + i)
                    .title("질문 " + i)
                    .content("내용 " + i)
                    .answered(false)
                    .regDt(now.minusDays(15 - i))
                    .build();
                entityManager.persist(entity);
            }
            entityManager.flush();
            entityManager.clear();

            // when
            Page<Question> result = adapter.findAll(0, 10);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getNumber()).isEqualTo(0);
            assertThat(result.getSize()).isEqualTo(10);
            assertThat(result.getTotalElements()).isEqualTo(15);
            assertThat(result.getTotalPages()).isEqualTo(2);
            assertThat(result.getContent()).hasSize(10);
        }

        @Test
        @DisplayName("[success] 질문이 최신순으로 정렬되어 조회된다")
        void success_orderedByRegDtDesc() {
            // given
            LocalDateTime now = LocalDateTime.now();

            QuestionEntity oldEntity = QuestionEntity.builder()
                .writerEmail("old@example.com")
                .writerNickname("old")
                .title("오래된 질문")
                .content("오래된 내용")
                .answered(false)
                .regDt(now.minusDays(2))
                .build();

            QuestionEntity newEntity = QuestionEntity.builder()
                .writerEmail("new@example.com")
                .writerNickname("new")
                .title("최신 질문")
                .content("최신 내용")
                .answered(false)
                .regDt(now)
                .build();

            entityManager.persist(oldEntity);
            entityManager.persist(newEntity);
            entityManager.flush();
            entityManager.clear();

            // when
            Page<Question> result = adapter.findAll(0, 10);

            // then
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent().get(0).getTitle()).isEqualTo("최신 질문");
            assertThat(result.getContent().get(1).getTitle()).isEqualTo("오래된 질문");
        }

        @Test
        @DisplayName("[success] 두 번째 페이지를 조회한다")
        void success_secondPage() {
            // given
            LocalDateTime now = LocalDateTime.now();
            for (int i = 1; i <= 15; i++) {
                QuestionEntity entity = QuestionEntity.builder()
                    .writerEmail("test" + i + "@example.com")
                    .writerNickname("tester" + i)
                    .title("질문 " + i)
                    .content("내용 " + i)
                    .answered(false)
                    .regDt(now.minusDays(15 - i))
                    .build();
                entityManager.persist(entity);
            }
            entityManager.flush();
            entityManager.clear();

            // when
            Page<Question> result = adapter.findAll(1, 10);

            // then
            assertThat(result.getNumber()).isEqualTo(1);
            assertThat(result.getSize()).isEqualTo(10);
            assertThat(result.getTotalElements()).isEqualTo(15);
            assertThat(result.getContent()).hasSize(5);
        }

        @Test
        @DisplayName("[success] 빈 결과를 조회한다")
        void success_emptyResult() {
            // when
            Page<Question> result = adapter.findAll(0, 10);

            // then
            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isEqualTo(0);
        }

        @Test
        @DisplayName("[success] 다양한 페이지 사이즈로 조회한다")
        void success_differentPageSize() {
            // given
            LocalDateTime now = LocalDateTime.now();
            for (int i = 1; i <= 20; i++) {
                QuestionEntity entity = QuestionEntity.builder()
                    .writerEmail("test" + i + "@example.com")
                    .writerNickname("tester" + i)
                    .title("질문 " + i)
                    .content("내용 " + i)
                    .answered(false)
                    .regDt(now.minusDays(20 - i))
                    .build();
                entityManager.persist(entity);
            }
            entityManager.flush();
            entityManager.clear();

            // when
            Page<Question> result = adapter.findAll(0, 5);

            // then
            assertThat(result.getSize()).isEqualTo(5);
            assertThat(result.getTotalElements()).isEqualTo(20);
            assertThat(result.getTotalPages()).isEqualTo(4);
            assertThat(result.getContent()).hasSize(5);
        }
    }
}
