package com.odcloud.adapter.out.persistence.jpa;

import static com.odcloud.adapter.out.persistence.jpa.QApiCallLogEntity.apiCallLogEntity;

import com.odcloud.domain.model.ApiCallLog;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class ApiCallLogRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;

    public ApiCallLog save(ApiCallLog domain) {
        ApiCallLogEntity entity = ApiCallLogEntity.of(domain);
        if (entity.getId() == null) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }
        return entity.toDomain();
    }

    public List<ApiCallLog> findAll() {
        return queryFactory
            .selectFrom(apiCallLogEntity)
            .fetch()
            .stream()
            .map(ApiCallLogEntity::toDomain)
            .collect(Collectors.toList());
    }
}

