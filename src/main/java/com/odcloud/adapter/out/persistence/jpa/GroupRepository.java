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
    Group save(Group group) {
        GroupEntity entity = GroupEntity.of(group);
        if (group.getId() == null) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }
        return entity.toDomain();
    }

    @Transactional
    void saveGroupMember(GroupAccount groupAccount) {
        if (groupAccount.getId() == null) {
            entityManager.persist(GroupAccountEntity.of(groupAccount));
        } else {
            entityManager.merge(GroupAccountEntity.of(groupAccount));
        }
    }

    boolean existsByName(String name) {
        return queryFactory.selectOne()
            .from(groupEntity)
            .where(groupEntity.name.eq(name))
            .fetchOne() != null;
    }

    Optional<Group> findById(String id) {
        Group group = queryFactory
            .select(Projections.constructor(
                Group.class,
                groupEntity.id,
                groupEntity.ownerEmail,
                groupEntity.name,
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
                groupAccountEntity.deniedCause,
                groupAccountEntity.modDt,
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
                groupEntity.name,
                groupEntity.regDt
            ))
            .from(groupEntity)
            .fetch();
    }

    List<Group> findByKeyword(String keyword) {
        return queryFactory
            .select(Projections.constructor(
                Group.class,
                groupEntity.id,
                groupEntity.ownerEmail,
                groupEntity.name,
                groupEntity.regDt
            ))
            .from(groupEntity)
            .where(groupEntity.name.like("%" + keyword + "%"))
            .fetch();
    }

    List<GroupAccount> findGroupAccountsByGroupId(String groupId) {
        List<GroupAccount> groupAccounts = queryFactory
            .select(Projections.constructor(
                GroupAccount.class,
                groupAccountEntity.id,
                groupAccountEntity.groupId,
                groupAccountEntity.accountId,
                groupEntity.name,
                accountEntity.name,
                accountEntity.nickname,
                accountEntity.email,
                groupAccountEntity.status,
                groupAccountEntity.deniedCause,
                groupAccountEntity.modDt,
                groupAccountEntity.regDt
            ))
            .from(groupAccountEntity)
            .innerJoin(accountEntity)
            .on(groupAccountEntity.accountId.eq(accountEntity.id))
            .innerJoin(groupEntity)
            .on(groupAccountEntity.groupId.eq(groupEntity.id))
            .where(groupAccountEntity.groupId.eq(groupId))
            .orderBy(groupAccountEntity.id.asc())
            .fetch();

        groupAccounts.forEach(ga -> ga.updateName(aesUtil.decryptText(ga.getName())));
        return groupAccounts;
    }

    List<GroupAccount> findGroupAccountsByAccountId(Long accountId) {
        List<GroupAccount> groupAccounts = queryFactory
            .select(Projections.constructor(
                GroupAccount.class,
                groupAccountEntity.id,
                groupAccountEntity.groupId,
                groupAccountEntity.accountId,
                groupEntity.name,
                accountEntity.name,
                accountEntity.nickname,
                accountEntity.email,
                groupAccountEntity.status,
                groupAccountEntity.deniedCause,
                groupAccountEntity.modDt,
                groupAccountEntity.regDt
            ))
            .from(groupAccountEntity)
            .innerJoin(accountEntity)
            .on(groupAccountEntity.accountId.eq(accountEntity.id))
            .innerJoin(groupEntity)
            .on(groupAccountEntity.groupId.eq(groupEntity.id))
            .where(groupAccountEntity.accountId.eq(accountId))
            .orderBy(groupAccountEntity.id.desc())
            .fetch();

        groupAccounts.forEach(ga -> ga.updateName(aesUtil.decryptText(ga.getName())));
        return groupAccounts;
    }

    List<GroupAccount> findPendingGroupAccountsByOwnerEmail(String ownerEmail) {
        List<GroupAccount> groupAccounts = queryFactory
            .select(Projections.constructor(
                GroupAccount.class,
                groupAccountEntity.id,
                groupAccountEntity.groupId,
                groupAccountEntity.accountId,
                groupEntity.name,
                accountEntity.name,
                accountEntity.nickname,
                accountEntity.email,
                groupAccountEntity.status,
                groupAccountEntity.deniedCause,
                groupAccountEntity.modDt,
                groupAccountEntity.regDt
            ))
            .from(groupAccountEntity)
            .innerJoin(accountEntity)
            .on(groupAccountEntity.accountId.eq(accountEntity.id))
            .innerJoin(groupEntity)
            .on(groupAccountEntity.groupId.eq(groupEntity.id))
            .where(groupEntity.ownerEmail.eq(ownerEmail)
                .and(groupAccountEntity.status.eq("PENDING")))
            .orderBy(groupAccountEntity.groupId.asc(), groupAccountEntity.regDt.asc())
            .fetch();

        groupAccounts.forEach(ga -> ga.updateName(aesUtil.decryptText(ga.getName())));
        return groupAccounts;
    }

    Optional<GroupAccount> findGroupAccountByGroupIdAndAccountId(String groupId, Long accountId) {
        GroupAccount groupAccount = queryFactory
            .select(Projections.constructor(
                GroupAccount.class,
                groupAccountEntity.id,
                groupAccountEntity.groupId,
                groupAccountEntity.accountId,
                groupEntity.name,
                accountEntity.name,
                accountEntity.nickname,
                accountEntity.email,
                groupAccountEntity.status,
                groupAccountEntity.deniedCause,
                groupAccountEntity.modDt,
                groupAccountEntity.regDt
            ))
            .from(groupAccountEntity)
            .innerJoin(accountEntity)
            .on(groupAccountEntity.accountId.eq(accountEntity.id))
            .innerJoin(groupEntity)
            .on(groupAccountEntity.groupId.eq(groupEntity.id))
            .where(groupAccountEntity.groupId.eq(groupId)
                .and(groupAccountEntity.accountId.eq(accountId)))
            .fetchOne();

        if (groupAccount == null) {
            return Optional.empty();
        }

        groupAccount.updateName(aesUtil.decryptText(groupAccount.getName()));
        return Optional.of(groupAccount);
    }
}
