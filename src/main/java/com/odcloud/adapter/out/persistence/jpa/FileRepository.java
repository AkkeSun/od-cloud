package com.odcloud.adapter.out.persistence.jpa;

import static com.odcloud.adapter.out.persistence.jpa.QFileEntity.fileEntity;

import com.odcloud.domain.model.File;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
class FileRepository {

    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;

    @Transactional
    public void save(File file) {
        if (file.getId() == null) {
            entityManager.persist(FileEntity.of(file));
        } else {
            entityManager.merge(FileEntity.of(file));
        }
    }

    public Optional<File> findById(Long id) {
        return Optional.ofNullable(queryFactory
            .select(Projections.constructor(File.class,
                fileEntity.id,
                fileEntity.folderId,
                fileEntity.fileName,
                fileEntity.fileLoc,
                fileEntity.modDt,
                fileEntity.regDt
            ))
            .from(fileEntity)
            .where(fileEntity.id.eq(id))
            .fetchOne());
    }

    public List<File> findByIds(List<Long> ids) {
        return queryFactory
            .select(Projections.constructor(File.class,
                fileEntity.id,
                fileEntity.folderId,
                fileEntity.fileName,
                fileEntity.fileLoc,
                fileEntity.modDt,
                fileEntity.regDt
            ))
            .from(fileEntity)
            .where(fileEntity.id.in(ids))
            .fetch();
    }

    public List<File> findByFolderId(Long folderId) {
        return queryFactory
            .select(Projections.constructor(File.class,
                fileEntity.id,
                fileEntity.folderId,
                fileEntity.fileName,
                fileEntity.fileLoc,
                fileEntity.modDt,
                fileEntity.regDt
            ))
            .from(fileEntity)
            .where(fileEntity.folderId.eq(folderId))
            .fetch();
    }
}