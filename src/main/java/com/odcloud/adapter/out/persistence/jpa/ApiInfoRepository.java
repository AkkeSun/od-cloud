package com.odcloud.adapter.out.persistence.jpa;

import static com.odcloud.adapter.out.persistence.jpa.QApiInfoEntity.apiInfoEntity;

import com.odcloud.domain.model.ApiInfo;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class ApiInfoRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;

    public ApiInfo save(ApiInfo domain) {
        ApiInfoEntity entity = ApiInfoEntity.of(domain);
        if (entity.getId() == null) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }
        return entity.toDomain();
    }

    public List<ApiInfo> findAll() {
        return queryFactory
            .selectFrom(apiInfoEntity)
            .fetch()
            .stream()
            .map(ApiInfoEntity::toDomain)
            .collect(Collectors.toList());
    }
}
