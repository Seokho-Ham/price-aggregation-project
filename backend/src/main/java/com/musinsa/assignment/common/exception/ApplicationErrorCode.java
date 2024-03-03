package com.musinsa.assignment.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApplicationErrorCode {

    //Common
    INVALID_PARAM("COMMON-001", "올바르지 않은 값을 전달하였습니다."),

    //Brand
    BRAND_NOT_FOUND("BRAND-001", "조건에 맞는 브랜드 정보를 찾지 못했습니다."),
    BRAND_DUPLICATE("BRAND-002", "동일한 이름의 브랜드가 존재합니다."),

    //Item
    ITEM_DUPLICATE("ITEM-001", "동일한 이름의 상품이 존재합니다."),
    INVALID_PRICE("ITEM-002", "올바르지 않은 가격정보입니다."),
    ITEM_NOT_FOUND("ITEM-003", "조건에 맞는 상품 정보를 찾지 못했습니다."),

    //Category
    CATEGORY_NOT_FOUND("CATEGORY-001", "조건에 맞는 카테고리 정보를 찾지 못했습니다.");

    private final String code;
    private final String message;

}
