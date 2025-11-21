package com.odcloud.adapter.out.persistence.jpa;

import static com.odcloud.adapter.out.persistence.jpa.QQuestionEntity.questionEntity;

import com.odcloud.domain.model.Question;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
class QuestionRepository {

    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;

    @Transactional
    public void save(Question question) {
        if (question.getId() == null) {
            entityManager.persist(QuestionEntity.of(question));
        } else {
            entityManager.merge(QuestionEntity.of(question));
        }
    }

    public QuestionEntity findById(Long questionId) {
        return entityManager.find(QuestionEntity.class, questionId);
    }

    public Page<QuestionEntity> findAll(int page, int size) {
        List<QuestionEntity> content = queryFactory
            .selectFrom(questionEntity)
            .orderBy(questionEntity.regDt.desc())
            .offset((long) page * size)
            .limit(size)
            .fetch();

        Long total = queryFactory
            .select(questionEntity.count())
            .from(questionEntity)
            .fetchOne();

        return new PageImpl<>(content, PageRequest.of(page, size), total != null ? total : 0L);
    }
}
