package com.odcloud.application.service.register_question;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.application.port.in.command.RegisterQuestionCommand;
import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.Question;
import com.odcloud.fakeClass.FakeQuestionStoragePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RegisterQuestionServiceTest {

    private FakeQuestionStoragePort fakeQuestionStoragePort;
    private RegisterQuestionService registerQuestionService;

    @BeforeEach
    void setUp() {
        fakeQuestionStoragePort = new FakeQuestionStoragePort();
        registerQuestionService = new RegisterQuestionService(fakeQuestionStoragePort);
    }

    @Nested
    @DisplayName("[registerQuestion] 질문을 등록하는 메서드")
    class Describe_registerQuestion {

        @Test
        @DisplayName("[success] 정상적으로 질문을 등록한다")
        void success() {
            // given
            Account account = Account.builder()
                .email("test@example.com")
                .nickname("tester")
                .build();

            RegisterQuestionCommand command = RegisterQuestionCommand.builder()
                .account(account)
                .title("테스트 제목")
                .content("테스트 내용")
                .build();

            // when
            RegisterQuestionServiceResponse response = registerQuestionService.registerQuestion(
                command);

            // then
            assertThat(response).isNotNull();
            assertThat(response.result()).isTrue();
            assertThat(fakeQuestionStoragePort.database).hasSize(1);

            Question savedQuestion = fakeQuestionStoragePort.database.get(0);
            assertThat(savedQuestion.getWriterEmail()).isEqualTo("test@example.com");
            assertThat(savedQuestion.getWriterNickname()).isEqualTo("tester");
            assertThat(savedQuestion.getTitle()).isEqualTo("테스트 제목");
            assertThat(savedQuestion.getContent()).isEqualTo("테스트 내용");
            assertThat(savedQuestion.getAnswered()).isFalse();
            assertThat(savedQuestion.getRegDt()).isNotNull();
        }

        @Test
        @DisplayName("[success] 여러 개의 질문을 등록할 수 있다")
        void success_multipleQuestions() {
            // given
            Account account1 = Account.builder()
                .email("test1@example.com")
                .nickname("tester1")
                .build();

            Account account2 = Account.builder()
                .email("test2@example.com")
                .nickname("tester2")
                .build();

            RegisterQuestionCommand command1 = RegisterQuestionCommand.builder()
                .account(account1)
                .title("첫 번째 질문")
                .content("첫 번째 내용")
                .build();

            RegisterQuestionCommand command2 = RegisterQuestionCommand.builder()
                .account(account2)
                .title("두 번째 질문")
                .content("두 번째 내용")
                .build();

            // when
            registerQuestionService.registerQuestion(command1);
            registerQuestionService.registerQuestion(command2);

            // then
            assertThat(fakeQuestionStoragePort.database).hasSize(2);
            assertThat(fakeQuestionStoragePort.database.get(0).getTitle()).isEqualTo("첫 번째 질문");
            assertThat(fakeQuestionStoragePort.database.get(1).getTitle()).isEqualTo("두 번째 질문");
        }

        @Test
        @DisplayName("[success] 같은 사용자가 여러 질문을 등록할 수 있다")
        void success_sameUserMultipleQuestions() {
            // given
            Account account = Account.builder()
                .email("test@example.com")
                .nickname("tester")
                .build();

            RegisterQuestionCommand command1 = RegisterQuestionCommand.builder()
                .account(account)
                .title("첫 번째 질문")
                .content("첫 번째 내용")
                .build();

            RegisterQuestionCommand command2 = RegisterQuestionCommand.builder()
                .account(account)
                .title("두 번째 질문")
                .content("두 번째 내용")
                .build();

            // when
            registerQuestionService.registerQuestion(command1);
            registerQuestionService.registerQuestion(command2);

            // then
            assertThat(fakeQuestionStoragePort.database).hasSize(2);
            assertThat(fakeQuestionStoragePort.database).allMatch(
                q -> q.getWriterEmail().equals("test@example.com"));
        }

        @Test
        @DisplayName("[success] 긴 내용의 질문을 등록할 수 있다")
        void success_longContent() {
            // given
            Account account = Account.builder()
                .email("test@example.com")
                .nickname("tester")
                .build();

            String longContent = "a".repeat(1000);
            RegisterQuestionCommand command = RegisterQuestionCommand.builder()
                .account(account)
                .title("긴 내용 질문")
                .content(longContent)
                .build();

            // when
            RegisterQuestionServiceResponse response = registerQuestionService.registerQuestion(
                command);

            // then
            assertThat(response.result()).isTrue();
            assertThat(fakeQuestionStoragePort.database.get(0).getContent()).hasSize(1000);
        }

        @Test
        @DisplayName("[success] 응답 객체는 성공 상태를 반환한다")
        void success_responseIsSuccess() {
            // given
            Account account = Account.builder()
                .email("test@example.com")
                .nickname("tester")
                .build();

            RegisterQuestionCommand command = RegisterQuestionCommand.builder()
                .account(account)
                .title("테스트 제목")
                .content("테스트 내용")
                .build();

            // when
            RegisterQuestionServiceResponse response = registerQuestionService.registerQuestion(
                command);

            // then
            assertThat(response).isEqualTo(RegisterQuestionServiceResponse.ofSuccess());
            assertThat(response.result()).isTrue();
        }
    }
}
