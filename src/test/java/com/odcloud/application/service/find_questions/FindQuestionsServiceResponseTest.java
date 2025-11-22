package com.odcloud.application.service.find_questions;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.domain.model.Question;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

class FindQuestionsServiceResponseTest {

    @Nested
    @DisplayName("[of] Page<Question>으로부터 응답을 생성하는 정적 팩토리 메서드")
    class Describe_of {

        @Test
        @DisplayName("[success] Page<Question>으로부터 응답을 생성한다")
        void success() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Question question1 = Question.builder()
                .id(1L)
                .writerEmail("test1@example.com")
                .writerNickname("tester1")
                .title("질문 1")
                .content("내용 1")
                .answered(false)
                .regDt(now)
                .build();

            Question question2 = Question.builder()
                .id(2L)
                .writerEmail("test2@example.com")
                .writerNickname("tester2")
                .title("질문 2")
                .content("내용 2")
                .answered(true)
                .regDt(now.minusDays(1))
                .build();

            List<Question> questions = List.of(question1, question2);
            Page<Question> page = new PageImpl<>(questions, PageRequest.of(0, 10), 15);

            // when
            FindQuestionsServiceResponse response = FindQuestionsServiceResponse.of(page);

            // then
            assertThat(response).isNotNull();
            assertThat(response.pageNumber()).isEqualTo(0);
            assertThat(response.pageSize()).isEqualTo(10);
            assertThat(response.totalElements()).isEqualTo(15);
            assertThat(response.totalPages()).isEqualTo(2);
            assertThat(response.questions()).hasSize(2);
            assertThat(response.questions()).containsExactly(question1, question2);
        }

        @Test
        @DisplayName("[success] 빈 페이지로부터 응답을 생성한다")
        void success_emptyPage() {
            // given
            Page<Question> page = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);

            // when
            FindQuestionsServiceResponse response = FindQuestionsServiceResponse.of(page);

            // then
            assertThat(response.pageNumber()).isEqualTo(0);
            assertThat(response.pageSize()).isEqualTo(10);
            assertThat(response.totalElements()).isEqualTo(0);
            assertThat(response.totalPages()).isEqualTo(0);
            assertThat(response.questions()).isEmpty();
        }

        @Test
        @DisplayName("[success] 두 번째 페이지로부터 응답을 생성한다")
        void success_secondPage() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Question question = Question.builder()
                .id(11L)
                .writerEmail("test@example.com")
                .writerNickname("tester")
                .title("질문 11")
                .content("내용 11")
                .answered(false)
                .regDt(now)
                .build();

            Page<Question> page = new PageImpl<>(List.of(question), PageRequest.of(1, 10), 11);

            // when
            FindQuestionsServiceResponse response = FindQuestionsServiceResponse.of(page);

            // then
            assertThat(response.pageNumber()).isEqualTo(1);
            assertThat(response.pageSize()).isEqualTo(10);
            assertThat(response.totalElements()).isEqualTo(11);
            assertThat(response.totalPages()).isEqualTo(2);
            assertThat(response.questions()).hasSize(1);
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
            Question question = Question.builder()
                .id(1L)
                .writerEmail("test@example.com")
                .writerNickname("tester")
                .title("질문")
                .content("내용")
                .answered(false)
                .regDt(now)
                .build();

            // when
            FindQuestionsServiceResponse response = FindQuestionsServiceResponse.builder()
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
            FindQuestionsServiceResponse response = FindQuestionsServiceResponse.builder()
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
            Page<Question> page = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
            FindQuestionsServiceResponse response1 = FindQuestionsServiceResponse.of(page);
            FindQuestionsServiceResponse response2 = FindQuestionsServiceResponse.of(page);

            // when & then
            assertThat(response1).isEqualTo(response2);
            assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        }

        @Test
        @DisplayName("[success] 다른 값으로 생성된 응답은 동등하지 않다")
        void success_notEqual() {
            // given
            Page<Question> page1 = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
            Page<Question> page2 = new PageImpl<>(List.of(), PageRequest.of(1, 10), 0);

            FindQuestionsServiceResponse response1 = FindQuestionsServiceResponse.of(page1);
            FindQuestionsServiceResponse response2 = FindQuestionsServiceResponse.of(page2);

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
            Question question = Question.builder()
                .id(1L)
                .writerEmail("test@example.com")
                .writerNickname("tester")
                .title("질문")
                .content("내용")
                .answered(false)
                .regDt(now)
                .build();

            Page<Question> page = new PageImpl<>(List.of(question), PageRequest.of(0, 10), 1);
            FindQuestionsServiceResponse response = FindQuestionsServiceResponse.of(page);

            // when & then
            assertThat(response.pageNumber()).isEqualTo(0);
            assertThat(response.pageSize()).isEqualTo(10);
            assertThat(response.totalElements()).isEqualTo(1);
            assertThat(response.totalPages()).isEqualTo(1);
            assertThat(response.questions()).hasSize(1);
            assertThat(response.questions().get(0)).isEqualTo(question);
        }
    }

    @Nested
    @DisplayName("[toString] 응답 toString 메서드 테스트")
    class Describe_toString {

        @Test
        @DisplayName("[success] toString()으로 문자열 표현을 반환한다")
        void success() {
            // given
            Page<Question> page = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
            FindQuestionsServiceResponse response = FindQuestionsServiceResponse.of(page);

            // when
            String result = response.toString();

            // then
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
        }
    }
}
