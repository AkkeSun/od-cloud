package com.odcloud.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.odcloud.application.port.in.command.RegisterQuestionCommand;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class QuestionTest {

    @Nested
    @DisplayName("[create] RegisterQuestionCommand로부터 Question을 생성하는 정적 팩토리 메서드")
    class Describe_create {

        @Test
        @DisplayName("[success] RegisterQuestionCommand로부터 Question을 생성한다")
        void success() {
            // given
            Account account = Account.builder()
                .email("test@example.com")
                .nickname("tester")
                .build();

            RegisterQuestionCommand command = mock(RegisterQuestionCommand.class);
            when(command.account()).thenReturn(account);
            when(command.title()).thenReturn("테스트 제목");
            when(command.content()).thenReturn("테스트 내용");

            // when
            Question question = Question.create(command);

            // then
            assertThat(question).isNotNull();
            assertThat(question.getWriterEmail()).isEqualTo("test@example.com");
            assertThat(question.getWriterNickname()).isEqualTo("tester");
            assertThat(question.getTitle()).isEqualTo("테스트 제목");
            assertThat(question.getContent()).isEqualTo("테스트 내용");
            assertThat(question.getAnswered()).isFalse();
            assertThat(question.getRegDt()).isNotNull();
            assertThat(question.getId()).isNull();
            assertThat(question.getModDt()).isNull();
        }

        @Test
        @DisplayName("[success] 생성된 Question의 answered는 false이다")
        void success_answeredIsFalse() {
            // given
            Account account = Account.builder()
                .email("test@example.com")
                .nickname("tester")
                .build();

            RegisterQuestionCommand command = mock(RegisterQuestionCommand.class);
            when(command.account()).thenReturn(account);
            when(command.title()).thenReturn("테스트 제목");
            when(command.content()).thenReturn("테스트 내용");

            // when
            Question question = Question.create(command);

            // then
            assertThat(question.getAnswered()).isFalse();
        }

        @Test
        @DisplayName("[success] 생성된 Question의 regDt는 현재 시각이다")
        void success_regDtIsNow() {
            // given
            LocalDateTime beforeCreate = LocalDateTime.now().minusSeconds(1);
            Account account = Account.builder()
                .email("test@example.com")
                .nickname("tester")
                .build();

            RegisterQuestionCommand command = mock(RegisterQuestionCommand.class);
            when(command.account()).thenReturn(account);
            when(command.title()).thenReturn("테스트 제목");
            when(command.content()).thenReturn("테스트 내용");

            // when
            Question question = Question.create(command);
            LocalDateTime afterCreate = LocalDateTime.now().plusSeconds(1);

            // then
            assertThat(question.getRegDt()).isAfter(beforeCreate);
            assertThat(question.getRegDt()).isBefore(afterCreate);
        }
    }

    @Nested
    @DisplayName("[markAsAnswered] 질문에 답변이 달렸음을 표시하는 메서드")
    class Describe_markAsAnswered {

        @Test
        @DisplayName("[success] answered를 true로 변경한다")
        void success_answeredIsTrue() {
            // given
            Question question = Question.builder()
                .writerEmail("test@example.com")
                .writerNickname("tester")
                .title("테스트 제목")
                .content("테스트 내용")
                .answered(false)
                .regDt(LocalDateTime.now())
                .build();

            // when
            question.markAsAnswered();

            // then
            assertThat(question.getAnswered()).isTrue();
        }

        @Test
        @DisplayName("[success] modDt를 현재 시각으로 설정한다")
        void success_setModDt() {
            // given
            LocalDateTime beforeMark = LocalDateTime.now().minusSeconds(1);
            Question question = Question.builder()
                .writerEmail("test@example.com")
                .writerNickname("tester")
                .title("테스트 제목")
                .content("테스트 내용")
                .answered(false)
                .regDt(LocalDateTime.now())
                .build();

            // when
            question.markAsAnswered();
            LocalDateTime afterMark = LocalDateTime.now().plusSeconds(1);

            // then
            assertThat(question.getModDt()).isNotNull();
            assertThat(question.getModDt()).isAfter(beforeMark);
            assertThat(question.getModDt()).isBefore(afterMark);
        }

        @Test
        @DisplayName("[success] 이미 answered가 true인 경우에도 modDt를 업데이트한다")
        void success_alreadyAnswered() throws InterruptedException {
            // given
            LocalDateTime originalModDt = LocalDateTime.now().minusDays(1);
            Question question = Question.builder()
                .writerEmail("test@example.com")
                .writerNickname("tester")
                .title("테스트 제목")
                .content("테스트 내용")
                .answered(true)
                .modDt(originalModDt)
                .regDt(LocalDateTime.now().minusDays(2))
                .build();

            // when
            Thread.sleep(10); // 시간 차이를 만들기 위해 대기
            question.markAsAnswered();

            // then
            assertThat(question.getAnswered()).isTrue();
            assertThat(question.getModDt()).isAfter(originalModDt);
        }
    }

    @Nested
    @DisplayName("[builder] Builder 패턴 테스트")
    class Describe_builder {

        @Test
        @DisplayName("[success] Builder로 Question을 생성한다")
        void success() {
            // given
            LocalDateTime now = LocalDateTime.now();

            // when
            Question question = Question.builder()
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
            assertThat(question).isNotNull();
            assertThat(question.getId()).isEqualTo(1L);
            assertThat(question.getWriterEmail()).isEqualTo("test@example.com");
            assertThat(question.getWriterNickname()).isEqualTo("tester");
            assertThat(question.getTitle()).isEqualTo("테스트 제목");
            assertThat(question.getContent()).isEqualTo("테스트 내용");
            assertThat(question.getAnswered()).isFalse();
            assertThat(question.getModDt()).isNull();
            assertThat(question.getRegDt()).isEqualTo(now);
        }

        @Test
        @DisplayName("[success] 모든 필드를 null로 Question을 생성할 수 있다")
        void success_allNullFields() {
            // when
            Question question = Question.builder()
                .id(null)
                .writerEmail(null)
                .writerNickname(null)
                .title(null)
                .content(null)
                .answered(null)
                .modDt(null)
                .regDt(null)
                .build();

            // then
            assertThat(question).isNotNull();
            assertThat(question.getId()).isNull();
            assertThat(question.getWriterEmail()).isNull();
            assertThat(question.getWriterNickname()).isNull();
            assertThat(question.getTitle()).isNull();
            assertThat(question.getContent()).isNull();
            assertThat(question.getAnswered()).isNull();
            assertThat(question.getModDt()).isNull();
            assertThat(question.getRegDt()).isNull();
        }
    }

    @Nested
    @DisplayName("[getter] Question getter 메서드 테스트")
    class Describe_getter {

        @Test
        @DisplayName("[success] 모든 getter 메서드가 정상 작동한다")
        void success() {
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
                .regDt(now)
                .build();

            // when & then
            assertThat(question.getId()).isEqualTo(1L);
            assertThat(question.getWriterEmail()).isEqualTo("test@example.com");
            assertThat(question.getWriterNickname()).isEqualTo("tester");
            assertThat(question.getTitle()).isEqualTo("테스트 제목");
            assertThat(question.getContent()).isEqualTo("테스트 내용");
            assertThat(question.getAnswered()).isTrue();
            assertThat(question.getModDt()).isEqualTo(modDt);
            assertThat(question.getRegDt()).isEqualTo(now);
        }
    }
}
