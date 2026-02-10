package com.odcloud.fakeClass;

import com.odcloud.application.voucher.port.out.VoucherStoragePort;
import com.odcloud.domain.model.Voucher;
import com.odcloud.domain.model.VoucherStatus;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FakeVoucherStoragePort implements VoucherStoragePort {

    public List<Voucher> database = new ArrayList<>();

    @Override
    public Voucher save(Voucher voucher) {
        Voucher savedVoucher = Voucher.builder()
            .id(voucher.getId())
            .paymentId(voucher.getPaymentId())
            .voucherType(voucher.getVoucherType())
            .status(voucher.getStatus())
            .accountId(voucher.getAccountId())
            .memo(voucher.getMemo())
            .startAt(voucher.getStartAt())
            .endDt(voucher.getEndDt())
            .modDt(voucher.getModDt())
            .regDt(voucher.getRegDt())
            .build();

        // Only remove existing voucher if id is not null
        if (savedVoucher.getId() != null) {
            database.removeIf(v -> v.getId() != null && v.getId().equals(savedVoucher.getId()));
        }
        database.add(savedVoucher);
        log.info("FakeVoucherStoragePort saved: id={}, voucherType={}", savedVoucher.getId(),
            voucher.getVoucherType());
        return savedVoucher;
    }

    @Override
    public void update(Voucher voucher) {
        database.removeIf(v -> v.getId() != null && v.getId().equals(voucher.getId()));
        database.add(voucher);
        log.info("FakeVoucherStoragePort updated: id={}, voucherType={}", voucher.getId(),
            voucher.getVoucherType());
    }

    @Override
    public Voucher findById(Long id) {
        return database.stream()
            .filter(voucher -> voucher.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new CustomBusinessException(ErrorCode.Business_NOT_FOUND_VOUCHER));
    }

    @Override
    public Voucher findByPaymentId(Long paymentId) {
        return database.stream()
            .filter(v -> v.getPaymentId() != null && v.getPaymentId().equals(paymentId))
            .findFirst()
            .orElseThrow(() -> new CustomBusinessException(ErrorCode.Business_NOT_FOUND_VOUCHER));
    }

    @Override
    public List<Voucher> findExpiredActiveVouchers() {
        LocalDateTime now = LocalDateTime.now();
        return database.stream()
            .filter(v -> v.getEndDt() != null && v.getEndDt().isBefore(now))
            .filter(v -> v.getStatus() != VoucherStatus.EXPIRED)
            .toList();
    }

    @Override
    public List<Voucher> findActiveByAccountId(Long accountId) {
        return List.of();
    }
}
