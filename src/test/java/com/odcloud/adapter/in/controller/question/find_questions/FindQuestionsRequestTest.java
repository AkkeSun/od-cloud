package com.odcloud.adapter.in.controller.question.find_questions;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.application.port.in.command.FindQuestionsCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FindQuestionsRequestTest {

    @Nested
    @DisplayName("[toCommand] Request를 Command로 변환하는 메서드")
    class Describe_toCommand {

        @Test
        @DisplayName("[success] Request를 Command로 변환한다")
        void success() {
            // given
            FindQuestionsRequest request = FindQuestionsRequest.builder()
                .page(0)
                .size(10)
                .build();

            // when
            FindQuestionsCommand command = request.toCommand();

            // then
            assertThat(command).isNotNull();
            assertThat(command.page()).isEqualTo(0);
            assertThat(command.size()).isEqualTo(10);
        }

        @Test
        @DisplayName("[success] page가 null인 경우 기본값 0으로 변환한다")
        void success_pageIsNull() {
            // given
            FindQuestionsRequest request = FindQuestionsRequest.builder()
                .page(null)
                .size(10)
                .build();

            // when
            FindQuestionsCommand command = request.toCommand();

            // then
            assertThat(command.page()).isEqualTo(0);
        }

        @Test
        @DisplayName("[success] size가 null인 경우 기본값 10으로 변환한다")
        void success_sizeIsNull() {
            // given
            FindQuestionsRequest request = FindQuestionsRequest.builder()
                .page(0)
                .size(null)
                .build();

            // when
            FindQuestionsCommand command = request.toCommand();

            // then
            assertThat(command.size()).isEqualTo(10);
        }

        @Test
        @DisplayName("[success] 둘 다 null인 경우 기본값으로 변환한다")
        void success_bothNull() {
            // given
            FindQuestionsRequest request = FindQuestionsRequest.builder()
                .page(null)
                .size(null)
                .build();

            // when
            FindQuestionsCommand command = request.toCommand();

            // then
            assertThat(command.page()).isEqualTo(0);
            assertThat(command.size()).isEqualTo(10);
        }

        @Test
        @DisplayName("[success] 다양한 페이지와 사이즈로 변환한다")
        void success_variousValues() {
            // given
            FindQuestionsRequest request = FindQuestionsRequest.builder()
                .page(2)
                .size(20)
                .build();

            // when
            FindQuestionsCommand command = request.toCommand();

            // then
            assertThat(command.page()).isEqualTo(2);
            assertThat(command.size()).isEqualTo(20);
        }
    }

    @Nested
    @DisplayName("[builder] Builder 패턴 테스트")
    class Describe_builder {

        @Test
        @DisplayName("[success] Builder로 Request를 생성한다")
        void success() {
            // when
            FindQuestionsRequest request = FindQuestionsRequest.builder()
                .page(0)
                .size(10)
                .build();

            // then
            assertThat(request).isNotNull();
            assertThat(request.getPage()).isEqualTo(0);
            assertThat(request.getSize()).isEqualTo(10);
        }

        @Test
        @DisplayName("[success] null 값으로 Request를 생성한다")
        void success_nullValues() {
            // when
            FindQuestionsRequest request = FindQuestionsRequest.builder()
                .page(null)
                .size(null)
                .build();

            // then
            assertThat(request.getPage()).isNull();
            assertThat(request.getSize()).isNull();
        }
    }

    @Nested
    @DisplayName("[getter] Request getter 메서드 테스트")
    class Describe_getter {

        @Test
        @DisplayName("[success] getPage()로 값을 조회한다")
        void success_getPage() {
            // given
            FindQuestionsRequest request = FindQuestionsRequest.builder()
                .page(1)
                .size(10)
                .build();

            // when
            Integer page = request.getPage();

            // then
            assertThat(page).isEqualTo(1);
        }

        @Test
        @DisplayName("[success] getSize()로 값을 조회한다")
        void success_getSize() {
            // given
            FindQuestionsRequest request = FindQuestionsRequest.builder()
                .page(0)
                .size(20)
                .build();

            // when
            Integer size = request.getSize();

            // then
            assertThat(size).isEqualTo(20);
        }
    }

    @Nested
    @DisplayName("[noArgsConstructor] 기본 생성자 테스트")
    class Describe_noArgsConstructor {

        @Test
        @DisplayName("[success] 기본 생성자로 Request를 생성한다")
        void success() {
            // when
            FindQuestionsRequest request = new FindQuestionsRequest();

            // then
            assertThat(request).isNotNull();
            assertThat(request.getPage()).isNull();
            assertThat(request.getSize()).isNull();
        }
    }

    @Nested
    @DisplayName("[allArgsConstructor] 전체 생성자 테스트")
    class Describe_allArgsConstructor {

        @Test
        @DisplayName("[success] 전체 생성자로 Request를 생성한다")
        void success() {
            // when
            FindQuestionsRequest request = new FindQuestionsRequest(0, 10);

            // then
            assertThat(request).isNotNull();
            assertThat(request.getPage()).isEqualTo(0);
            assertThat(request.getSize()).isEqualTo(10);
        }

        @Test
        @DisplayName("[success] null 값으로 Request를 생성한다")
        void success_withNull() {
            // when
            FindQuestionsRequest request = new FindQuestionsRequest(null, null);

            // then
            assertThat(request.getPage()).isNull();
            assertThat(request.getSize()).isNull();
        }
    }
}
