package com.odcloud.adapter.out.persistence.jpa;

import com.odcloud.application.port.out.AnswerStoragePort;
import com.odcloud.domain.model.Answer;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class AnswerStorageAdapter implements AnswerStoragePort {

    private final AnswerRepository repository;

    @Override
    public void save(Answer answer) {
        repository.save(answer);
    }

    @Override
    public Optional<Answer> findOneByQuestionId(Long questionId) {
        return Optional.ofNullable(repository.findOneByQuestionId(questionId))
            .map(AnswerEntity::toDomain);
    }

    @Override
    public boolean existsByQuestionId(Long questionId) {
        return repository.existsByQuestionId(questionId);
    }
}
