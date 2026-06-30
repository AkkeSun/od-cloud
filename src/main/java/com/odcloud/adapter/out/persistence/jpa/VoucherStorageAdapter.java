package com.odcloud.adapter.out.persistence.jpa;

import com.odcloud.application.voucher.port.out.VoucherDetail;
import com.odcloud.application.voucher.port.out.VoucherStoragePort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class VoucherStorageAdapter implements VoucherStoragePort {

    private final VoucherRepository repository;

    @Override
    public List<VoucherDetail> findActiveByGroupIds(List<Long> groupIds) {
        return repository.findActiveByGroupIds(groupIds);
    }
}
