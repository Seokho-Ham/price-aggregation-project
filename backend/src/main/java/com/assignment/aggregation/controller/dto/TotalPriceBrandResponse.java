package com.assignment.aggregation.controller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class TotalPriceBrandResponse {

    private final String brandName;
    private final List<CategoryPriceResponse> categories;
    private final Double totalPrice;

    @JsonCreator
    public TotalPriceBrandResponse(
        @JsonProperty("브랜드") String brandName,
        @JsonProperty("카테고리")List<CategoryPriceResponse> categories,
        @JsonProperty("총액")Double totalPrice
    ) {
        this.brandName = brandName;
        this.categories = categories;
        this.totalPrice = totalPrice;
    }
}
