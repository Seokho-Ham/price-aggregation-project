package com.assignment.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApplicationErrorCode {

    //Common
    INVALID_PARAM("COMMON-001", "올바르지 않은 값을 전달하였습니다."),
    CACHE_DATA_PARSING_FAIL("COMMON-002", "캐시에서 조회한 데이터를 파싱하는데 실패하였습니다."),

    //Brand
    BRAND_NOT_FOUND("BRAND-001", "조건에 맞는 브랜드 정보를 찾지 못했습니다."),
    BRAND_DUPLICATE("BRAND-002", "동일한 이름의 브랜드가 존재합니다."),

    //Item
    ITEM_DUPLICATE("ITEM-001", "동일한 이름의 상품이 존재합니다."),
    INVALID_PRICE("ITEM-002", "올바르지 않은 가격정보입니다."),
    ITEM_NOT_FOUND("ITEM-003", "조건에 맞는 상품 정보를 찾지 못했습니다."),

    //Category
    CATEGORY_NOT_FOUND("CATEGORY-001", "조건에 맞는 카테고리 정보를 찾지 못했습니다."),

    //Aggregation
    LOWEST_TOTAL_PRICE_BRAND_NOT_FOUND("AGGREGATION-001", "총액 최저가 브랜드에 관련해 집계된 데이터가 존재하지 않습니다."),

    //Redis
    CACHE_NOT_FOUND("REDIS-001", "등록된 캐시를 찾지 못했습니다."),
    REDIS_SERVER_EXCEPTION("REDIS-010","레디스 서버 실행에 실패하였습니다." );

    private final String code;
    private final String message;

}
