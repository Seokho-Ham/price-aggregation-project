package com.assignment.common.dto;

import com.assignment.common.exception.ApplicationErrorCode;
import lombok.Getter;

@Getter
public class ApplicationResponse<T> {

    private static final String SUCCESS_CODE = "SUCCESS-001";
    private static final String SUCCESS_MESSAGE = "요청이 성공적으로 이루어졌습니다.";

    private final T data;
    private final String code;
    private final String message;

    private ApplicationResponse(T data, String code, String message) {
        this.data = data;
        this.code = code;
        this.message = message;
    }

    public static ApplicationResponse<Void> success() {
        return new ApplicationResponse<>(null, SUCCESS_CODE, SUCCESS_MESSAGE);
    }

    public static<T> ApplicationResponse<T> success(T body) {
        return new ApplicationResponse<>(body, SUCCESS_CODE, SUCCESS_MESSAGE);
    }

    public static ApplicationResponse<Void> fail(ApplicationErrorCode errorCode) {
        return new ApplicationResponse<>(null, errorCode.getCode(), errorCode.getMessage());
    }

}
