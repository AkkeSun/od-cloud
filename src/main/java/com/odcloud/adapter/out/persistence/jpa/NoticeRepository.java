package com.odcloud.adapter.out.persistence.jpa;

import static com.odcloud.adapter.out.persistence.jpa.QNoticeEntity.noticeEntity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
class NoticeRepository {

    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;

    @Transactional
    NoticeEntity save(NoticeEntity entity) {
        if (entity.getId() == null) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }
        return entity;
    }

    List<NoticeEntity> findByGroupId(String groupId, int limit) {
        return queryFactory
            .selectFrom(noticeEntity)
            .where(noticeEntity.groupId.eq(groupId))
            .orderBy(noticeEntity.regDt.desc())
            .limit(limit)
            .fetch();
    }
}
