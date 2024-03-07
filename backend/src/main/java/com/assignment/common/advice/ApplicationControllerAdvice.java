package com.assignment.common.advice;

import com.assignment.common.dto.ApplicationResponse;
import com.assignment.common.exception.ApplicationErrorCode;
import com.assignment.common.exception.ApplicationException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ApplicationControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ApplicationException.class)
    public ApplicationResponse<Void> handleApplicationException(ApplicationException exception) {
        log.error("[{}] - code: {}, message: {}", exception.getClass(), exception.getErrorCode().getCode(), exception.getErrorCode().getMessage());
        return ApplicationResponse.fail(exception.getErrorCode());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
        ConstraintViolationException.class,
        MethodArgumentNotValidException.class
    })
    public ApplicationResponse<Void> handleValidationException(Exception exception) {
        log.error("[{}] - message:{}", exception.getClass(), exception.getMessage());
        return ApplicationResponse.fail(ApplicationErrorCode.INVALID_PARAM);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({Throwable.class})
    protected ApplicationResponse<Void> handle(Throwable throwable) {
        log.error("[UnknownException] Occur exception.", throwable);
        return ApplicationResponse.fail(ApplicationErrorCode.SERVER_ERROR);
    }


}
