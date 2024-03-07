package com.assignment.aggregation.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class CategoryPriceResponse {

    private final String categoryName;
    private final Double price;

    public CategoryPriceResponse(
        @JsonProperty("카테고리") String categoryName,
        @JsonProperty("가격") Double price
    ) {
        this.categoryName = categoryName;
        this.price = price;
    }
}
