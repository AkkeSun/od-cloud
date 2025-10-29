package com.odcloud.adapter.out.persistence.jpa;

import com.odcloud.application.port.out.AccountStoragePort;
import com.odcloud.domain.model.Account;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Transactional
@RequiredArgsConstructor
class AccountStorageAdapter implements AccountStoragePort {

    private final AccountRepository repository;

    @Override
    public void register(Account account) {
        repository.save(AccountEntity.of(account));
    }

    @Override
    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }
}
