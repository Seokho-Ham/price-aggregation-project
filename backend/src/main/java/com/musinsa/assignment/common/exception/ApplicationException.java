package com.musinsa.assignment.common.exception;

import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException{

    private final ApplicationErrorCode errorCode;

    public ApplicationException(ApplicationErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}
