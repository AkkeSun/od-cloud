package com.odcloud.application.voucher.port.out;

import com.odcloud.domain.model.Voucher;
import java.util.List;

public interface VoucherStoragePort {

    Voucher save(Voucher voucher);

    void update(Voucher voucher);

    Voucher findById(Long id);
    
    Voucher findByPaymentId(Long paymentId);

    List<Voucher> findExpiredActiveVouchers();

    List<Voucher> findActiveByAccountId(Long accountId);
}
