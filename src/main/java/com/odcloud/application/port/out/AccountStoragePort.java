package com.odcloud.application.port.out;

import com.odcloud.domain.model.Account;

public interface AccountStoragePort {

    void register(Account account);

    void update(Account account);

    boolean existsByUsername(String username);

    Account findByUsername(String username);

    Account findByUsernameAndPassword(String username, String password);
}
