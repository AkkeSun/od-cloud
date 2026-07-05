package com.odcloud.fakeClass;

import com.odcloud.application.subscription.port.out.PgClientPort;
import java.math.BigDecimal;

public class FakePgClientPort implements PgClientPort {

    public boolean valid = true;
    public boolean paid = true;

    @Override
    public boolean verifyBillingKey(String billingKey) {
        return valid;
    }

    @Override
    public boolean pay(String billingKey, BigDecimal amount) {
        return paid;
    }
}
