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
@Table(name = "PAYMENT")
class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "ACCOUNT_ID")
    private Long accountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "STORE_TYPE")
    private StoreType storeType;

    @Column(name = "SUBSCRIPTION_KEY")
    private String subscriptionKey;

    @Column(name = "ORDER_TX_ID")
    private String orderTxId;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private PaymentStatus status;

    @Column(name = "STORE_PROCESS_DT")
    private LocalDateTime storeProcessDt;

    @Column(name = "REG_DT")
    private LocalDateTime regDt;
}
