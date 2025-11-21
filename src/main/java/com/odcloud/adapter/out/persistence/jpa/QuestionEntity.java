package com.odcloud.adapter.out.persistence.jpa;

import com.odcloud.domain.model.Question;
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
@Table(name = "QUESTION")
class QuestionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "WRITER_EMAIL")
    private String writerEmail;

    @Column(name = "WRITER_NICKNAME")
    private String writerNickname;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "CONTENT", columnDefinition = "TEXT")
    private String content;

    @Column(name = "ANSWERED")
    private Boolean answered;

    @Column(name = "MOD_DT")
    private LocalDateTime modDt;

    @Column(name = "REG_DT")
    private LocalDateTime regDt;

    static QuestionEntity of(Question question) {
        return QuestionEntity.builder()
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

    Question toDomain() {
        return Question.builder()
            .id(id)
            .writerEmail(writerEmail)
            .writerNickname(writerNickname)
            .title(title)
            .content(content)
            .answered(answered)
            .modDt(modDt)
            .regDt(regDt)
            .build();
    }
}
