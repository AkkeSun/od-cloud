package com.odcloud.adapter.out.persistence.jpa;

import static com.odcloud.adapter.out.persistence.jpa.QFolderEntity.folderEntity;

import com.odcloud.application.port.in.command.FindFilesCommand;
import com.odcloud.domain.model.Folder;
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
class FolderRepository {

    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;
    private final ConstructorExpression<Folder> constructor;

    FolderRepository(EntityManager entityManager, JPAQueryFactory queryFactory) {
        this.entityManager = entityManager;
        this.queryFactory = queryFactory;
        this.constructor = Projections.constructor(Folder.class,
            folderEntity.id,
            folderEntity.parentId,
            folderEntity.groupId,
            folderEntity.name,
            folderEntity.owner,
            folderEntity.path,
            folderEntity.accessLevel,
            folderEntity.modDt,
            folderEntity.regDt
        );
    }

    @Transactional
    void save(Folder folder) {
        if (folder.getId() == null) {
            entityManager.persist(FolderEntity.of(folder));
        } else {
            entityManager.merge(FolderEntity.of(folder));
        }
    }

    Optional<Folder> findById(Long id) {
        return Optional.ofNullable(queryFactory
            .select(constructor)
            .from(folderEntity)
            .where(folderEntity.id.eq(id))
            .fetchOne());
    }

    public List<Folder> findAll(FindFilesCommand command) {
        String sql = """
            SELECT ID, PARENT_ID, GROUP_ID, NAME, PATH, OWNER, ACCESS_LEVEL, REG_DT, MOD_DT
            FROM FOLDER
            WHERE (
                 (GROUP_ID IN (:groupIds) AND ACCESS_LEVEL = 'PUBLIC')
                 OR (OWNER = :email AND ACCESS_LEVEL = 'PRIVATE')
                 ) %s
            ORDER BY
            """ + getSortRule(command.sortType());

        if (command.isFulltextSearch()) {
            sql = String.format(sql,
                "AND MATCH(NAME) AGAINST(:keyword IN BOOLEAN MODE)");
        } else if (command.isLikeSearch()) {
            sql = String.format(sql, "AND NAME LIKE :keyword");
        } else if (command.isRootSearch()) {
            sql = String.format(sql, "AND PARENT_ID IS NULL ");
        } else {
            sql = String.format(sql, "AND PARENT_ID = :folderId ");
        }

        Query query = entityManager.createNativeQuery(sql.toString(), FolderEntity.class);
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
        List<FolderEntity> entities = query.getResultList();

        return entities.stream()
            .map(entity -> Folder.builder()
                .id(entity.getId())
                .parentId(entity.getParentId())
                .groupId(entity.getGroupId())
                .name(entity.getName())
                .accessLevel(entity.getAccessLevel())
                .regDt(entity.getRegDt())
                .build())
            .toList();
    }

    public String getSortRule(String sortType) {
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
            .from(folderEntity)
            .where(folderEntity.parentId.eq(parentId).and(folderEntity.name.eq(name)))
            .fetchOne() != null;
    }
}
