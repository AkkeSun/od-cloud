package com.odcloud.adapter.out.persistence.jpa;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
class VoucherRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;

}
