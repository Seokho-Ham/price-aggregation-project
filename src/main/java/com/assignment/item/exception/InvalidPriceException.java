package com.assignment.item.exception;

import com.assignment.common.exception.ApplicationErrorCode;
import com.assignment.common.exception.ApplicationException;

public class InvalidPriceException extends ApplicationException {

    public InvalidPriceException() {
        super(ApplicationErrorCode.INVALID_PRICE);
    }
}
