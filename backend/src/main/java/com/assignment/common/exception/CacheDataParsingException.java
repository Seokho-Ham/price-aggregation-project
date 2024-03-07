package com.assignment.common.exception;

public class CacheDataParsingException extends ApplicationException {
    public CacheDataParsingException() {
        super(ApplicationErrorCode.OBJECT_PARSING_FAIL);
    }
}
