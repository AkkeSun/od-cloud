package com.odcloud.fakeClass;

import com.odcloud.application.port.out.AccountStoragePort;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FakeAccountStoragePort implements AccountStoragePort {

    public List<Account> database = new ArrayList<>();
    public Long id = 0L;
    public boolean shouldThrowException = false;

    @Override
    public void register(Account account) {
        if (shouldThrowException) {
            throw new RuntimeException("Storage failure");
        }

        Account savedAccount = Account.builder()
            .id(++id)
            .username(account.getUsername())
            .password(account.getPassword())
            .name(account.getName())
            .email(account.getEmail())
            .role(account.getRole())
            .twoFactorSecret(account.getTwoFactorSecret())
            .isAdminApproved(account.getIsAdminApproved())
            .regDt(account.getRegDt())
            .build();

        database.add(savedAccount);
        log.info("FakeAccountStoragePort registered: username={}", account.getUsername());
    }

    @Override
    public void update(Account account) {
        database = database.stream()
            .filter(a -> !a.getId().equals(account.getId()))
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        database.add(account);
        log.info("FakeAccountStoragePort updated: id={}", account.getId());
    }

    @Override
    public boolean existsByUsername(String username) {
        boolean exists = database.stream()
            .anyMatch(account -> account.getUsername().equals(username));
        log.info("FakeAccountStoragePort existsByUsername: username={}, exists={}", username, exists);
        return exists;
    }

    @Override
    public Account findByUsername(String username) {
        return database.stream()
            .filter(account -> account.getUsername().equals(username))
            .findFirst()
            .orElseThrow(() -> new CustomBusinessException(ErrorCode.Business_NOT_FOUND_ACCOUNT));
    }

    @Override
    public Account findByUsernameAndPassword(String username, String password) {
        return database.stream()
            .filter(account -> account.getUsername().equals(username))
            .filter(account -> account.getPassword().equals(password))
            .findFirst()
            .orElseThrow(() -> new CustomBusinessException(ErrorCode.Business_NOT_FOUND_ACCOUNT));
    }
}