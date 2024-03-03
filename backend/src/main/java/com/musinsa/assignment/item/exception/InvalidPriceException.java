package com.musinsa.assignment.item.exception;

import com.musinsa.assignment.common.exception.ApplicationErrorCode;
import com.musinsa.assignment.common.exception.ApplicationException;

public class InvalidPriceException extends ApplicationException {

    public InvalidPriceException() {
        super(ApplicationErrorCode.INVALID_PRICE);
    }
}
