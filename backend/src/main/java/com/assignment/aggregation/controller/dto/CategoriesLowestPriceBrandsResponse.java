package com.assignment.aggregation.controller.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class CategoriesLowestPriceBrandsResponse {

    private List<CategoryLowestPriceBrandResponse> content;
    private double totalPrice;

    public CategoriesLowestPriceBrandsResponse(List<CategoryLowestPriceBrandResponse> content) {
        this.content = content;
        this.totalPrice = content.stream().mapToDouble(CategoryLowestPriceBrandResponse::getPrice).sum();
    }
}
