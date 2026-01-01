package com.odcloud.adapter.out.persistence.jpa;

import static com.odcloud.adapter.out.persistence.jpa.QFileInfoEntity.fileInfoEntity;

import com.odcloud.application.file.port.in.command.FindFilesCommand;
import com.odcloud.domain.model.FileInfo;
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
class FileInfoRepository {

    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;
    private final ConstructorExpression<FileInfo> constructor;

    FileInfoRepository(EntityManager entityManager, JPAQueryFactory queryFactory) {
        this.entityManager = entityManager;
        this.queryFactory = queryFactory;
        this.constructor = Projections.constructor(FileInfo.class,
            fileInfoEntity.id,
            fileInfoEntity.folderId,
            fileInfoEntity.fileName,
            fileInfoEntity.fileLoc,
            fileInfoEntity.fileSize,
            fileInfoEntity.modDt,
            fileInfoEntity.regDt
        );
    }

    @Transactional
    public void save(FileInfo file) {
        if (file.getId() == null) {
            entityManager.persist(FileInfoEntity.of(file));
        } else {
            entityManager.merge(FileInfoEntity.of(file));
        }
    }

    public Optional<FileInfo> findById(Long id) {
        return Optional.ofNullable(queryFactory
            .select(constructor)
            .from(fileInfoEntity)
            .where(fileInfoEntity.id.eq(id))
            .fetchOne());
    }

    public List<FileInfo> findByIds(List<Long> ids) {
        return queryFactory
            .select(constructor)
            .from(fileInfoEntity)
            .where(fileInfoEntity.id.in(ids))
            .fetch();
    }

    public boolean existsByFolderIdAndName(Long folderId, String name) {
        return queryFactory
            .selectOne()
            .from(fileInfoEntity)
            .where(fileInfoEntity.folderId.eq(folderId)
                .and(fileInfoEntity.fileName.eq(name)))
            .fetchOne() != null;
    }

    public List<FileInfo> findByFolderId(Long folderId) {
        return queryFactory
            .select(constructor)
            .from(fileInfoEntity)
            .where(fileInfoEntity.folderId.eq(folderId))
            .fetch();
    }

    public List<FileInfo> findAll(FindFilesCommand command) {
        String sql = """
            SELECT f.ID, f.FOLDER_ID, f.FILE_NAME, f.FILE_LOC, f.FILE_SIZE, f.MOD_DT, f.REG_DT
            FROM FILE_INFO f
            INNER JOIN FOLDER_INFO fo ON f.FOLDER_ID = fo.ID
            WHERE fo.GROUP_ID IN (:groupIds)
                  %s
            ORDER BY
            """ + getSortRule(command.sortType());

        boolean isH2 = isH2Database();

        if (command.isFulltextSearch() && !isH2) {
            sql = String.format(sql,
                "AND MATCH(f.FILE_NAME) AGAINST(:keyword IN BOOLEAN MODE)");
        } else if (command.isFulltextSearch() && isH2) {
            sql = String.format(sql, "AND f.FILE_NAME LIKE :keyword");
        } else if (command.isLikeSearch()) {
            sql = String.format(sql, "AND f.FILE_NAME LIKE :keyword");
        } else if (command.isRootSearch()) {
            sql = String.format(sql, "AND f.FOLDER_ID IS NULL ");
        } else {
            sql = String.format(sql, "AND f.FOLDER_ID = :folderId ");
        }

        Query query = entityManager.createNativeQuery(sql.toString(), FileInfoEntity.class);
        query.setParameter("groupIds", command.account().getGroupIds());

        if (command.isFulltextSearch() && !isH2) {
            query.setParameter("keyword", command.keyword() + "*");
        } else if ((command.isFulltextSearch() && isH2) || command.isLikeSearch()) {
            query.setParameter("keyword", "%" + command.keyword() + "%");
        } else if (!command.isRootSearch()) {
            query.setParameter("folderId", command.folderId());
        }

        @SuppressWarnings("unchecked")
        List<FileInfoEntity> entities = query.getResultList();

        return entities.stream()
            .map(entity -> FileInfo.builder()
                .id(entity.getId())
                .folderId(entity.getFolderId())
                .fileName(entity.getFileName())
                .fileLoc(entity.getFileLoc())
                .fileSize(entity.getFileSize())
                .modDt(entity.getModDt())
                .regDt(entity.getRegDt())
                .build())
            .toList();
    }

    private boolean isH2Database() {
        try {
            java.sql.Connection connection = entityManager.unwrap(org.hibernate.Session.class)
                .doReturningWork(conn -> conn);
            String databaseProductName = connection.getMetaData().getDatabaseProductName();
            return databaseProductName.toLowerCase().contains("h2");
        } catch (Exception e) {
            return false;
        }
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

    @Transactional
    public void delete(FileInfo file) {
        queryFactory.delete(fileInfoEntity)
            .where(fileInfoEntity.id.eq(file.getId()))
            .execute();
    }
}