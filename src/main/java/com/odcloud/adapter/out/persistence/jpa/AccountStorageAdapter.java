package com.odcloud.adapter.out.persistence.jpa;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_NOT_FOUND_ACCOUNT;

import com.odcloud.application.port.out.AccountStoragePort;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.util.AesUtil;
import jakarta.transaction.Transactional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Transactional
@RequiredArgsConstructor
class AccountStorageAdapter implements AccountStoragePort {

    private final AesUtil aesUtil;
    private final AccountRepository repository;

    @Override
    public Account register(Account account) {
        AccountEntity entity = toEntity(account);
        repository.save(entity);
        return toDomain(entity);
    }

    @Override
    public void update(Account account) {
        repository.save(toEntity(account));
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public Account findByEmail(String email) {
        AccountEntity entity = repository.findByEmail(email)
            .orElseThrow(() -> new CustomBusinessException(Business_NOT_FOUND_ACCOUNT));
        return toDomain(entity);
    }

    @Override
    public Account findById(Long id) {
        AccountEntity entity = repository.findById(id).orElseThrow(
            () -> new CustomBusinessException(Business_NOT_FOUND_ACCOUNT));
        return toDomain(entity);
    }

    private AccountEntity toEntity(Account account) {
        return AccountEntity.builder()
            .id(account.getId())
            .email(account.getEmail())
            .nickname(account.getNickname())
            .name(aesUtil.encryptText(account.getName()))
            .picture(account.getPicture())
            .groups(account.getGroups().stream()
                .map(GroupEntity::of)
                .collect(Collectors.toSet()))
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
            .groups(entity.getGroups().stream()
                .map(GroupEntity::toDomain)
                .collect(Collectors.toSet()))
            .regDt(entity.getRegDt())
            .updateDt(entity.getUpdateDt())
            .build();
    }
}
