package com.odcloud.adapter.out.persistence.jpa;

import static com.odcloud.adapter.out.persistence.jpa.QAccountEntity.accountEntity;

import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.util.AesUtil;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class AccountRepository {

    private final AesUtil aesUtil;
    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;

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
        AccountEntity result = queryFactory
            .selectFrom(accountEntity)
            .where(accountEntity.email.eq(email))
            .fetchOne();
        return result == null ? Optional.empty() : Optional.of(toDomain(result));
    }

    public Optional<Account> findById(Long id) {
        AccountEntity result = queryFactory
            .selectFrom(accountEntity)
            .where(accountEntity.id.eq(id))
            .fetchOne();
        return result == null ? Optional.empty() : Optional.of(toDomain(result));
    }

    private AccountEntity toEntity(Account account) {
        return AccountEntity.builder()
            .id(account.getId())
            .email(account.getEmail())
            .nickname(account.getNickname())
            .name(aesUtil.encryptText(account.getName()))
            .picture(account.getPicture())
            .regDt(account.getRegDt())
            .updateDt(account.getUpdateDt())
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
            .updateDt(entity.getUpdateDt())
            .build();
    }
}
