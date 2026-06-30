package com.odcloud.fakeClass;

import com.odcloud.application.voucher.port.out.VoucherDetail;
import com.odcloud.application.voucher.port.out.VoucherStoragePort;
import java.util.ArrayList;
import java.util.List;

public class FakeVoucherStoragePort implements VoucherStoragePort {

    public List<VoucherDetail> database = new ArrayList<>();

    @Override
    public List<VoucherDetail> findActiveByGroupIds(List<Long> groupIds) {
        return new ArrayList<>(database);
    }
}
