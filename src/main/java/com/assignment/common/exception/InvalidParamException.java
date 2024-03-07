package com.assignment.common.exception;

public class InvalidParamException extends ApplicationException {
    public InvalidParamException() {
        super(ApplicationErrorCode.INVALID_PARAM);
    }
}
