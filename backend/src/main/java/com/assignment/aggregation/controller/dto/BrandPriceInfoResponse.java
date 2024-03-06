package com.assignment.aggregation.controller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class BrandPriceInfoResponse {

    private String brandName;
    private double price;

    @JsonCreator
    public BrandPriceInfoResponse(
        @JsonProperty("브랜드") String brandName,
        @JsonProperty("가격")double price
    ) {
        this.brandName = brandName;
        this.price = price;
    }

}
