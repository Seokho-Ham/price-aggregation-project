package com.assignment.brand.exception;

import com.assignment.common.exception.ApplicationException;
import com.assignment.common.exception.ApplicationErrorCode;

public class BrandNotFoundException extends ApplicationException {

    public BrandNotFoundException() {
        super(ApplicationErrorCode.BRAND_NOT_FOUND);
    }

}
