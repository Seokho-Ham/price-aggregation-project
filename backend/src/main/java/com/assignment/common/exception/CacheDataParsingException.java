package com.assignment.common.exception;

public class CacheDataParsingException extends ApplicationException {
    public CacheDataParsingException() {
        super(ApplicationErrorCode.CACHE_DATA_PARSING_FAIL);
    }
}
