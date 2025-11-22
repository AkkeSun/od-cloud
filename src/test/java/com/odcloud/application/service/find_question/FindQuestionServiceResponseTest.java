package com.odcloud.application.service.find_question;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.application.service.find_question.FindQuestionServiceResponse.AnswerResponseItem;
import com.odcloud.application.service.find_question.FindQuestionServiceResponse.QuestionResponseItem;
import com.odcloud.domain.model.Answer;
import com.odcloud.domain.model.Question;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FindQuestionServiceResponseTest {

    @Nested
    @DisplayName("[of] Domain 모델로부터 응답을 생성하는 정적 팩토리 메서드")
    class Describe_of {

        @Test
        @DisplayName("[success] Question과 Answer로부터 응답을 생성한다")
        void success_withAnswer() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Question question = Question.builder()
                .id(1L)
                .writerEmail("question@example.com")
                .writerNickname("questioner")
                .title("테스트 제목")
                .content("테스트 내용")
                .answered(true)
                .modDt(now.minusDays(1))
                .regDt(now.minusDays(2))
                .build();

            Answer answer = Answer.builder()
                .id(1L)
                .questionId(1L)
                .writerEmail("answer@example.com")
                .writerNickname("answerer")
                .content("답변 내용")
                .modDt(now.minusHours(1))
                .regDt(now.minusHours(2))
                .build();

            // when
            FindQuestionServiceResponse response = FindQuestionServiceResponse.of(question,
                answer);

            // then
            assertThat(response).isNotNull();
            assertThat(response.question()).isNotNull();
            assertThat(response.question().id()).isEqualTo(1L);
            assertThat(response.question().writerEmail()).isEqualTo("question@example.com");
            assertThat(response.question().writerNickname()).isEqualTo("questioner");
            assertThat(response.question().title()).isEqualTo("테스트 제목");
            assertThat(response.question().content()).isEqualTo("테스트 내용");
            assertThat(response.question().answered()).isTrue();
            assertThat(response.question().modDt()).isEqualTo(now.minusDays(1));
            assertThat(response.question().regDt()).isEqualTo(now.minusDays(2));

            assertThat(response.answer()).isNotNull();
            assertThat(response.answer().id()).isEqualTo(1L);
            assertThat(response.answer().questionId()).isEqualTo(1L);
            assertThat(response.answer().writerEmail()).isEqualTo("answer@example.com");
            assertThat(response.answer().writerNickname()).isEqualTo("answerer");
            assertThat(response.answer().content()).isEqualTo("답변 내용");
            assertThat(response.answer().modDt()).isEqualTo(now.minusHours(1));
            assertThat(response.answer().regDt()).isEqualTo(now.minusHours(2));
        }

        @Test
        @DisplayName("[success] Question만으로 응답을 생성한다 (Answer가 null)")
        void success_withoutAnswer() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Question question = Question.builder()
                .id(1L)
                .writerEmail("test@example.com")
                .writerNickname("tester")
                .title("테스트 제목")
                .content("테스트 내용")
                .answered(false)
                .regDt(now)
                .build();

            // when
            FindQuestionServiceResponse response = FindQuestionServiceResponse.of(question, null);

            // then
            assertThat(response).isNotNull();
            assertThat(response.question()).isNotNull();
            assertThat(response.answer()).isNull();
        }
    }

    @Nested
    @DisplayName("[QuestionResponseItem] Question 응답 항목 테스트")
    class Describe_QuestionResponseItem {

        @Test
        @DisplayName("[success] Question으로부터 QuestionResponseItem을 생성한다")
        void success() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Question question = Question.builder()
                .id(1L)
                .writerEmail("test@example.com")
                .writerNickname("tester")
                .title("테스트 제목")
                .content("테스트 내용")
                .answered(true)
                .modDt(now.minusDays(1))
                .regDt(now.minusDays(2))
                .build();

            // when
            QuestionResponseItem item = QuestionResponseItem.of(question);

            // then
            assertThat(item).isNotNull();
            assertThat(item.id()).isEqualTo(1L);
            assertThat(item.writerEmail()).isEqualTo("test@example.com");
            assertThat(item.writerNickname()).isEqualTo("tester");
            assertThat(item.title()).isEqualTo("테스트 제목");
            assertThat(item.content()).isEqualTo("테스트 내용");
            assertThat(item.answered()).isTrue();
            assertThat(item.modDt()).isEqualTo(now.minusDays(1));
            assertThat(item.regDt()).isEqualTo(now.minusDays(2));
        }

        @Test
        @DisplayName("[success] Builder로 QuestionResponseItem을 생성한다")
        void success_builder() {
            // given
            LocalDateTime now = LocalDateTime.now();

            // when
            QuestionResponseItem item = QuestionResponseItem.builder()
                .id(1L)
                .writerEmail("test@example.com")
                .writerNickname("tester")
                .title("테스트 제목")
                .content("테스트 내용")
                .answered(false)
                .modDt(null)
                .regDt(now)
                .build();

            // then
            assertThat(item).isNotNull();
            assertThat(item.id()).isEqualTo(1L);
            assertThat(item.modDt()).isNull();
        }
    }

    @Nested
    @DisplayName("[AnswerResponseItem] Answer 응답 항목 테스트")
    class Describe_AnswerResponseItem {

        @Test
        @DisplayName("[success] Answer로부터 AnswerResponseItem을 생성한다")
        void success() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Answer answer = Answer.builder()
                .id(1L)
                .questionId(10L)
                .writerEmail("answer@example.com")
                .writerNickname("answerer")
                .content("답변 내용")
                .modDt(now.minusDays(1))
                .regDt(now.minusDays(2))
                .build();

            // when
            AnswerResponseItem item = AnswerResponseItem.of(answer);

            // then
            assertThat(item).isNotNull();
            assertThat(item.id()).isEqualTo(1L);
            assertThat(item.questionId()).isEqualTo(10L);
            assertThat(item.writerEmail()).isEqualTo("answer@example.com");
            assertThat(item.writerNickname()).isEqualTo("answerer");
            assertThat(item.content()).isEqualTo("답변 내용");
            assertThat(item.modDt()).isEqualTo(now.minusDays(1));
            assertThat(item.regDt()).isEqualTo(now.minusDays(2));
        }

        @Test
        @DisplayName("[success] Builder로 AnswerResponseItem을 생성한다")
        void success_builder() {
            // given
            LocalDateTime now = LocalDateTime.now();

            // when
            AnswerResponseItem item = AnswerResponseItem.builder()
                .id(1L)
                .questionId(10L)
                .writerEmail("answer@example.com")
                .writerNickname("answerer")
                .content("답변 내용")
                .modDt(null)
                .regDt(now)
                .build();

            // then
            assertThat(item).isNotNull();
            assertThat(item.id()).isEqualTo(1L);
            assertThat(item.questionId()).isEqualTo(10L);
            assertThat(item.modDt()).isNull();
        }
    }

    @Nested
    @DisplayName("[builder] Builder 패턴 테스트")
    class Describe_builder {

        @Test
        @DisplayName("[success] Builder로 응답을 생성한다")
        void success() {
            // given
            LocalDateTime now = LocalDateTime.now();
            QuestionResponseItem question = QuestionResponseItem.builder()
                .id(1L)
                .writerEmail("test@example.com")
                .writerNickname("tester")
                .title("테스트 제목")
                .content("테스트 내용")
                .answered(false)
                .regDt(now)
                .build();

            // when
            FindQuestionServiceResponse response = FindQuestionServiceResponse.builder()
                .question(question)
                .answer(null)
                .build();

            // then
            assertThat(response).isNotNull();
            assertThat(response.question()).isEqualTo(question);
            assertThat(response.answer()).isNull();
        }
    }

    @Nested
    @DisplayName("[record] Record 불변성 테스트")
    class Describe_immutability {

        @Test
        @DisplayName("[success] 응답은 불변 객체이다")
        void success() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Question question = Question.builder()
                .id(1L)
                .writerEmail("test@example.com")
                .writerNickname("tester")
                .title("테스트 제목")
                .content("테스트 내용")
                .answered(false)
                .regDt(now)
                .build();

            FindQuestionServiceResponse response1 = FindQuestionServiceResponse.of(question, null);
            FindQuestionServiceResponse response2 = FindQuestionServiceResponse.of(question, null);

            // when & then
            assertThat(response1).isEqualTo(response2);
            assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        }
    }
}
