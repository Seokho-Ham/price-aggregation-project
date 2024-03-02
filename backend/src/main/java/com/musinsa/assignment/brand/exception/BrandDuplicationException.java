package com.musinsa.assignment.brand.exception;

import com.musinsa.assignment.common.exception.ApplicationException;
import com.musinsa.assignment.common.exception.ApplicationErrorCode;

public class BrandDuplicationException extends ApplicationException {

    public BrandDuplicationException() {
        super(ApplicationErrorCode.BRAND_DUPLICATION);
    }

}
