package com.assignment.aggregation.repository.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class CategoryPriceBrandDto {

    private Long categoryId;
    private String categoryName;
    private Long brandId;
    private String brandName;
    private Double price;

    @QueryProjection
    public CategoryPriceBrandDto(Long categoryId, String categoryName, Long brandId, String brandName, Double price) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.brandId = brandId;
        this.brandName = brandName;
        this.price = price;
    }
}
