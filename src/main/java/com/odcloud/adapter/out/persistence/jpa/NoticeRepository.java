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
        entityManager.persist(entity);
        return entity;
    }

    java.util.Optional<NoticeEntity> findById(Long id) {
        NoticeEntity entity = entityManager.find(NoticeEntity.class, id);
        return java.util.Optional.ofNullable(entity);
    }

    List<NoticeEntity> findByGroupId(Long groupId, int limit) {
        return queryFactory
            .selectFrom(noticeEntity)
            .where(noticeEntity.groupId.eq(groupId))
            .orderBy(noticeEntity.regDt.desc())
            .limit(limit)
            .fetch();
    }

    @Transactional
    void delete(Long id) {
        queryFactory.delete(noticeEntity)
            .where(noticeEntity.id.eq(id))
            .execute();
    }

    @Transactional
    void update(Long id, String title, String content, java.time.LocalDateTime modDt) {
        queryFactory.update(noticeEntity)
            .set(noticeEntity.title, title)
            .set(noticeEntity.content, content)
            .set(noticeEntity.modDt, modDt)
            .where(noticeEntity.id.eq(id))
            .execute();
    }
}
