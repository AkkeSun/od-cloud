package com.odcloud.adapter.in.controller.question.find_questions;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.adapter.in.controller.question.find_questions.FindQuestionsResponse.QuestionDto;
import com.odcloud.application.service.find_questions.FindQuestionsServiceResponse;
import com.odcloud.domain.model.Question;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FindQuestionsResponseTest {

    @Nested
    @DisplayName("[of] ServiceResponse로부터 응답을 생성하는 정적 팩토리 메서드")
    class Describe_of {

        @Test
        @DisplayName("[success] ServiceResponse로부터 응답을 생성한다")
        void success() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Question question1 = Question.builder()
                .id(1L)
                .writerNickname("tester1")
                .title("질문 1")
                .answered(false)
                .regDt(now)
                .build();

            Question question2 = Question.builder()
                .id(2L)
                .writerNickname("tester2")
                .title("질문 2")
                .answered(true)
                .regDt(now.minusDays(1))
                .build();

            FindQuestionsServiceResponse serviceResponse = FindQuestionsServiceResponse.builder()
                .pageNumber(0)
                .pageSize(10)
                .totalElements(15)
                .totalPages(2)
                .questions(List.of(question1, question2))
                .build();

            // when
            FindQuestionsResponse response = FindQuestionsResponse.of(serviceResponse);

            // then
            assertThat(response).isNotNull();
            assertThat(response.pageNumber()).isEqualTo(0);
            assertThat(response.pageSize()).isEqualTo(10);
            assertThat(response.totalElements()).isEqualTo(15);
            assertThat(response.totalPages()).isEqualTo(2);
            assertThat(response.questions()).hasSize(2);
            assertThat(response.questions().get(0).id()).isEqualTo(1L);
            assertThat(response.questions().get(1).id()).isEqualTo(2L);
        }

        @Test
        @DisplayName("[success] 빈 ServiceResponse로부터 응답을 생성한다")
        void success_empty() {
            // given
            FindQuestionsServiceResponse serviceResponse = FindQuestionsServiceResponse.builder()
                .pageNumber(0)
                .pageSize(10)
                .totalElements(0)
                .totalPages(0)
                .questions(List.of())
                .build();

            // when
            FindQuestionsResponse response = FindQuestionsResponse.of(serviceResponse);

            // then
            assertThat(response.pageNumber()).isEqualTo(0);
            assertThat(response.pageSize()).isEqualTo(10);
            assertThat(response.totalElements()).isEqualTo(0);
            assertThat(response.totalPages()).isEqualTo(0);
            assertThat(response.questions()).isEmpty();
        }
    }

    @Nested
    @DisplayName("[QuestionDto] Question DTO 테스트")
    class Describe_QuestionDto {

        @Test
        @DisplayName("[success] Question으로부터 QuestionDto를 생성한다")
        void success() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Question question = Question.builder()
                .id(1L)
                .writerNickname("tester")
                .title("테스트 제목")
                .answered(false)
                .regDt(now)
                .build();

            // when
            QuestionDto dto = QuestionDto.of(question);

            // then
            assertThat(dto).isNotNull();
            assertThat(dto.id()).isEqualTo(1L);
            assertThat(dto.writerNickname()).isEqualTo("tester");
            assertThat(dto.title()).isEqualTo("테스트 제목");
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
                .answered(true)
                .regDt(now)
                .build();

            // then
            assertThat(dto).isNotNull();
            assertThat(dto.answered()).isTrue();
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
                .answered(false)
                .regDt(now)
                .build();

            // when
            FindQuestionsResponse response = FindQuestionsResponse.builder()
                .pageNumber(0)
                .pageSize(10)
                .totalElements(1)
                .totalPages(1)
                .questions(List.of(question))
                .build();

            // then
            assertThat(response).isNotNull();
            assertThat(response.pageNumber()).isEqualTo(0);
            assertThat(response.pageSize()).isEqualTo(10);
            assertThat(response.totalElements()).isEqualTo(1);
            assertThat(response.totalPages()).isEqualTo(1);
            assertThat(response.questions()).hasSize(1);
        }

        @Test
        @DisplayName("[success] 빈 질문 목록으로 응답을 생성한다")
        void success_emptyQuestions() {
            // when
            FindQuestionsResponse response = FindQuestionsResponse.builder()
                .pageNumber(0)
                .pageSize(10)
                .totalElements(0)
                .totalPages(0)
                .questions(List.of())
                .build();

            // then
            assertThat(response.questions()).isEmpty();
        }
    }

    @Nested
    @DisplayName("[record] Record 불변성 테스트")
    class Describe_immutability {

        @Test
        @DisplayName("[success] 응답은 불변 객체이다")
        void success() {
            // given
            FindQuestionsResponse response1 = FindQuestionsResponse.builder()
                .pageNumber(0)
                .pageSize(10)
                .totalElements(0)
                .totalPages(0)
                .questions(List.of())
                .build();

            FindQuestionsResponse response2 = FindQuestionsResponse.builder()
                .pageNumber(0)
                .pageSize(10)
                .totalElements(0)
                .totalPages(0)
                .questions(List.of())
                .build();

            // when & then
            assertThat(response1).isEqualTo(response2);
            assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        }

        @Test
        @DisplayName("[success] 다른 값으로 생성된 응답은 동등하지 않다")
        void success_notEqual() {
            // given
            FindQuestionsResponse response1 = FindQuestionsResponse.builder()
                .pageNumber(0)
                .pageSize(10)
                .totalElements(0)
                .totalPages(0)
                .questions(List.of())
                .build();

            FindQuestionsResponse response2 = FindQuestionsResponse.builder()
                .pageNumber(1)
                .pageSize(10)
                .totalElements(0)
                .totalPages(0)
                .questions(List.of())
                .build();

            // when & then
            assertThat(response1).isNotEqualTo(response2);
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
                .answered(false)
                .regDt(now)
                .build();

            FindQuestionsResponse response = FindQuestionsResponse.builder()
                .pageNumber(0)
                .pageSize(10)
                .totalElements(1)
                .totalPages(1)
                .questions(List.of(question))
                .build();

            // when & then
            assertThat(response.pageNumber()).isEqualTo(0);
            assertThat(response.pageSize()).isEqualTo(10);
            assertThat(response.totalElements()).isEqualTo(1);
            assertThat(response.totalPages()).isEqualTo(1);
            assertThat(response.questions()).hasSize(1);
            assertThat(response.questions().get(0)).isEqualTo(question);
        }
    }
}
