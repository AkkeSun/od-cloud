package com.odcloud.application.question.service.find_questions;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.application.question.port.in.command.FindQuestionsCommand;
import com.odcloud.domain.model.Question;
import com.odcloud.fakeClass.FakeQuestionStoragePort;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FindQuestionsServiceTest {

    private FakeQuestionStoragePort fakeQuestionStoragePort;
    private FindQuestionsService findQuestionsService;

    @BeforeEach
    void setUp() {
        fakeQuestionStoragePort = new FakeQuestionStoragePort();
        findQuestionsService = new FindQuestionsService(fakeQuestionStoragePort);
    }

    @Nested
    @DisplayName("[findQuestions] 질문 목록을 조회하는 메서드")
    class Describe_findQuestions {

        @Test
        @DisplayName("[success] 첫 번째 페이지를 조회한다")
        void success_firstPage() {
            // given
            for (int i = 1; i <= 15; i++) {
                Question question = Question.builder()
                    .id((long) i)
                    .writerEmail("test" + i + "@example.com")
                    .writerNickname("tester" + i)
                    .title("질문 " + i)
                    .content("내용 " + i)
                    .answered(false)
                    .regDt(LocalDateTime.now().minusDays(15 - i))
                    .build();
                fakeQuestionStoragePort.database.add(question);
            }

            FindQuestionsCommand command = FindQuestionsCommand.builder()
                .page(0)
                .size(10)
                .build();

            // when
            FindQuestionsServiceResponse response = findQuestionsService.findQuestions(command);

            // then
            assertThat(response).isNotNull();
            assertThat(response.pageNumber()).isEqualTo(0);
            assertThat(response.pageSize()).isEqualTo(10);
            assertThat(response.totalElements()).isEqualTo(15);
            assertThat(response.totalPages()).isEqualTo(2);
            assertThat(response.questions()).hasSize(10);
        }

        @Test
        @DisplayName("[success] 두 번째 페이지를 조회한다")
        void success_secondPage() {
            // given
            for (int i = 1; i <= 15; i++) {
                Question question = Question.builder()
                    .id((long) i)
                    .writerEmail("test" + i + "@example.com")
                    .writerNickname("tester" + i)
                    .title("질문 " + i)
                    .content("내용 " + i)
                    .answered(false)
                    .regDt(LocalDateTime.now().minusDays(15 - i))
                    .build();
                fakeQuestionStoragePort.database.add(question);
            }

            FindQuestionsCommand command = FindQuestionsCommand.builder()
                .page(1)
                .size(10)
                .build();

            // when
            FindQuestionsServiceResponse response = findQuestionsService.findQuestions(command);

            // then
            assertThat(response.pageNumber()).isEqualTo(1);
            assertThat(response.pageSize()).isEqualTo(10);
            assertThat(response.totalElements()).isEqualTo(15);
            assertThat(response.questions()).hasSize(5);
        }

        @Test
        @DisplayName("[success] 빈 결과를 조회한다")
        void success_emptyResult() {
            // given
            FindQuestionsCommand command = FindQuestionsCommand.builder()
                .page(0)
                .size(10)
                .build();

            // when
            FindQuestionsServiceResponse response = findQuestionsService.findQuestions(command);

            // then
            assertThat(response.pageNumber()).isEqualTo(0);
            assertThat(response.pageSize()).isEqualTo(10);
            assertThat(response.totalElements()).isEqualTo(0);
            assertThat(response.totalPages()).isEqualTo(0);
            assertThat(response.questions()).isEmpty();
        }

        @Test
        @DisplayName("[success] 질문이 최신순으로 정렬되어 조회된다")
        void success_orderedByRegDtDesc() {
            // given
            LocalDateTime now = LocalDateTime.now();

            Question oldQuestion = Question.builder()
                .id(1L)
                .writerEmail("old@example.com")
                .writerNickname("old")
                .title("오래된 질문")
                .content("오래된 내용")
                .answered(false)
                .regDt(now.minusDays(2))
                .build();

            Question newQuestion = Question.builder()
                .id(2L)
                .writerEmail("new@example.com")
                .writerNickname("new")
                .title("최신 질문")
                .content("최신 내용")
                .answered(false)
                .regDt(now)
                .build();

            fakeQuestionStoragePort.database.add(oldQuestion);
            fakeQuestionStoragePort.database.add(newQuestion);

            FindQuestionsCommand command = FindQuestionsCommand.builder()
                .page(0)
                .size(10)
                .build();

            // when
            FindQuestionsServiceResponse response = findQuestionsService.findQuestions(command);

            // then
            assertThat(response.questions()).hasSize(2);
            assertThat(response.questions().get(0).getId()).isEqualTo(2L);
            assertThat(response.questions().get(0).getTitle()).isEqualTo("최신 질문");
            assertThat(response.questions().get(1).getId()).isEqualTo(1L);
            assertThat(response.questions().get(1).getTitle()).isEqualTo("오래된 질문");
        }

        @Test
        @DisplayName("[success] 다양한 페이지 사이즈로 조회한다")
        void success_differentPageSize() {
            // given
            for (int i = 1; i <= 20; i++) {
                Question question = Question.builder()
                    .id((long) i)
                    .writerEmail("test" + i + "@example.com")
                    .writerNickname("tester" + i)
                    .title("질문 " + i)
                    .content("내용 " + i)
                    .answered(false)
                    .regDt(LocalDateTime.now().minusDays(20 - i))
                    .build();
                fakeQuestionStoragePort.database.add(question);
            }

            FindQuestionsCommand command = FindQuestionsCommand.builder()
                .page(0)
                .size(5)
                .build();

            // when
            FindQuestionsServiceResponse response = findQuestionsService.findQuestions(command);

            // then
            assertThat(response.pageSize()).isEqualTo(5);
            assertThat(response.totalElements()).isEqualTo(20);
            assertThat(response.totalPages()).isEqualTo(4);
            assertThat(response.questions()).hasSize(5);
        }

        @Test
        @DisplayName("[success] 답변이 있는 질문과 없는 질문을 모두 조회한다")
        void success_withAnsweredAndUnanswered() {
            // given
            Question answeredQuestion = Question.builder()
                .id(1L)
                .writerEmail("test1@example.com")
                .writerNickname("tester1")
                .title("답변된 질문")
                .content("내용 1")
                .answered(true)
                .regDt(LocalDateTime.now())
                .build();

            Question unansweredQuestion = Question.builder()
                .id(2L)
                .writerEmail("test2@example.com")
                .writerNickname("tester2")
                .title("답변 안된 질문")
                .content("내용 2")
                .answered(false)
                .regDt(LocalDateTime.now().minusDays(1))
                .build();

            fakeQuestionStoragePort.database.add(answeredQuestion);
            fakeQuestionStoragePort.database.add(unansweredQuestion);

            FindQuestionsCommand command = FindQuestionsCommand.builder()
                .page(0)
                .size(10)
                .build();

            // when
            FindQuestionsServiceResponse response = findQuestionsService.findQuestions(command);

            // then
            assertThat(response.questions()).hasSize(2);
            assertThat(response.questions()).anyMatch(q -> q.getAnswered());
            assertThat(response.questions()).anyMatch(q -> !q.getAnswered());
        }

        @Test
        @DisplayName("[success] 페이지 범위를 벗어나면 빈 결과를 반환한다")
        void success_outOfRange() {
            // given
            Question question = Question.builder()
                .id(1L)
                .writerEmail("test@example.com")
                .writerNickname("tester")
                .title("질문")
                .content("내용")
                .answered(false)
                .regDt(LocalDateTime.now())
                .build();

            fakeQuestionStoragePort.database.add(question);

            FindQuestionsCommand command = FindQuestionsCommand.builder()
                .page(10)
                .size(10)
                .build();

            // when
            FindQuestionsServiceResponse response = findQuestionsService.findQuestions(command);

            // then
            assertThat(response.pageNumber()).isEqualTo(10);
            assertThat(response.totalElements()).isEqualTo(1);
            assertThat(response.questions()).isEmpty();
        }
    }
}
