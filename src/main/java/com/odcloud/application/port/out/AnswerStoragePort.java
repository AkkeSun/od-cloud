package com.odcloud.application.port.out;

import com.odcloud.domain.model.Answer;
import java.util.Optional;

public interface AnswerStoragePort {

    void save(Answer answer);

    Optional<Answer> findOneByQuestionId(Long questionId);

    boolean existsByQuestionId(Long questionId);
}
