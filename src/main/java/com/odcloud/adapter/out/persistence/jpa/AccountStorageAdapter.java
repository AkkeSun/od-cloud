package com.odcloud.adapter.out.persistence.jpa;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_NOT_FOUND_ACCOUNT;

import com.odcloud.application.port.out.AccountStoragePort;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Transactional
@RequiredArgsConstructor
class AccountStorageAdapter implements AccountStoragePort {

    private final AccountRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void register(Account account) {
        account.updatePassword(passwordEncoder.encode(account.getPassword()));
        repository.save(AccountEntity.of(account));
    }

    @Override
    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }

    @Override
    public Account findByUsernameAndPassword(String username, String password) {
        AccountEntity entity = repository.findByUsername(username)
            .orElseThrow(() -> new CustomBusinessException(Business_NOT_FOUND_ACCOUNT));
        if (!passwordEncoder.matches(password, entity.getPassword())) {
            throw new CustomBusinessException(Business_NOT_FOUND_ACCOUNT);
        }
        return entity.toDomain();
    }
}
