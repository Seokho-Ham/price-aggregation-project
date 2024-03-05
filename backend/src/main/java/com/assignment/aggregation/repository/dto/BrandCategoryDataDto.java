package com.assignment.aggregation.repository.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class BrandCategoryDataDto {

    private Long brandId;
    private String brandName;
    private Long categoryId;
    private String categoryName;
    private Double price;

    @QueryProjection
    public BrandCategoryDataDto(Long brandId, String brandName, Long categoryId, String categoryName, Double price) {
        this.brandId = brandId;
        this.brandName = brandName;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.price = price;
    }
}
