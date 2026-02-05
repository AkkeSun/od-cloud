package com.odcloud.adapter.in.scheduler;

import com.odcloud.application.voucher.port.in.ExpireVouchersUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class VoucherScheduler {

    private final ExpireVouchersUseCase expireVouchersUseCase;

    @Scheduled(cron = "0 0 */2 * * *")
    @SchedulerLock(name = "VoucherScheduler_expireVouchers", lockAtLeastFor = "30s", lockAtMostFor = "5m")
    public void expireVouchers() {
        log.info("Starting voucher expiration check");
        int expiredCount = expireVouchersUseCase.expireVouchers();
        log.info("Voucher expiration completed: {} vouchers expired", expiredCount);
    }
}
