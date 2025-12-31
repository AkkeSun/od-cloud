package com.odcloud.adapter.in.controller.question.find_question;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.adapter.in.controller.question.find_question.FindQuestionResponse.AnswerDto;
import com.odcloud.adapter.in.controller.question.find_question.FindQuestionResponse.QuestionDto;
import com.odcloud.application.question.service.find_question.FindQuestionServiceResponse;
import com.odcloud.application.question.service.find_question.FindQuestionServiceResponse.AnswerResponseItem;
import com.odcloud.application.question.service.find_question.FindQuestionServiceResponse.QuestionResponseItem;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FindQuestionResponseTest {

    @Nested
    @DisplayName("[of] ServiceResponse로부터 응답을 생성하는 정적 팩토리 메서드")
    class Describe_of {

        @Test
        @DisplayName("[success] 답변이 있는 ServiceResponse로부터 응답을 생성한다")
        void success_withAnswer() {
            // given
            LocalDateTime now = LocalDateTime.now();
            QuestionResponseItem questionItem = QuestionResponseItem.builder()
                .id(1L)
                .writerNickname("questioner")
                .title("테스트 제목")
                .content("테스트 내용")
                .answered(true)
                .regDt(now)
                .build();

            AnswerResponseItem answerItem = AnswerResponseItem.builder()
                .id(1L)
                .writerNickname("answerer")
                .content("답변 내용")
                .regDt(now.minusDays(1))
                .build();

            FindQuestionServiceResponse serviceResponse = FindQuestionServiceResponse.builder()
                .question(questionItem)
                .answer(answerItem)
                .build();

            // when
            FindQuestionResponse response = FindQuestionResponse.of(serviceResponse);

            // then
            assertThat(response).isNotNull();
            assertThat(response.question()).isNotNull();
            assertThat(response.question().id()).isEqualTo(1L);
            assertThat(response.question().writerNickname()).isEqualTo("questioner");
            assertThat(response.question().title()).isEqualTo("테스트 제목");
            assertThat(response.question().content()).isEqualTo("테스트 내용");
            assertThat(response.question().answered()).isTrue();
            assertThat(response.question().regDt()).isEqualTo(now);

            assertThat(response.answer()).isNotNull();
            assertThat(response.answer().id()).isEqualTo(1L);
            assertThat(response.answer().writerNickname()).isEqualTo("answerer");
            assertThat(response.answer().content()).isEqualTo("답변 내용");
            assertThat(response.answer().regDt()).isEqualTo(now.minusDays(1));
        }

        @Test
        @DisplayName("[success] 답변이 없는 ServiceResponse로부터 응답을 생성한다")
        void success_withoutAnswer() {
            // given
            LocalDateTime now = LocalDateTime.now();
            QuestionResponseItem questionItem = QuestionResponseItem.builder()
                .id(1L)
                .writerNickname("questioner")
                .title("테스트 제목")
                .content("테스트 내용")
                .answered(false)
                .regDt(now)
                .build();

            FindQuestionServiceResponse serviceResponse = FindQuestionServiceResponse.builder()
                .question(questionItem)
                .answer(null)
                .build();

            // when
            FindQuestionResponse response = FindQuestionResponse.of(serviceResponse);

            // then
            assertThat(response).isNotNull();
            assertThat(response.question()).isNotNull();
            assertThat(response.answer()).isNull();
        }
    }

    @Nested
    @DisplayName("[QuestionDto] Question DTO 테스트")
    class Describe_QuestionDto {

        @Test
        @DisplayName("[success] QuestionResponseItem으로부터 QuestionDto를 생성한다")
        void success() {
            // given
            LocalDateTime now = LocalDateTime.now();
            QuestionResponseItem item = QuestionResponseItem.builder()
                .id(1L)
                .writerNickname("tester")
                .title("제목")
                .content("내용")
                .answered(false)
                .regDt(now)
                .build();

            // when
            QuestionDto dto = QuestionDto.of(item);

            // then
            assertThat(dto).isNotNull();
            assertThat(dto.id()).isEqualTo(1L);
            assertThat(dto.writerNickname()).isEqualTo("tester");
            assertThat(dto.title()).isEqualTo("제목");
            assertThat(dto.content()).isEqualTo("내용");
            assertThat(dto.answered()).isFalse();
            assertThat(dto.regDt()).isEqualTo(now);
        }

        @Test
        @DisplayName("[success] Builder로 QuestionDto를 생성한다")
        void success_builder() {
            // given
            LocalDateTime now = LocalDateTime.now();

            // when
            QuestionDto dto = QuestionDto.builder()
                .id(1L)
                .writerNickname("tester")
                .title("제목")
                .content("내용")
                .answered(true)
                .regDt(now)
                .build();

            // then
            assertThat(dto).isNotNull();
            assertThat(dto.answered()).isTrue();
        }
    }

    @Nested
    @DisplayName("[AnswerDto] Answer DTO 테스트")
    class Describe_AnswerDto {

        @Test
        @DisplayName("[success] AnswerResponseItem으로부터 AnswerDto를 생성한다")
        void success() {
            // given
            LocalDateTime now = LocalDateTime.now();
            AnswerResponseItem item = AnswerResponseItem.builder()
                .id(1L)
                .writerNickname("answerer")
                .content("답변 내용")
                .regDt(now)
                .build();

            // when
            AnswerDto dto = AnswerDto.of(item);

            // then
            assertThat(dto).isNotNull();
            assertThat(dto.id()).isEqualTo(1L);
            assertThat(dto.writerNickname()).isEqualTo("answerer");
            assertThat(dto.content()).isEqualTo("답변 내용");
            assertThat(dto.regDt()).isEqualTo(now);
        }

        @Test
        @DisplayName("[success] Builder로 AnswerDto를 생성한다")
        void success_builder() {
            // given
            LocalDateTime now = LocalDateTime.now();

            // when
            AnswerDto dto = AnswerDto.builder()
                .id(1L)
                .writerNickname("answerer")
                .content("답변 내용")
                .regDt(now)
                .build();

            // then
            assertThat(dto).isNotNull();
            assertThat(dto.id()).isEqualTo(1L);
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
            QuestionDto question = QuestionDto.builder()
                .id(1L)
                .writerNickname("tester")
                .title("제목")
                .content("내용")
                .answered(false)
                .regDt(now)
                .build();

            // when
            FindQuestionResponse response = FindQuestionResponse.builder()
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
            QuestionDto question = QuestionDto.builder()
                .id(1L)
                .writerNickname("tester")
                .title("제목")
                .content("내용")
                .answered(false)
                .regDt(now)
                .build();

            FindQuestionResponse response1 = FindQuestionResponse.builder()
                .question(question)
                .answer(null)
                .build();

            FindQuestionResponse response2 = FindQuestionResponse.builder()
                .question(question)
                .answer(null)
                .build();

            // when & then
            assertThat(response1).isEqualTo(response2);
            assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        }
    }

    @Nested
    @DisplayName("[accessor] 응답 accessor 메서드 테스트")
    class Describe_accessor {

        @Test
        @DisplayName("[success] 모든 accessor 메서드가 정상 작동한다")
        void success() {
            // given
            LocalDateTime now = LocalDateTime.now();
            QuestionDto question = QuestionDto.builder()
                .id(1L)
                .writerNickname("tester")
                .title("제목")
                .content("내용")
                .answered(false)
                .regDt(now)
                .build();

            AnswerDto answer = AnswerDto.builder()
                .id(1L)
                .writerNickname("answerer")
                .content("답변")
                .regDt(now)
                .build();

            FindQuestionResponse response = FindQuestionResponse.builder()
                .question(question)
                .answer(answer)
                .build();

            // when & then
            assertThat(response.question()).isEqualTo(question);
            assertThat(response.answer()).isEqualTo(answer);
        }
    }
}
