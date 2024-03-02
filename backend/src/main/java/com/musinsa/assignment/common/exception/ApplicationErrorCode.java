package com.musinsa.assignment.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApplicationErrorCode {
    ;

    private final String code;
    private final String message;

}
