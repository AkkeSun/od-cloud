package com.odcloud.application.question.service.find_question;

import com.odcloud.domain.model.Answer;
import com.odcloud.domain.model.Question;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record FindQuestionServiceResponse(
    QuestionResponseItem question,
    AnswerResponseItem answer
) {

    public static FindQuestionServiceResponse of(Question question, Answer answer) {
        return FindQuestionServiceResponse.builder()
            .question(QuestionResponseItem.of(question))
            .answer(answer != null ? AnswerResponseItem.of(answer) : null)
            .build();
    }

    @Builder
    public record QuestionResponseItem(
        Long id,
        String writerEmail,
        String writerNickname,
        String title,
        String content,
        Boolean answered,
        LocalDateTime modDt,
        LocalDateTime regDt
    ) {

        public static QuestionResponseItem of(Question question) {
            return QuestionResponseItem.builder()
                .id(question.getId())
                .writerEmail(question.getWriterEmail())
                .writerNickname(question.getWriterNickname())
                .title(question.getTitle())
                .content(question.getContent())
                .answered(question.getAnswered())
                .modDt(question.getModDt())
                .regDt(question.getRegDt())
                .build();
        }
    }

    @Builder
    public record AnswerResponseItem(
        Long id,
        Long questionId,
        String writerEmail,
        String writerNickname,
        String content,
        LocalDateTime modDt,
        LocalDateTime regDt
    ) {

        public static AnswerResponseItem of(Answer answer) {
            return AnswerResponseItem.builder()
                .id(answer.getId())
                .questionId(answer.getQuestionId())
                .writerEmail(answer.getWriterEmail())
                .writerNickname(answer.getWriterNickname())
                .content(answer.getContent())
                .modDt(answer.getModDt())
                .regDt(answer.getRegDt())
                .build();
        }
    }
}
