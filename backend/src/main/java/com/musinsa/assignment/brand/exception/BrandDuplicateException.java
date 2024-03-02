package com.musinsa.assignment.brand.exception;

import com.musinsa.assignment.common.exception.ApplicationException;
import com.musinsa.assignment.common.exception.ApplicationErrorCode;

public class BrandDuplicateException extends ApplicationException {

    public BrandDuplicateException() {
        super(ApplicationErrorCode.BRAND_DUPLICATE);
    }

}
