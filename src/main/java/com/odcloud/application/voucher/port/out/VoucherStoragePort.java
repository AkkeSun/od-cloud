package com.odcloud.application.voucher.port.out;

import com.odcloud.domain.model.Voucher;
import com.odcloud.domain.model.VoucherType;
import java.util.List;
import java.util.Optional;

public interface VoucherStoragePort {

    Voucher save(Voucher voucher);

    void update(Voucher voucher);

    Voucher findById(Long id);

    Optional<Voucher> findForSubscription(Long groupId, VoucherType voucherType, Long accountId);

    List<Voucher> findActiveByAccountIdOrGroupIds(Long accountId, List<Long> groupIds);

}
