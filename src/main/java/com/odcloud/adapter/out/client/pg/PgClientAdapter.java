package com.odcloud.adapter.out.client.pg;

import com.odcloud.application.subscription.port.out.PgClientPort;
import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
class PgClientAdapter implements PgClientPort {

    @Override
    public boolean verifyBillingKey(String billingKey) {
        // TODO: 실제 PG사 빌링키 검증 연동 필요. 현재는 모킹되어 항상 true 를 반환합니다.
        log.info("PgClientAdapter verifyBillingKey - billingKey={}", billingKey);
        return true;
    }

    @Override
    public boolean pay(String billingKey, BigDecimal amount) {
        // TODO: 실제 PG사 빌링키 결제 연동 필요. 현재는 모킹되어 항상 true 를 반환합니다.
        log.info("PgClientAdapter pay - billingKey={}, amount={}", billingKey, amount);
        return true;
    }
}
