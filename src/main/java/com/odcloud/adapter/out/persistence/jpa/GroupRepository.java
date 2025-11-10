package com.odcloud.adapter.out.persistence.jpa;

import static com.odcloud.adapter.out.persistence.jpa.QAccountEntity.accountEntity;
import static com.odcloud.adapter.out.persistence.jpa.QGroupAccountEntity.groupAccountEntity;
import static com.odcloud.adapter.out.persistence.jpa.QGroupEntity.groupEntity;

import com.odcloud.domain.model.Group;
import com.odcloud.domain.model.GroupAccount;
import com.odcloud.infrastructure.util.AesUtil;
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
class GroupRepository {

    private final AesUtil aesUtil;
    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;

    @Transactional
    void save(Group group) {
        if (group.getId() == null) {
            entityManager.persist(GroupEntity.of(group));
        } else {
            entityManager.merge(GroupEntity.of(group));
        }
    }

    @Transactional
    void saveGroupMember(GroupAccount groupAccount) {
        if (groupAccount.getId() == null) {
            entityManager.persist(GroupAccountEntity.of(groupAccount));
        } else {
            entityManager.merge(GroupAccountEntity.of(groupAccount));
        }
    }

    boolean existsById(String id) {
        return queryFactory.selectOne()
            .from(groupEntity)
            .where(groupEntity.id.eq(id))
            .fetchOne() != null;
    }

    Optional<Group> findById(String id) {
        Group group = queryFactory
            .select(Projections.constructor(
                Group.class,
                groupEntity.id,
                groupEntity.ownerEmail,
                groupEntity.description,
                groupEntity.regDt
            ))
            .from(groupEntity)
            .where(groupEntity.id.eq(id))
            .fetchOne();

        if (group == null) {
            return Optional.empty();
        }

        List<GroupAccount> members = queryFactory
            .select(Projections.constructor(
                GroupAccount.class,
                groupAccountEntity.id,
                groupAccountEntity.groupId,
                groupAccountEntity.accountId,
                accountEntity.name,
                accountEntity.nickname,
                accountEntity.email,
                groupAccountEntity.status,
                groupAccountEntity.updateDt,
                groupAccountEntity.regDt
            ))
            .from(groupAccountEntity)
            .innerJoin(accountEntity)
            .on(groupAccountEntity.accountId.eq(accountEntity.id))
            .where(groupAccountEntity.groupId.eq(id))
            .fetch();

        members.forEach(m -> m.updateName(aesUtil.decryptText(m.getName())));
        group.updateGroupMembers(members);

        return Optional.of(group);
    }

    List<Group> findAll() {
        return queryFactory
            .select(Projections.constructor(
                Group.class,
                groupEntity.id,
                groupEntity.ownerEmail,
                groupEntity.description,
                groupEntity.regDt
            ))
            .from(groupEntity)
            .fetch();
    }

    List<GroupAccount> findGroupAccountsByGroupId(String groupId) {
        List<GroupAccount> groupAccounts = queryFactory
            .select(Projections.constructor(
                GroupAccount.class,
                groupAccountEntity.id,
                groupAccountEntity.groupId,
                groupAccountEntity.accountId,
                accountEntity.name,
                accountEntity.nickname,
                accountEntity.email,
                groupAccountEntity.status,
                groupAccountEntity.updateDt,
                groupAccountEntity.regDt
            ))
            .from(groupAccountEntity)
            .innerJoin(accountEntity)
            .on(groupAccountEntity.accountId.eq(accountEntity.id))
            .where(groupAccountEntity.groupId.eq(groupId))
            .orderBy(groupAccountEntity.id.asc())
            .fetch();

        groupAccounts.forEach(ga -> ga.updateName(aesUtil.decryptText(ga.getName())));
        return groupAccounts;
    }
}
