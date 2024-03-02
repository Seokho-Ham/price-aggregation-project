package com.musinsa.assignment.brand.exception;

import com.musinsa.assignment.common.exception.ApplicationException;
import com.musinsa.assignment.common.exception.ApplicationErrorCode;

public class BrandNotFoundException extends ApplicationException {

    public BrandNotFoundException() {
        super(ApplicationErrorCode.BRAND_NOT_FOUND);
    }

}
