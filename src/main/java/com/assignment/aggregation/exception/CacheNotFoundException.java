package com.assignment.aggregation.exception;

import com.assignment.common.exception.ApplicationErrorCode;
import com.assignment.common.exception.ApplicationException;

public class CacheNotFoundException extends ApplicationException {
    public CacheNotFoundException() {
        super(ApplicationErrorCode.CACHE_NOT_FOUND);
    }
}
