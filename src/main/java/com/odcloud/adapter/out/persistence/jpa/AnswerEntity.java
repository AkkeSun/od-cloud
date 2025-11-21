package com.odcloud.adapter.out.persistence.jpa;

import com.odcloud.domain.model.Answer;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ANSWER")
class AnswerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "QUESTION_ID")
    private Long questionId;

    @Column(name = "WRITER_EMAIL")
    private String writerEmail;

    @Column(name = "WRITER_NICKNAME")
    private String writerNickname;

    @Column(name = "CONTENT", columnDefinition = "TEXT")
    private String content;

    @Column(name = "MOD_DT")
    private LocalDateTime modDt;

    @Column(name = "REG_DT")
    private LocalDateTime regDt;

    static AnswerEntity of(Answer answer) {
        return AnswerEntity.builder()
            .id(answer.getId())
            .questionId(answer.getQuestionId())
            .writerEmail(answer.getWriterEmail())
            .writerNickname(answer.getWriterNickname())
            .content(answer.getContent())
            .modDt(answer.getModDt())
            .regDt(answer.getRegDt())
            .build();
    }

    Answer toDomain() {
        return Answer.builder()
            .id(id)
            .questionId(questionId)
            .writerEmail(writerEmail)
            .writerNickname(writerNickname)
            .content(content)
            .modDt(modDt)
            .regDt(regDt)
            .build();
    }
}
