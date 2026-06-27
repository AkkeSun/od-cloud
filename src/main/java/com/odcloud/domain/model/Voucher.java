package com.odcloud.domain.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Voucher {

    private Long id;
    private Long paymentId;
    private String voucherType;
    private String status;
    private Long groupId;
    private String memo;
    private LocalDateTime startAt;
    private LocalDateTime endDt;
    private LocalDateTime modDt;
    private LocalDateTime regDt;

}
