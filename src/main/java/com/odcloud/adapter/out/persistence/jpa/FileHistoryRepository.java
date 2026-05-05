package com.odcloud.adapter.out.persistence.jpa;

import static com.odcloud.adapter.out.persistence.jpa.QFileHistoryEntity.fileHistoryEntity;

import com.odcloud.domain.model.FileHistory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
class FileHistoryRepository {

    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;

    FileHistoryRepository(EntityManager entityManager, JPAQueryFactory queryFactory) {
        this.entityManager = entityManager;
        this.queryFactory = queryFactory;
    }

    @Transactional
    public void save(FileHistory history) {
        entityManager.persist(FileHistoryEntity.of(history));
    }

    public List<FileHistory> findByFileId(Long fileId) {
        return queryFactory
            .selectFrom(fileHistoryEntity)
            .where(fileHistoryEntity.fileId.eq(fileId))
            .orderBy(fileHistoryEntity.regDt.desc())
            .fetch()
            .stream()
            .map(this::toModel)
            .toList();
    }

    public List<FileHistory> findByGroupId(Long groupId) {
        return queryFactory
            .selectFrom(fileHistoryEntity)
            .where(fileHistoryEntity.groupId.eq(groupId))
            .orderBy(fileHistoryEntity.regDt.desc())
            .fetch()
            .stream()
            .map(this::toModel)
            .toList();
    }

    @Transactional
    public void deleteByFileId(Long fileId) {
        queryFactory.delete(fileHistoryEntity)
            .where(fileHistoryEntity.fileId.eq(fileId))
            .execute();
    }

    private FileHistory toModel(FileHistoryEntity entity) {
        return FileHistory.builder()
            .id(entity.getId())
            .fileId(entity.getFileId())
            .groupId(entity.getGroupId())
            .actionType(entity.getActionType())
            .actorEmail(entity.getActorEmail())
            .beforeFileName(entity.getBeforeFileName())
            .afterFileName(entity.getAfterFileName())
            .beforeFolderId(entity.getBeforeFolderId())
            .afterFolderId(entity.getAfterFolderId())
            .fileLoc(entity.getFileLoc())
            .fileSize(entity.getFileSize())
            .backupDt(entity.getBackupDt())
            .regDt(entity.getRegDt())
            .build();
    }
}
