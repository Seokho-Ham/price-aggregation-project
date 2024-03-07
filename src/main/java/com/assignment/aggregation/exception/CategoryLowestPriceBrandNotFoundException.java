package com.assignment.aggregation.exception;

import com.assignment.common.exception.ApplicationErrorCode;
import com.assignment.common.exception.ApplicationException;

public class CategoryLowestPriceBrandNotFoundException extends ApplicationException {
    public CategoryLowestPriceBrandNotFoundException() {
        super(ApplicationErrorCode.CATEGORY_LOWEST_PRICE_BRAND_NOT_FOUND);
    }
}
