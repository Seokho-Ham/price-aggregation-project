package com.musinsa.assignment.aggregation.controller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class CategoryLowestAndHighestBrandResponse {

    private String categoryName;
    private BrandPriceInfoResponse lowestPriceBrand;
    private BrandPriceInfoResponse highestPriceBrand;

    @JsonCreator
    public CategoryLowestAndHighestBrandResponse(
        @JsonProperty("카테고리") String categoryName,
        @JsonProperty("최저가") BrandPriceInfoResponse lowestPriceBrand,
        @JsonProperty("최고가") BrandPriceInfoResponse highestPriceBrand
    ) {
        this.categoryName = categoryName;
        this.lowestPriceBrand = lowestPriceBrand;
        this.highestPriceBrand = highestPriceBrand;
    }
}
