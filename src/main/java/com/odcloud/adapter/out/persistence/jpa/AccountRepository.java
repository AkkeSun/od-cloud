package com.odcloud.adapter.out.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    boolean existsByUsername(String username);
}
