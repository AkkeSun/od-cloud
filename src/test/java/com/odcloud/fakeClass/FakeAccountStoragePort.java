package com.odcloud.fakeClass;

import com.odcloud.application.account.port.out.AccountStoragePort;
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
    public Account save(Account account) {
        if (shouldThrowException) {
            throw new RuntimeException("Storage failure");
        }

        Account savedAccount = Account.builder()
            .id(account.getId() == null ? ++id : account.getId())
            .email(account.getEmail())
            .nickname(account.getNickname())
            .name(account.getName())
            .picture(account.getPicture())
            .groups(account.getGroups())
            .modDt(account.getModDt())
            .regDt(account.getRegDt())
            .build();

        database.removeIf(a -> a.getId().equals(savedAccount.getId()));
        database.add(savedAccount);
        log.info("FakeAccountStoragePort saved: email={}", account.getEmail());
        return savedAccount;
    }

    @Override
    public boolean existsByEmail(String email) {
        boolean exists = database.stream()
            .anyMatch(account -> account.getEmail().equals(email));
        log.info("FakeAccountStoragePort existsByEmail: email={}, exists={}", email, exists);
        return exists;
    }

    @Override
    public Account findByEmail(String email) {
        return database.stream()
            .filter(account -> account.getEmail().equals(email))
            .findFirst()
            .orElseThrow(() -> new CustomBusinessException(ErrorCode.Business_NOT_FOUND_ACCOUNT));
    }

    public Account update(Account account) {
        if (shouldThrowException) {
            throw new RuntimeException("Storage failure");
        }

        Account existingAccount = database.stream()
            .filter(a -> a.getId().equals(account.getId()))
            .findFirst()
            .orElseThrow(() -> new CustomBusinessException(ErrorCode.Business_NOT_FOUND_ACCOUNT));

        Account updatedAccount = Account.builder()
            .id(account.getId())
            .email(account.getEmail())
            .nickname(account.getNickname())
            .name(account.getName())
            .picture(account.getPicture())
            .groups(account.getGroups())
            .modDt(account.getModDt())
            .regDt(account.getRegDt())
            .build();

        database.removeIf(a -> a.getId().equals(account.getId()));
        database.add(updatedAccount);
        log.info("FakeAccountStoragePort updated: email={}", account.getEmail());
        return updatedAccount;
    }
}