package com.odcloud.adapter.in.find_question;

import com.odcloud.application.service.find_question.FindQuestionServiceResponse;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record FindQuestionResponse(
    QuestionDto question,
    AnswerDto answer
) {

    public static FindQuestionResponse of(FindQuestionServiceResponse response) {
        return FindQuestionResponse.builder()
            .question(QuestionDto.of(response.question()))
            .answer(response.answer() != null ? AnswerDto.of(response.answer()) : null)
            .build();
    }

    @Builder
    public record QuestionDto(
        Long id,
        String writerNickname,
        String title,
        String content,
        Boolean answered,
        LocalDateTime regDt
    ) {

        public static QuestionDto of(FindQuestionServiceResponse.QuestionResponseItem question) {
            return QuestionDto.builder()
                .id(question.id())
                .writerNickname(question.writerNickname())
                .title(question.title())
                .content(question.content())
                .answered(question.answered())
                .regDt(question.regDt())
                .build();
        }
    }

    @Builder
    public record AnswerDto(
        Long id,
        String writerNickname,
        String content,
        LocalDateTime regDt
    ) {

        public static AnswerDto of(FindQuestionServiceResponse.AnswerResponseItem answer) {
            return AnswerDto.builder()
                .id(answer.id())
                .writerNickname(answer.writerNickname())
                .content(answer.content())
                .regDt(answer.regDt())
                .build();
        }
    }
}
