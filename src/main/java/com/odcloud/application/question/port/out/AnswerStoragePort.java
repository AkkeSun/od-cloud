package com.odcloud.application.question.port.out;

import com.odcloud.domain.model.Answer;
import java.util.Optional;

public interface AnswerStoragePort {

    void save(Answer answer);

    Optional<Answer> findOneByQuestionId(Long questionId);

    boolean existsByQuestionId(Long questionId);
}
