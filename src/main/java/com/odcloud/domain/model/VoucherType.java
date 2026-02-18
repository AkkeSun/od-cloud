package com.odcloud.domain.model;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VoucherType {
    STORAGE_BASIC(30, 50L * 1024 * 1024 * 1024),  // 30일, 50GB
    STORAGE_PLUS(30, 200L * 1024 * 1024 * 1024),   // 30일, 200GB
    ADVERTISE_30(30, 0L),
    ADVERTISE_90(90, 0L),
    ADVERTISE_365(365, 0L);

    private final Integer durationDays;
    private final Long storageSizeInBytes;

    public LocalDateTime calculateEndDt(LocalDateTime startAt) {
        if (durationDays == null) {
            return null;
        }
        return startAt.plusDays(durationDays);
    }

    public long getStorageIncreaseSize() {
        return storageSizeInBytes != null ? storageSizeInBytes : 0L;
    }

    public boolean isStorageVoucher() {
        return this == STORAGE_BASIC || this == STORAGE_PLUS;
    }
}
