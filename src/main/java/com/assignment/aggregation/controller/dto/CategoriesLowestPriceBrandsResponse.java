package com.assignment.aggregation.controller.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class CategoriesLowestPriceBrandsResponse {

    private List<CategoryLowestPriceBrandResponse> content;
    private double totalPrice;

    public CategoriesLowestPriceBrandsResponse(List<CategoryLowestPriceBrandResponse> content, double totalPrice) {
        this.content = content;
        this.totalPrice = totalPrice;
    }
}
