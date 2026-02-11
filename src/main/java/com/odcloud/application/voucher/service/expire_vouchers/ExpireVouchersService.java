package com.odcloud.application.voucher.service.expire_vouchers;

import static com.odcloud.infrastructure.constant.CommonConstant.DEFAULT_STORAGE_TOTAL;
import static com.odcloud.infrastructure.constant.CommonConstant.GROUP_LOCK;

import com.odcloud.application.auth.port.out.RedisStoragePort;
import com.odcloud.application.group.port.out.GroupStoragePort;
import com.odcloud.application.voucher.port.in.ExpireVouchersUseCase;
import com.odcloud.application.voucher.port.out.VoucherStoragePort;
import com.odcloud.domain.model.Group;
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
    private final GroupStoragePort groupStoragePort;
    private final RedisStoragePort redisStoragePort;

    @Override
    @Transactional
    public int expireVouchers() {
        List<Voucher> expiredVouchers = voucherStoragePort.findExpiredActiveVouchers();

        for (Voucher voucher : expiredVouchers) {
            voucher.expire();
            voucherStoragePort.update(voucher);
            log.info("Voucher expired: id={}, type={}, accountId={}",
                voucher.getId(), voucher.getVoucherType(), voucher.getAccountId());

            if (voucher.getVoucherType().isStorageVoucher()) {
                for (Group group : groupStoragePort.findByOwnerId(voucher.getAccountId())) {
                    redisStoragePort.executeWithLock(GROUP_LOCK + group.getId(), () -> {
                        Group lockedGroup = groupStoragePort.findById(group.getId());
                        lockedGroup.updateStorageTotal(DEFAULT_STORAGE_TOTAL);
                        groupStoragePort.updateStorageTotal(lockedGroup);
                        return null;
                    });
                }
            }
        }

        return expiredVouchers.size();
    }
}
