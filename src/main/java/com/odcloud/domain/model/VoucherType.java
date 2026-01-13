package com.odcloud.domain.model;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VoucherType {
    STORAGE_BASIC(30, 100L * 1024 * 1024 * 1024),  // 30일, 100GB
    STORAGE_PLUS(30, 300L * 1024 * 1024 * 1024),   // 30일, 300GB
    STORAGE_50(null, 50L * 1024 * 1024 * 1024),    // 무제한, 50GB
    STORAGE_100(null, 100L * 1024 * 1024 * 1024),  // 무제한, 100GB
    ADVERTISE(30, 0L);                             // 30일, 스토리지 없음

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
        return this == STORAGE_BASIC
            || this == STORAGE_PLUS
            || this == STORAGE_50
            || this == STORAGE_100;
    }

    public boolean isSubscription() {
        return this == STORAGE_BASIC || this == STORAGE_PLUS || this == ADVERTISE;
    }
}
