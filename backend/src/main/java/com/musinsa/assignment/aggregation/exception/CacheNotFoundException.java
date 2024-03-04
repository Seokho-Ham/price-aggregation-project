package com.musinsa.assignment.aggregation.exception;

import com.musinsa.assignment.common.exception.ApplicationErrorCode;
import com.musinsa.assignment.common.exception.ApplicationException;

public class CacheNotFoundException extends ApplicationException {
    public CacheNotFoundException() {
        super(ApplicationErrorCode.CACHE_NOT_FOUND);
    }
}
