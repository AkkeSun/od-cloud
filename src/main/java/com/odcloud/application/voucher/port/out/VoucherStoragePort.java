package com.odcloud.application.voucher.port.out;

import com.odcloud.domain.model.Voucher;
import java.util.List;

public interface VoucherStoragePort {

    Voucher save(Voucher voucher);

    void update(Voucher voucher);

    Voucher findById(Long id);

    List<Voucher> findActiveByAccountIdOrGroupIds(Long accountId, List<Long> groupIds);

    Voucher findByPaymentId(Long paymentId);

    List<Voucher> findExpiredActiveVouchers();
}
