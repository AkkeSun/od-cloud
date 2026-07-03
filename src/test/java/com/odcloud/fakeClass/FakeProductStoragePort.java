package com.odcloud.fakeClass;

import com.odcloud.application.subscription.port.out.ProductStoragePort;
import com.odcloud.domain.model.Product;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import java.util.ArrayList;
import java.util.List;

public class FakeProductStoragePort implements ProductStoragePort {

    public List<Product> database = new ArrayList<>();

    @Override
    public Product findById(Long id) {
        return database.stream()
            .filter(product -> product.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new CustomBusinessException(ErrorCode.Business_NOT_FOUND_PRODUCT));
    }
}
