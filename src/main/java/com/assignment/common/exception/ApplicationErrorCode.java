package com.assignment.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApplicationErrorCode {

    //Common
    INVALID_PARAM("COMMON-001", "올바르지 않은 값을 전달하였습니다."),
    OBJECT_PARSING_FAIL("COMMON-002", "데이터를 파싱하는데 실패하였습니다."),

    //Server
    SERVER_ERROR("SERVER-001", "서버 오류 입니다."),

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
    LOWEST_TOTAL_PRICE_BRAND_NOT_FOUND("AGGREGATION-001", "총액 최저가 브랜드에 대해 집계된 데이터가 존재하지 않습니다."),
    NO_PRICE_DATA("AGGREGATION-002", "카테고리의 최저가, 최고가 관련 가격 정보가 존재하지 않습니다."),
    CATEGORY_LOWEST_PRICE_BRAND_NOT_FOUND("AGGREGATION-003", "카테고리 최저가 브랜드에 대해 집계된 데이터가 존재하지 않습니다."),
    CATEGORY_HIGHEST_PRICE_BRAND_NOT_FOUND("AGGREGATION-004", "카테고리 최고가 브랜드에 대해 집계된 데이터가 존재하지 않습니다."),

    //Redis
    CACHE_NOT_FOUND("REDIS-001", "등록된 캐시를 찾지 못했습니다."),
    REDIS_SERVER_EXCEPTION("REDIS-010","레디스 서버 실행에 실패하였습니다." );

    private final String code;
    private final String message;

}
