package com.assignment.aggregation.exception;

import com.assignment.common.exception.ApplicationErrorCode;
import com.assignment.common.exception.ApplicationException;

public class LowestTotalPriceBrandNotFoundException extends ApplicationException {
    public LowestTotalPriceBrandNotFoundException() {
        super(ApplicationErrorCode.LOWEST_TOTAL_PRICE_BRAND_NOT_FOUND);
    }
}
