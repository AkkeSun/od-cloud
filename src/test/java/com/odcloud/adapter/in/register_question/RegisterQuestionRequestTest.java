package com.odcloud.adapter.in.register_question;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.application.port.in.command.RegisterQuestionCommand;
import com.odcloud.domain.model.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RegisterQuestionRequestTest {

    @Nested
    @DisplayName("[toCommand] Request를 Command로 변환하는 메서드")
    class Describe_toCommand {

        @Test
        @DisplayName("[success] Request를 Command로 변환한다")
        void success() {
            // given
            RegisterQuestionRequest request = RegisterQuestionRequest.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .build();

            Account account = Account.builder()
                .email("test@example.com")
                .nickname("tester")
                .build();

            // when
            RegisterQuestionCommand command = request.toCommand(account);

            // then
            assertThat(command).isNotNull();
            assertThat(command.account()).isEqualTo(account);
            assertThat(command.title()).isEqualTo("테스트 제목");
            assertThat(command.content()).isEqualTo("테스트 내용");
        }

        @Test
        @DisplayName("[success] null 값이 포함된 Request를 Command로 변환한다")
        void success_withNullValues() {
            // given
            RegisterQuestionRequest request = RegisterQuestionRequest.builder()
                .title(null)
                .content(null)
                .build();

            Account account = null;

            // when
            RegisterQuestionCommand command = request.toCommand(account);

            // then
            assertThat(command).isNotNull();
            assertThat(command.account()).isNull();
            assertThat(command.title()).isNull();
            assertThat(command.content()).isNull();
        }

        @Test
        @DisplayName("[success] 빈 문자열이 포함된 Request를 Command로 변환한다")
        void success_withEmptyStrings() {
            // given
            RegisterQuestionRequest request = RegisterQuestionRequest.builder()
                .title("")
                .content("")
                .build();

            Account account = Account.builder()
                .email("")
                .nickname("")
                .build();

            // when
            RegisterQuestionCommand command = request.toCommand(account);

            // then
            assertThat(command).isNotNull();
            assertThat(command.account()).isNotNull();
            assertThat(command.title()).isEmpty();
            assertThat(command.content()).isEmpty();
        }

        @Test
        @DisplayName("[success] 긴 내용이 포함된 Request를 Command로 변환한다")
        void success_withLongContent() {
            // given
            String longContent = "a".repeat(1000);
            RegisterQuestionRequest request = RegisterQuestionRequest.builder()
                .title("긴 내용 질문")
                .content(longContent)
                .build();

            Account account = Account.builder()
                .email("test@example.com")
                .nickname("tester")
                .build();

            // when
            RegisterQuestionCommand command = request.toCommand(account);

            // then
            assertThat(command.content()).hasSize(1000);
        }
    }

    @Nested
    @DisplayName("[builder] Builder 패턴 테스트")
    class Describe_builder {

        @Test
        @DisplayName("[success] Builder로 Request를 생성한다")
        void success() {
            // when
            RegisterQuestionRequest request = RegisterQuestionRequest.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .build();

            // then
            assertThat(request).isNotNull();
            assertThat(request.title()).isEqualTo("테스트 제목");
            assertThat(request.content()).isEqualTo("테스트 내용");
        }

        @Test
        @DisplayName("[success] null 값으로 Request를 생성한다")
        void success_nullValues() {
            // when
            RegisterQuestionRequest request = RegisterQuestionRequest.builder()
                .title(null)
                .content(null)
                .build();

            // then
            assertThat(request.title()).isNull();
            assertThat(request.content()).isNull();
        }
    }

    @Nested
    @DisplayName("[record] Record 불변성 테스트")
    class Describe_immutability {

        @Test
        @DisplayName("[success] Request는 불변 객체이다")
        void success() {
            // given
            RegisterQuestionRequest request1 = RegisterQuestionRequest.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .build();

            RegisterQuestionRequest request2 = RegisterQuestionRequest.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .build();

            // when & then
            assertThat(request1).isEqualTo(request2);
            assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
        }

        @Test
        @DisplayName("[success] 다른 값으로 생성된 Request는 동등하지 않다")
        void success_notEqual() {
            // given
            RegisterQuestionRequest request1 = RegisterQuestionRequest.builder()
                .title("제목 1")
                .content("내용 1")
                .build();

            RegisterQuestionRequest request2 = RegisterQuestionRequest.builder()
                .title("제목 2")
                .content("내용 2")
                .build();

            // when & then
            assertThat(request1).isNotEqualTo(request2);
        }
    }

    @Nested
    @DisplayName("[accessor] Request accessor 메서드 테스트")
    class Describe_accessor {

        @Test
        @DisplayName("[success] title()로 값을 조회한다")
        void success_title() {
            // given
            RegisterQuestionRequest request = RegisterQuestionRequest.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .build();

            // when
            String title = request.title();

            // then
            assertThat(title).isEqualTo("테스트 제목");
        }

        @Test
        @DisplayName("[success] content()로 값을 조회한다")
        void success_content() {
            // given
            RegisterQuestionRequest request = RegisterQuestionRequest.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .build();

            // when
            String content = request.content();

            // then
            assertThat(content).isEqualTo("테스트 내용");
        }
    }

    @Nested
    @DisplayName("[toString] Request toString 메서드 테스트")
    class Describe_toString {

        @Test
        @DisplayName("[success] toString()으로 문자열 표현을 반환한다")
        void success() {
            // given
            RegisterQuestionRequest request = RegisterQuestionRequest.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .build();

            // when
            String result = request.toString();

            // then
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
        }
    }
}
