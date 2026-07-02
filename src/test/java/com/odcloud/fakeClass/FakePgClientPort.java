package com.odcloud.fakeClass;

import com.odcloud.application.subscription.port.out.PgClientPort;

public class FakePgClientPort implements PgClientPort {

    public boolean valid = true;

    @Override
    public boolean verifyBillingKey(String billingKey) {
        return valid;
    }
}
