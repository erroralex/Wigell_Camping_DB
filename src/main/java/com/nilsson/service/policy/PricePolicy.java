package com.nilsson.service.policy;

import java.math.BigDecimal;

public interface PricePolicy {

    BigDecimal calculatePrice(BigDecimal dailyCost, long days);

}