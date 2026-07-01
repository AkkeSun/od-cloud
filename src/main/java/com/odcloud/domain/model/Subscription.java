package com.odcloud.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {

    private Long id;
    private Long productId;
    private Long groupId;
    private Long buyerId;
    private String status;
    private String billingKey;
    private LocalDate nextBillingDate;
    private LocalDateTime modDt;
    private LocalDateTime regDt;

}
