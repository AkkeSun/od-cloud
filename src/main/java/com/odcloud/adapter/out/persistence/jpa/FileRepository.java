package com.odcloud.adapter.out.persistence.jpa;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_DoesNotExists_FILE;

import com.odcloud.domain.model.File;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;
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

    public File findById(Long id) {
        FileEntity entity = entityManager.find(FileEntity.class, id);
        if (entity == null) {
            throw new CustomBusinessException(Business_DoesNotExists_FILE);
        }
        return File.builder()
            .id(entity.getId())
            .folderId(entity.getFolderId())
            .fileName(entity.getFileName())
            .fileLoc(entity.getFileLoc())
            .modDt(entity.getModDt())
            .regDt(entity.getRegDt())
            .build();
    }

    public List<File> findByIds(List<Long> ids) {
        List<FileEntity> entities = entityManager.createQuery(
                "SELECT f FROM FileEntity f WHERE f.id IN :ids", FileEntity.class)
            .setParameter("ids", ids)
            .getResultList();

        return entities.stream()
            .map(entity -> File.builder()
                .id(entity.getId())
                .folderId(entity.getFolderId())
                .fileName(entity.getFileName())
                .fileLoc(entity.getFileLoc())
                .modDt(entity.getModDt())
                .regDt(entity.getRegDt())
                .build())
            .toList();
    }
}
