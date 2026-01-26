package com.nilsson.service.policy;

import java.math.BigDecimal;

public class PremiumPricePolicy implements PricePolicy {
    private static final BigDecimal PREMIUM_RATE = new BigDecimal("1.2");

    @Override
    public BigDecimal calculatePrice(BigDecimal dailyCost, long days) {
        BigDecimal standardPrice = dailyCost.multiply(BigDecimal.valueOf(days));
        return standardPrice.multiply(PREMIUM_RATE);
    }
}