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
@Table(name = "question")
class QuestionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "writer_email")
    private String writerEmail;

    @Column(name = "writer_nickname")
    private String writerNickname;

    @Column(name = "title")
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "answered")
    private Boolean answered;

    @Column(name = "mod_dt")
    private LocalDateTime modDt;

    @Column(name = "reg_dt")
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
