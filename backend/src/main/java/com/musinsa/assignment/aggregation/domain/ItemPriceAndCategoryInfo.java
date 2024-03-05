package com.musinsa.assignment.aggregation.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ItemPriceAndCategoryInfo {

    private final String categoryName;
    private final Double price;

}
