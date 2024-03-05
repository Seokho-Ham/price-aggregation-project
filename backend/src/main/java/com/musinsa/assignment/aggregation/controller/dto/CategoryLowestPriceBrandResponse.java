package com.musinsa.assignment.aggregation.controller.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CategoryLowestPriceBrandResponse {

    private final String categoryName;
    private final String brandName;
    private final double price;
}
