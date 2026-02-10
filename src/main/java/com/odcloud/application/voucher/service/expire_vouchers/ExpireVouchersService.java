package com.odcloud.application.voucher.service.expire_vouchers;

import com.odcloud.application.voucher.port.in.ExpireVouchersUseCase;
import com.odcloud.application.voucher.port.out.VoucherStoragePort;
import com.odcloud.domain.model.Voucher;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
class ExpireVouchersService implements ExpireVouchersUseCase {

    private final VoucherStoragePort voucherStoragePort;

    @Override
    @Transactional
    public int expireVouchers() {
        List<Voucher> expiredVouchers = voucherStoragePort.findExpiredActiveVouchers();

        for (Voucher voucher : expiredVouchers) {
            voucher.expire();
            voucherStoragePort.update(voucher);
            log.info("Voucher expired: id={}, type={}, accountId={}",
                voucher.getId(), voucher.getVoucherType(), voucher.getAccountId());
            // todo: 스토리지 바우처의 경우 용량 수정
        }

        return expiredVouchers.size();
    }
}
