package com.odcloud.adapter.out.persistence.jpa;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_NOT_FOUND_VOUCHER;

import com.odcloud.application.voucher.port.out.VoucherStoragePort;
import com.odcloud.domain.model.Voucher;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Transactional
@RequiredArgsConstructor
class VoucherStorageAdapter implements VoucherStoragePort {

    private final VoucherRepository repository;

    @Override
    public Voucher save(Voucher voucher) {
        return repository.save(voucher);
    }

    @Override
    public void update(Voucher voucher) {
        repository.update(voucher);
    }

    @Override
    public Voucher findById(Long id) {
        return repository.findById(id).orElseThrow(
            () -> new CustomBusinessException(Business_NOT_FOUND_VOUCHER));
    }

    @Override
    public List<Voucher> findActiveByAccountIdOrGroupIds(Long accountId, List<Long> groupIds) {
        return repository.findActiveByAccountIdOrGroupIds(accountId, groupIds);
    }

    @Override
    public Voucher findByPaymentId(Long paymentId) {
        return repository.findByPaymentId(paymentId).orElseThrow(
            () -> new CustomBusinessException(Business_NOT_FOUND_VOUCHER));
    }

    @Override
    public List<Voucher> findExpiredActiveVouchers() {
        return repository.findExpiredActiveVouchers();
    }
}
