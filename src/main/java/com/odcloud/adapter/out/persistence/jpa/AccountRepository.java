package com.odcloud.adapter.out.persistence.jpa;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    boolean existsByUsername(String username);

    Optional<AccountEntity> findByUsername(String username);
}
