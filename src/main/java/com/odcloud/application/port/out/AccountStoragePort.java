package com.odcloud.application.port.out;

import com.odcloud.domain.model.Account;

public interface AccountStoragePort {

    void register(Account account);

    boolean existsByUsername(String username);
}
