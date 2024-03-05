package com.assignment.aggregation.domain;

import lombok.Getter;

@Getter
public class BrandPriceInfo {

    private String brandName;
    private Double price;

    public BrandPriceInfo(String brandName, Double price) {
        this.brandName = brandName;
        this.price = price;
    }
}
