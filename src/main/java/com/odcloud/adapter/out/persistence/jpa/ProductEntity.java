package com.odcloud.adapter.out.persistence.jpa;

import com.odcloud.domain.model.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product")
class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "mod_dt")
    private LocalDateTime modDt;

    @Column(name = "reg_dt")
    private LocalDateTime regDt;

    static ProductEntity of(Product product) {
        return ProductEntity.builder()
            .id(product.getId())
            .productName(product.getProductName())
            .price(product.getPrice())
            .modDt(product.getModDt())
            .regDt(product.getRegDt())
            .build();
    }

    Product toDomain() {
        return Product.builder()
            .id(id)
            .productName(productName)
            .price(price)
            .modDt(modDt)
            .regDt(regDt)
            .build();
    }
}
