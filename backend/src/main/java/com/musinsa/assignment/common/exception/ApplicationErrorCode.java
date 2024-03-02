package com.musinsa.assignment.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApplicationErrorCode {

    BRAND_NOT_FOUND("BRAND-001", "조건에 맞는 브랜드 정보를 찾지 못했습니다."),
    BRAND_DUPLICATION("BRAND-002", "동일한 이름의 브랜드가 존재합니다.");

    private final String code;
    private final String message;

}
