package com.odcloud.adapter.out.persistence.jpa;

import static com.odcloud.adapter.out.persistence.jpa.QFileEntity.fileEntity;

import com.odcloud.application.port.in.command.FindFilesCommand;
import com.odcloud.domain.model.File;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
class FileRepository {

    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;
    private final ConstructorExpression<File> constructor;

    FileRepository(EntityManager entityManager, JPAQueryFactory queryFactory) {
        this.entityManager = entityManager;
        this.queryFactory = queryFactory;
        this.constructor = Projections.constructor(File.class,
            fileEntity.id,
            fileEntity.folderId,
            fileEntity.fileName,
            fileEntity.fileLoc,
            fileEntity.modDt,
            fileEntity.regDt
        );
    }

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
            .select(constructor)
            .from(fileEntity)
            .where(fileEntity.id.eq(id))
            .fetchOne());
    }

    public List<File> findByIds(List<Long> ids) {
        return queryFactory
            .select(constructor)
            .from(fileEntity)
            .where(fileEntity.id.in(ids))
            .fetch();
    }

    public boolean existsByFolderIdAndName(Long folderId, String name) {
        return queryFactory
            .selectOne()
            .from(fileEntity)
            .where(fileEntity.folderId.eq(folderId)
                .and(fileEntity.fileName.eq(name)))
            .fetchOne() != null;
    }

    public List<File> findAll(FindFilesCommand command) {
        String sql = """
            SELECT f.ID, f.FOLDER_ID, f.FILE_NAME, f.FILE_LOC, f.MOD_DT, f.REG_DT
            FROM FILE f
            INNER JOIN FOLDER fo ON f.FOLDER_ID = fo.ID
            WHERE (
                 (fo.GROUP_ID IN (:groupIds) AND fo.ACCESS_LEVEL = 'PUBLIC')
                 OR (fo.OWNER = :email AND fo.ACCESS_LEVEL = 'PRIVATE')
                 ) %s
            ORDER BY
            """ + getSortRule(command.sortType());

        if (command.isFulltextSearch()) {
            sql = String.format(sql,
                "AND MATCH(f.FILE_NAME) AGAINST(:keyword IN BOOLEAN MODE)");
        } else if (command.isLikeSearch()) {
            sql = String.format(sql, "AND f.FILE_NAME LIKE :keyword");
        } else if (command.isRootSearch()) {
            sql = String.format(sql, "AND f.FOLDER_ID IS NULL ");
        } else {
            sql = String.format(sql, "AND f.FOLDER_ID = :folderId ");
        }

        Query query = entityManager.createNativeQuery(sql.toString(), FileEntity.class);
        query.setParameter("groupIds", command.account().getGroupIds());
        query.setParameter("email", command.account().getEmail());

        if (command.isFulltextSearch()) {
            query.setParameter("keyword", command.keyword() + "*");
        } else if (command.isLikeSearch()) {
            query.setParameter("keyword", "%" + command.keyword() + "%");
        } else if (!command.isRootSearch()) {
            query.setParameter("folderId", command.folderId());
        }

        @SuppressWarnings("unchecked")
        List<FileEntity> entities = query.getResultList();

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

    public String getSortRule(String sortType) {
        if (sortType == null) {
            return " f.FILE_NAME ASC";
        }
        return switch (sortType) {
            case "NAME_DESC" -> " f.FILE_NAME DESC";
            case "REG_DT_ASC" -> " f.REG_DT ASC";
            case "REG_DT_DESC" -> " f.REG_DT DESC";
            default -> " f.FILE_NAME ASC";
        };
    }
}