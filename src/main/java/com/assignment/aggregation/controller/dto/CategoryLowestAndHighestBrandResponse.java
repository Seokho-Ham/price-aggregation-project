package com.assignment.aggregation.controller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class CategoryLowestAndHighestBrandResponse {

    private String categoryName;
    private List<BrandPriceInfoResponse> lowestPriceBrand;
    private List<BrandPriceInfoResponse> highestPriceBrand;

    @JsonCreator
    public CategoryLowestAndHighestBrandResponse(
        @JsonProperty("카테고리") String categoryName,
        @JsonProperty("최저가") List<BrandPriceInfoResponse> lowestPriceBrand,
        @JsonProperty("최고가") List<BrandPriceInfoResponse> highestPriceBrand
    ) {
        this.categoryName = categoryName;
        this.lowestPriceBrand = lowestPriceBrand;
        this.highestPriceBrand = highestPriceBrand;
    }
}
