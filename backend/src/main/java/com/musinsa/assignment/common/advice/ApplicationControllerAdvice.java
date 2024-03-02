package com.musinsa.assignment.common.advice;

import com.musinsa.assignment.common.dto.ApplicationResponse;
import com.musinsa.assignment.common.exception.ApplicationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ApplicationControllerAdvice {

    @ExceptionHandler(ApplicationException.class)
    public ApplicationResponse<Void> handleApplicationException(ApplicationException exception) {
        log.error("{} - code: {}, message: {}", exception.getClass(), exception.getErrorCode().getCode(), exception.getErrorCode().getMessage());
        return ApplicationResponse.fail(exception.getErrorCode());
    }

}
