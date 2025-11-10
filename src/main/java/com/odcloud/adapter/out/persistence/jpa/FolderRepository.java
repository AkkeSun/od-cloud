package com.odcloud.adapter.out.persistence.jpa;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_DoesNotExists_FOLDER;

import com.odcloud.domain.model.Folder;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
@RequiredArgsConstructor
class FolderRepository {

    private final EntityManager entityManager;

    public void save(Folder folder) {
        if (folder.getId() == null) {
            entityManager.persist(FolderEntity.of(folder));
        } else {
            entityManager.merge(FolderEntity.of(folder));
        }
    }

    public Folder findById(Long id) {
        FolderEntity entity = entityManager.find(FolderEntity.class, id);
        if (entity == null) {
            throw new CustomBusinessException(Business_DoesNotExists_FOLDER);
        }
        return Folder.builder()
            .id(entity.getId())
            .parentId(entity.getParentId())
            .groupId(entity.getGroupId())
            .name(entity.getName())
            .owner(entity.getOwner())
            .path(entity.getPath())
            .accessLevel(entity.getAccessLevel())
            .modDt(entity.getModDt())
            .regDt(entity.getRegDt())
            .build();
    }
}
