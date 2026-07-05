package com.odcloud.application.subscription.port.out;

import java.math.BigDecimal;

public interface PgClientPort {

    boolean verifyBillingKey(String billingKey);

    boolean pay(String billingKey, BigDecimal amount);
}
