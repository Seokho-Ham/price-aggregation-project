package com.assignment.aggregation.exception;

import com.assignment.common.exception.ApplicationErrorCode;
import com.assignment.common.exception.ApplicationException;

public class CategoryHighestPriceBrandNotFoundException extends ApplicationException {
    public CategoryHighestPriceBrandNotFoundException() {
        super(ApplicationErrorCode.CATEGORY_HIGHEST_PRICE_BRAND_NOT_FOUND);
    }
}
