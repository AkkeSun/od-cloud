package com.odcloud.adapter.out.persistence.jpa;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_NOT_FOUND_ACCOUNT;

import com.odcloud.application.port.out.AccountStoragePort;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Transactional
@RequiredArgsConstructor
class AccountStorageAdapter implements AccountStoragePort {

    private final AccountRepository repository;

    @Override
    public Account save(Account account) {
        return repository.save(account);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public Account findByEmail(String email) {
        return repository.findByEmail(email).orElseThrow(
            () -> new CustomBusinessException(Business_NOT_FOUND_ACCOUNT));
    }
}
