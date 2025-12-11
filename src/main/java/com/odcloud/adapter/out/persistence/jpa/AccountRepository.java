package com.odcloud.adapter.out.persistence.jpa;

import static com.odcloud.adapter.out.persistence.jpa.QAccountEntity.accountEntity;
import static com.odcloud.adapter.out.persistence.jpa.QGroupAccountEntity.groupAccountEntity;
import static com.odcloud.adapter.out.persistence.jpa.QGroupEntity.groupEntity;

import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.Group;
import com.odcloud.infrastructure.util.AesUtil;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
class AccountRepository {

    private final AesUtil aesUtil;
    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;

    @Transactional
    public Account save(Account account) {
        AccountEntity entity = toEntity(account);
        if (entity.getId() == null) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }

        return toDomain(entity);
    }

    public boolean existsByEmail(String email) {
        return queryFactory
            .selectOne()
            .from(accountEntity)
            .where(accountEntity.email.eq(email))
            .fetchFirst() != null;
    }

    public Optional<Account> findByEmail(String email) {
        AccountEntity entity = queryFactory
            .selectFrom(accountEntity)
            .where(accountEntity.email.eq(email))
            .fetchOne();

        if (entity == null) {
            return Optional.empty();
        }

        Account account = toDomain(entity);
        account.updateGroups(queryFactory.select(Projections.constructor(
                Group.class,
                groupEntity.id,
                groupEntity.ownerEmail,
                groupEntity.name,
                groupEntity.regDt))
            .from(groupEntity)
            .innerJoin(groupAccountEntity)
            .on(groupEntity.id.eq(groupAccountEntity.groupId))
            .innerJoin(accountEntity)
            .on(groupAccountEntity.accountId.eq(accountEntity.id))
            .where(accountEntity.email.eq(email)
                .and(groupAccountEntity.status.eq("ACTIVE")))
            .fetch());

        return Optional.of(account);
    }

    private AccountEntity toEntity(Account account) {
        return AccountEntity.builder()
            .id(account.getId())
            .email(account.getEmail())
            .nickname(account.getNickname())
            .name(aesUtil.encryptText(account.getName()))
            .picture(account.getPicture())
            .regDt(account.getRegDt())
            .modDt(account.getModDt())
            .build();
    }

    private Account toDomain(AccountEntity entity) {
        return Account.builder()
            .id(entity.getId())
            .email(entity.getEmail())
            .nickname(entity.getNickname())
            .name(aesUtil.decryptText(entity.getName()))
            .picture(entity.getPicture())
            .regDt(entity.getRegDt())
            .modDt(entity.getModDt())
            .build();
    }
}
