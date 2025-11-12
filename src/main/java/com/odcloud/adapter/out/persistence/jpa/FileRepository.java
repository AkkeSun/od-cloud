package com.odcloud.adapter.out.persistence.jpa;

import com.odcloud.domain.model.File;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
@RequiredArgsConstructor
class FileRepository {

    private final EntityManager entityManager;

    public void save(File file) {
        if (file.getId() == null) {
            entityManager.persist(FileEntity.of(file));
        } else {
            entityManager.merge(FileEntity.of(file));
        }
    }
}