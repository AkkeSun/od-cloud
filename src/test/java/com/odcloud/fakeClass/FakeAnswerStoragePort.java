package com.odcloud.fakeClass;

import com.odcloud.application.question.port.out.AnswerStoragePort;
import com.odcloud.domain.model.Answer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FakeAnswerStoragePort implements AnswerStoragePort {

    public List<Answer> database = new ArrayList<>();
    public Long id = 0L;
    public boolean shouldThrowException = false;

    @Override
    public void save(Answer answer) {
        if (shouldThrowException) {
            throw new RuntimeException("Storage failure");
        }

        Answer savedAnswer = Answer.builder()
            .id(answer.getId() == null ? ++id : answer.getId())
            .questionId(answer.getQuestionId())
            .writerEmail(answer.getWriterEmail())
            .writerNickname(answer.getWriterNickname())
            .content(answer.getContent())
            .modDt(answer.getModDt())
            .regDt(answer.getRegDt())
            .build();

        database.removeIf(a -> a.getId() != null && a.getId().equals(savedAnswer.getId()));
        database.add(savedAnswer);
        log.info("FakeAnswerStoragePort saved: id={}, questionId={}", savedAnswer.getId(),
            answer.getQuestionId());
    }

    @Override
    public Optional<Answer> findOneByQuestionId(Long questionId) {
        return database.stream()
            .filter(answer -> answer.getQuestionId().equals(questionId))
            .findFirst();
    }

    @Override
    public boolean existsByQuestionId(Long questionId) {
        return database.stream()
            .anyMatch(answer -> answer.getQuestionId().equals(questionId));
    }
}
