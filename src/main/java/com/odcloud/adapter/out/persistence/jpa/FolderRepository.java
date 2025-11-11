package com.odcloud.adapter.out.persistence.jpa;

import static com.odcloud.adapter.out.persistence.jpa.QFolderEntity.folderEntity;

import com.odcloud.domain.model.Folder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
@RequiredArgsConstructor
class FolderRepository {

    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;

    public void save(Folder folder) {
        if (folder.getId() == null) {
            entityManager.persist(FolderEntity.of(folder));
        } else {
            entityManager.merge(FolderEntity.of(folder));
        }
    }


    public Optional<Folder> findById(Long id) {
        return Optional.ofNullable(queryFactory
            .select(Projections.constructor(Folder.class,
                folderEntity.id,
                folderEntity.parentId,
                folderEntity.groupId,
                folderEntity.name,
                folderEntity.owner,
                folderEntity.path,
                folderEntity.accessLevel,
                folderEntity.modDt,
                folderEntity.regDt
            ))
            .from(folderEntity)
            .where(folderEntity.id.eq(id))
            .fetchOne());
    }
}
