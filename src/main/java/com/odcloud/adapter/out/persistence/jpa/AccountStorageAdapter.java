package com.odcloud.adapter.out.persistence.jpa;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_NOT_FOUND_ACCOUNT;

import com.odcloud.application.port.out.AccountStoragePort;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.util.AesUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Transactional
@RequiredArgsConstructor
class AccountStorageAdapter implements AccountStoragePort {

    private final AesUtil aesUtil;
    private final AccountRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void register(Account account) {
        repository.save(toEntity(account));
    }

    @Override
    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }

    @Override
    public Account findByUsername(String username) {
        AccountEntity entity = repository.findByUsername(username)
            .orElseThrow(() -> new CustomBusinessException(Business_NOT_FOUND_ACCOUNT));
        return toDomain(entity);
    }

    @Override
    public Account findByUsernameAndPassword(String username, String password) {
        AccountEntity entity = repository.findByUsername(username)
            .orElseThrow(() -> new CustomBusinessException(Business_NOT_FOUND_ACCOUNT));
        if (!passwordEncoder.matches(password, entity.getPassword())) {
            throw new CustomBusinessException(Business_NOT_FOUND_ACCOUNT);
        }
        return toDomain(entity);
    }

    private AccountEntity toEntity(Account account) {
        return AccountEntity.builder()
            .id(account.getId())
            .username(account.getUsername())
            .password(passwordEncoder.encode(account.getPassword()))
            .name(aesUtil.encryptText(account.getName()))
            .email(account.getEmail())
            .role(account.getRole())
            .twoFactorSecret(account.getTwoFactorSecret())
            .isAdminApproved(account.getIsAdminApproved())
            .regDt(account.getRegDt())
            .build();
    }

    private Account toDomain(AccountEntity entity) {
        return Account.builder()
            .id(entity.getId())
            .username(entity.getUsername())
            .password(entity.getPassword())
            .name(aesUtil.decryptText(entity.getName()))
            .email(entity.getEmail())
            .role(entity.getRole())
            .twoFactorSecret(entity.getTwoFactorSecret())
            .isAdminApproved(entity.getIsAdminApproved())
            .regDt(entity.getRegDt())
            .build();
    }
}
