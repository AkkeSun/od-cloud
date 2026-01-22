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
@Table(name = "answer")
class AnswerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "question_id")
    private Long questionId;

    @Column(name = "writer_email")
    private String writerEmail;

    @Column(name = "writer_nickname")
    private String writerNickname;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "mod_dt")
    private LocalDateTime modDt;

    @Column(name = "reg_dt")
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
