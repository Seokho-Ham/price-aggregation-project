package com.assignment.aggregation.controller.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class CategoryLowestPriceBrandResponse {

    private final String categoryName;
    private final List<String> brandNames;
    private final double price;
}
