package com.odcloud.domain.service;

import com.odcloud.domain.model.Subscription;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class SubscriptionPlanChangeCalculator {

    public boolean isUpgrade(BigDecimal currentPrice, BigDecimal newPrice) {
        return newPrice.compareTo(currentPrice) > 0;
    }

    public boolean isDowngrade(BigDecimal currentPrice, BigDecimal newPrice) {
        return newPrice.compareTo(currentPrice) < 0;
    }

    public BigDecimal calculateRemainingValue(
        BigDecimal currentPrice,
        Subscription currentSubscription,
        LocalDate today
    ) {
        LocalDate expiredDate = currentSubscription.getExpiredDate();
        LocalDate periodStart = expiredDate.minusMonths(1);

        long totalPeriodDays = ChronoUnit.DAYS.between(periodStart, expiredDate);
        if (totalPeriodDays <= 0) {
            return BigDecimal.ZERO;
        }

        long remainingDays = Math.max(0, ChronoUnit.DAYS.between(today, expiredDate));

        return currentPrice.multiply(BigDecimal.valueOf(remainingDays))
            .divide(BigDecimal.valueOf(totalPeriodDays), 0, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateUpgradeChargeAmount(BigDecimal newPrice, BigDecimal remainingValue) {
        BigDecimal chargeAmount = newPrice.subtract(remainingValue);
        return chargeAmount.max(BigDecimal.ZERO);
    }
}
