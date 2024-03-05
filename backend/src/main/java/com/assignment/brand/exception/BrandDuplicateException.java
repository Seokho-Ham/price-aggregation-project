package com.assignment.brand.exception;

import com.assignment.common.exception.ApplicationException;
import com.assignment.common.exception.ApplicationErrorCode;

public class BrandDuplicateException extends ApplicationException {

    public BrandDuplicateException() {
        super(ApplicationErrorCode.BRAND_DUPLICATE);
    }

}
