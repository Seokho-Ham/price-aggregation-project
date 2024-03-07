package com.assignment.aggregation.exception;

import com.assignment.common.exception.ApplicationErrorCode;
import com.assignment.common.exception.ApplicationException;

public class NoPriceDataException extends ApplicationException {
    public NoPriceDataException() {
        super(ApplicationErrorCode.NO_PRICE_DATA);
    }
}
