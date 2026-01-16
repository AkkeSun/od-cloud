package com.odcloud.fakeClass;

import com.odcloud.application.voucher.port.out.VoucherStoragePort;
import com.odcloud.domain.model.Voucher;
import com.odcloud.domain.model.VoucherType;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
            .groupId(voucher.getGroupId())
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
    public Optional<Voucher> findForSubscription(Long groupId, VoucherType voucherType,
        Long accountId) {
        return database.stream()
            .filter(v -> v.getGroupId() != null && v.getGroupId().equals(groupId))
            .filter(v -> v.getVoucherType() == voucherType)
            .filter(v -> v.getAccountId().equals(accountId))
            .filter(v -> v.getStatus() == com.odcloud.domain.model.VoucherStatus.ACTIVE)
            .findFirst();
    }

    @Override
    public List<Voucher> findActiveByAccountIdOrGroupIds(Long accountId, List<Long> groupIds) {
        return database.stream()
            .filter(v -> v.getStatus() == com.odcloud.domain.model.VoucherStatus.ACTIVE)
            .filter(v -> (v.getAccountId() != null && v.getAccountId().equals(accountId))
                || (v.getGroupId() != null && groupIds.contains(v.getGroupId())))
            .toList();
    }
}
