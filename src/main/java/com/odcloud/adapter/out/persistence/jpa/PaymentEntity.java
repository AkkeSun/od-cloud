package com.odcloud.adapter.out.persistence.jpa;

import com.odcloud.domain.model.PaymentStatus;
import com.odcloud.domain.model.StoreType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "payment")
class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "account_id")
    private Long accountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "store_type")
    private StoreType storeType;

    @Column(name = "subscription_key")
    private String subscriptionKey;

    @Column(name = "order_tx_id")
    private String orderTxId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PaymentStatus status;

    @Column(name = "store_process_dt")
    private LocalDateTime storeProcessDt;

    @Column(name = "reg_dt")
    private LocalDateTime regDt;
}
