package com.odcloud.application.question.service.find_question;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.odcloud.domain.model.Answer;
import com.odcloud.domain.model.Question;
import com.odcloud.fakeClass.FakeAnswerStoragePort;
import com.odcloud.fakeClass.FakeQuestionStoragePort;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FindQuestionServiceTest {

    private FakeQuestionStoragePort fakeQuestionStoragePort;
    private FakeAnswerStoragePort fakeAnswerStoragePort;
    private FindQuestionService findQuestionService;

    @BeforeEach
    void setUp() {
        fakeQuestionStoragePort = new FakeQuestionStoragePort();
        fakeAnswerStoragePort = new FakeAnswerStoragePort();
        findQuestionService = new FindQuestionService(fakeQuestionStoragePort,
            fakeAnswerStoragePort);
    }

    @Nested
    @DisplayName("[findQuestion] 질문을 조회하는 메서드")
    class Describe_findQuestion {

        @Test
        @DisplayName("[success] 답변이 없는 질문을 조회한다")
        void success_withoutAnswer() {
            // given
            Question question = Question.builder()
                .id(1L)
                .writerEmail("test@example.com")
                .writerNickname("tester")
                .title("테스트 제목")
                .content("테스트 내용")
                .answered(false)
                .regDt(LocalDateTime.now())
                .build();

            fakeQuestionStoragePort.database.add(question);

            // when
            FindQuestionServiceResponse response = findQuestionService.findQuestion(1L);

            // then
            assertThat(response).isNotNull();
            assertThat(response.question()).isNotNull();
            assertThat(response.question().id()).isEqualTo(1L);
            assertThat(response.question().writerEmail()).isEqualTo("test@example.com");
            assertThat(response.question().writerNickname()).isEqualTo("tester");
            assertThat(response.question().title()).isEqualTo("테스트 제목");
            assertThat(response.question().content()).isEqualTo("테스트 내용");
            assertThat(response.question().answered()).isFalse();
            assertThat(response.question().regDt()).isNotNull();
            assertThat(response.answer()).isNull();
        }

        @Test
        @DisplayName("[success] 답변이 있는 질문을 조회한다")
        void success_withAnswer() {
            // given
            Question question = Question.builder()
                .id(1L)
                .writerEmail("question@example.com")
                .writerNickname("questioner")
                .title("테스트 제목")
                .content("테스트 내용")
                .answered(true)
                .regDt(LocalDateTime.now())
                .modDt(LocalDateTime.now())
                .build();

            Answer answer = Answer.builder()
                .id(1L)
                .questionId(1L)
                .writerEmail("answer@example.com")
                .writerNickname("answerer")
                .content("답변 내용")
                .regDt(LocalDateTime.now())
                .build();

            fakeQuestionStoragePort.database.add(question);
            fakeAnswerStoragePort.database.add(answer);

            // when
            FindQuestionServiceResponse response = findQuestionService.findQuestion(1L);

            // then
            assertThat(response).isNotNull();
            assertThat(response.question()).isNotNull();
            assertThat(response.question().id()).isEqualTo(1L);
            assertThat(response.question().answered()).isTrue();
            assertThat(response.answer()).isNotNull();
            assertThat(response.answer().id()).isEqualTo(1L);
            assertThat(response.answer().questionId()).isEqualTo(1L);
            assertThat(response.answer().writerNickname()).isEqualTo("answerer");
            assertThat(response.answer().content()).isEqualTo("답변 내용");
        }

        @Test
        @DisplayName("[error] 존재하지 않는 질문을 조회하면 예외가 발생한다")
        void error_notFound() {
            // when & then
            assertThatThrownBy(() -> findQuestionService.findQuestion(999L))
                .isInstanceOf(CustomBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode",
                    ErrorCode.Business_NOT_FOUND_QUESTION);
        }

        @Test
        @DisplayName("[success] 질문과 답변의 작성자가 다르다")
        void success_differentWriters() {
            // given
            Question question = Question.builder()
                .id(1L)
                .writerEmail("question@example.com")
                .writerNickname("questioner")
                .title("테스트 제목")
                .content("테스트 내용")
                .answered(true)
                .regDt(LocalDateTime.now())
                .build();

            Answer answer = Answer.builder()
                .id(1L)
                .questionId(1L)
                .writerEmail("answer@example.com")
                .writerNickname("answerer")
                .content("답변 내용")
                .regDt(LocalDateTime.now())
                .build();

            fakeQuestionStoragePort.database.add(question);
            fakeAnswerStoragePort.database.add(answer);

            // when
            FindQuestionServiceResponse response = findQuestionService.findQuestion(1L);

            // then
            assertThat(response.question().writerNickname()).isEqualTo("questioner");
            assertThat(response.answer().writerNickname()).isEqualTo("answerer");
            assertThat(response.question().writerNickname()).isNotEqualTo(
                response.answer().writerNickname());
        }

        @Test
        @DisplayName("[success] 질문의 modDt가 설정되어 있다")
        void success_withModDt() {
            // given
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime modDt = now.minusDays(1);

            Question question = Question.builder()
                .id(1L)
                .writerEmail("test@example.com")
                .writerNickname("tester")
                .title("테스트 제목")
                .content("테스트 내용")
                .answered(true)
                .modDt(modDt)
                .regDt(now.minusDays(2))
                .build();

            fakeQuestionStoragePort.database.add(question);

            // when
            FindQuestionServiceResponse response = findQuestionService.findQuestion(1L);

            // then
            assertThat(response.question().modDt()).isEqualTo(modDt);
        }

        @Test
        @DisplayName("[success] 여러 질문 중 특정 질문을 조회한다")
        void success_findSpecificQuestion() {
            // given
            Question question1 = Question.builder()
                .id(1L)
                .writerEmail("test1@example.com")
                .writerNickname("tester1")
                .title("첫 번째 질문")
                .content("첫 번째 내용")
                .answered(false)
                .regDt(LocalDateTime.now())
                .build();

            Question question2 = Question.builder()
                .id(2L)
                .writerEmail("test2@example.com")
                .writerNickname("tester2")
                .title("두 번째 질문")
                .content("두 번째 내용")
                .answered(false)
                .regDt(LocalDateTime.now())
                .build();

            fakeQuestionStoragePort.database.add(question1);
            fakeQuestionStoragePort.database.add(question2);

            // when
            FindQuestionServiceResponse response = findQuestionService.findQuestion(2L);

            // then
            assertThat(response.question().id()).isEqualTo(2L);
            assertThat(response.question().title()).isEqualTo("두 번째 질문");
            assertThat(response.question().writerNickname()).isEqualTo("tester2");
        }
    }
}
