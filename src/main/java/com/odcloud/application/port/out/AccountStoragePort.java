package com.odcloud.application.port.out;

import com.odcloud.domain.model.Account;

public interface AccountStoragePort {

    Account save(Account account);

    boolean existsByEmail(String email);

    Account findByEmail(String email);
}
