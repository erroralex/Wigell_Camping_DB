package com.nilsson.service.policy;

import java.math.BigDecimal;

public class StandardPricePolicy implements PricePolicy {
    @Override
    public BigDecimal calculatePrice(BigDecimal dailyCost, long days) {
        return dailyCost.multiply(BigDecimal.valueOf(days));
    }
}