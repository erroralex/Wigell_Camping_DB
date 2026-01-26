package com.nilsson.service.policy;

import java.math.BigDecimal;

public class StudentPricePolicy implements PricePolicy {
    private static final BigDecimal STUDENT_DISCOUNT = new BigDecimal("0.8");

    @Override
    public BigDecimal calculatePrice(BigDecimal dailyCost, long days) {
        BigDecimal standardPrice = dailyCost.multiply(BigDecimal.valueOf(days));
        return standardPrice.multiply(STUDENT_DISCOUNT);
    }
}