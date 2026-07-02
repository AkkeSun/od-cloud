package com.odcloud.application.subscription.port.out;

import com.odcloud.domain.model.Product;

public interface ProductStoragePort {

    Product findById(Long id);
}
