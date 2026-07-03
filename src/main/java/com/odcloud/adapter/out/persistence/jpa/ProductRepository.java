package com.odcloud.adapter.out.persistence.jpa;

import static com.odcloud.adapter.out.persistence.jpa.QProductEntity.productEntity;

import com.odcloud.domain.model.Product;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
class ProductRepository {

    private final JPAQueryFactory queryFactory;

    Optional<Product> findById(Long id) {
        Product product = queryFactory
            .select(Projections.constructor(
                Product.class,
                productEntity.id,
                productEntity.productName,
                productEntity.price,
                productEntity.modDt,
                productEntity.regDt
            ))
            .from(productEntity)
            .where(productEntity.id.eq(id))
            .fetchOne();

        return Optional.ofNullable(product);
    }
}
