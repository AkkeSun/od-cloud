package com.odcloud.application.voucher.port.out;

import java.util.List;

public interface VoucherStoragePort {

    List<VoucherDetail> findActiveByGroupIds(List<Long> groupIds);
}
