package com.odcloud.adapter.out.persistence.jpa;

import com.odcloud.domain.model.Folder;
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
}
