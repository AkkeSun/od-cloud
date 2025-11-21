package com.odcloud.adapter.out.persistence.jpa;

import static com.odcloud.adapter.out.persistence.jpa.QAnswerEntity.answerEntity;

import com.odcloud.domain.model.Answer;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
class AnswerRepository {

    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;

    @Transactional
    public void save(Answer answer) {
        entityManager.persist(AnswerEntity.of(answer));
    }

    public AnswerEntity findOneByQuestionId(Long questionId) {
        return queryFactory
            .selectFrom(answerEntity)
            .where(answerEntity.questionId.eq(questionId))
            .fetchFirst();
    }

    public boolean existsByQuestionId(Long questionId) {
        Integer fetchOne = queryFactory
            .selectOne()
            .from(answerEntity)
            .where(answerEntity.questionId.eq(questionId))
            .fetchFirst();
        return fetchOne != null;
    }
}
