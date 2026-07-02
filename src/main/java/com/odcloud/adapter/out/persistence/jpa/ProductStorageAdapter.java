package com.odcloud.adapter.out.persistence.jpa;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_NOT_FOUND_PRODUCT;

import com.odcloud.application.subscription.port.out.ProductStoragePort;
import com.odcloud.domain.model.Product;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class ProductStorageAdapter implements ProductStoragePort {

    private final ProductRepository repository;

    @Override
    public Product findById(Long id) {
        return repository.findById(id).orElseThrow(
            () -> new CustomBusinessException(Business_NOT_FOUND_PRODUCT));
    }
}
