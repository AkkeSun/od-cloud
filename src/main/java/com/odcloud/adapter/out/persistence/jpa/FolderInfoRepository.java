package com.odcloud.adapter.out.persistence.jpa;

import static com.odcloud.adapter.out.persistence.jpa.QFolderInfoEntity.folderInfoEntity;

import com.odcloud.application.file.port.in.command.FindFilesCommand;
import com.odcloud.domain.model.FolderInfo;
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
class FolderInfoRepository {

    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;
    private final ConstructorExpression<FolderInfo> constructor;

    FolderInfoRepository(EntityManager entityManager, JPAQueryFactory queryFactory) {
        this.entityManager = entityManager;
        this.queryFactory = queryFactory;
        this.constructor = Projections.constructor(FolderInfo.class,
            folderInfoEntity.id,
            folderInfoEntity.parentId,
            folderInfoEntity.groupId,
            folderInfoEntity.name,
            folderInfoEntity.owner,
            folderInfoEntity.path,
            folderInfoEntity.modDt,
            folderInfoEntity.regDt
        );
    }

    @Transactional
    void save(FolderInfo folder) {
        if (folder.getId() == null) {
            entityManager.persist(FolderInfoEntity.of(folder));
        } else {
            entityManager.merge(FolderInfoEntity.of(folder));
        }
    }

    Optional<FolderInfo> findById(Long id) {
        return Optional.ofNullable(queryFactory
            .select(constructor)
            .from(folderInfoEntity)
            .where(folderInfoEntity.id.eq(id))
            .fetchOne());
    }

    public List<FolderInfo> findAll(FindFilesCommand command) {
        String sql = """
            SELECT ID, PARENT_ID, GROUP_ID, NAME, PATH, OWNER, REG_DT, MOD_DT
            FROM FOLDER_INFO
            WHERE GROUP_ID IN (:groupIds)
                  %s
            ORDER BY
            """ + getSortRule(command.sortType());

        boolean isH2 = isH2Database();

        if (command.isFulltextSearch() && !isH2) {
            sql = String.format(sql,
                "AND MATCH(NAME) AGAINST(:keyword IN BOOLEAN MODE)");
        } else if (command.isFulltextSearch() && isH2) {
            sql = String.format(sql, "AND NAME LIKE :keyword");
        } else if (command.isLikeSearch()) {
            sql = String.format(sql, "AND NAME LIKE :keyword");
        } else if (command.isRootSearch()) {
            sql = String.format(sql, "AND PARENT_ID IS NULL ");
        } else {
            sql = String.format(sql, "AND PARENT_ID = :folderId ");
        }

        Query query = entityManager.createNativeQuery(sql.toString(), FolderInfoEntity.class);
        query.setParameter("groupIds", command.account().getGroupIds());

        if (command.isFulltextSearch() && !isH2) {
            query.setParameter("keyword", command.keyword() + "*");
        } else if ((command.isFulltextSearch() && isH2) || command.isLikeSearch()) {
            query.setParameter("keyword", "%" + command.keyword() + "%");
        } else if (!command.isRootSearch()) {
            query.setParameter("folderId", command.folderId());
        }

        @SuppressWarnings("unchecked")
        List<FolderInfoEntity> entities = query.getResultList();

        return entities.stream()
            .map(entity -> FolderInfo.builder()
                .id(entity.getId())
                .parentId(entity.getParentId())
                .groupId(entity.getGroupId())
                .name(entity.getName())
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

    private String getSortRule(String sortType) {
        if (sortType == null) {
            return " NAME ASC";
        }
        return switch (sortType) {
            case "NAME_DESC" -> " NAME DESC";
            case "REG_DT_ASC" -> " REG_DT ASC";
            case "REG_DT_DESC" -> " REG_DT DESC";
            default -> " NAME ASC";
        };
    }

    boolean existsSameFolderName(Long parentId, String name) {
        return queryFactory.selectOne()
            .from(folderInfoEntity)
            .where(folderInfoEntity.parentId.eq(parentId).and(folderInfoEntity.name.eq(name)))
            .fetchOne() != null;
    }

    List<FolderInfo> findByParentId(Long parentId) {
        return queryFactory
            .select(constructor)
            .from(folderInfoEntity)
            .where(folderInfoEntity.parentId.eq(parentId))
            .fetch();
    }

    @Transactional
    void delete(FolderInfo folder) {
        queryFactory.delete(folderInfoEntity)
            .where(folderInfoEntity.id.eq(folder.getId()))
            .execute();
    }

    Optional<FolderInfo> findRootFolderByGroupId(String groupId) {
        return Optional.ofNullable(queryFactory
            .select(constructor)
            .from(folderInfoEntity)
            .where(folderInfoEntity.groupId.eq(groupId)
                .and(folderInfoEntity.parentId.isNull()))
            .fetchOne());
    }

    List<FolderInfo> findByGroupId(String groupId) {
        return queryFactory
            .select(constructor)
            .from(folderInfoEntity)
            .where(folderInfoEntity.groupId.eq(groupId))
            .fetch();
    }
}
