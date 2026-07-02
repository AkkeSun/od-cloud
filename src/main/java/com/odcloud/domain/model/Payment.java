package com.odcloud.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    private Long id;
    private Long subscriptionId;
    private Long productId;
    private Long groupId;
    private Long buyerId;
    private BigDecimal amount;
    private String status;
    private String billingKey;
    private LocalDateTime modDt;
    private LocalDateTime regDt;

}
