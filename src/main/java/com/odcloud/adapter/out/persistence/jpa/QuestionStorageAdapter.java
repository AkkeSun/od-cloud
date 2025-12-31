package com.odcloud.adapter.out.persistence.jpa;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_NOT_FOUND_QUESTION;

import com.odcloud.application.question.port.out.QuestionStoragePort;
import com.odcloud.domain.model.Question;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class QuestionStorageAdapter implements QuestionStoragePort {

    private final QuestionRepository repository;

    @Override
    public void save(Question question) {
        repository.save(question);
    }

    @Override
    public Question findById(Long id) {
        QuestionEntity entity = repository.findById(id);
        if (entity == null) {
            throw new CustomBusinessException(Business_NOT_FOUND_QUESTION);
        }
        return entity.toDomain();
    }

    @Override
    public Page<Question> findAll(int page, int size) {
        return repository.findAll(page, size)
            .map(QuestionEntity::toDomain);
    }
}
