package com.musinsa.assignment.aggregation.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.musinsa.assignment.aggregation.domain.BrandPriceInfo;

public class BrandPriceInfoResponse {

    private String brandName;
    private double price;

    public BrandPriceInfoResponse(
        @JsonProperty("브랜드") String brandName,
        @JsonProperty("가격")double price
    ) {
        this.brandName = brandName;
        this.price = price;
    }

    public static BrandPriceInfoResponse from(BrandPriceInfo info) {
        return new BrandPriceInfoResponse(info.getBrandName(), info.getPrice());
    }
}
