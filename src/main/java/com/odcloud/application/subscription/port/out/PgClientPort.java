package com.odcloud.application.subscription.port.out;

public interface PgClientPort {

    boolean verifyBillingKey(String billingKey);
}
