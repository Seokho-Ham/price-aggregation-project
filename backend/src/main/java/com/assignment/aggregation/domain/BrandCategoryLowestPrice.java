package com.assignment.aggregation.domain;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class BrandCategoryLowestPrice {

    @EmbeddedId
    private BrandCategoryLowestPricePk id;
    private String brandName;
    private String categoryName;
    private Double price;

    public BrandCategoryLowestPrice(Long brandId, Long categoryId, String brandName, String categoryName, double price) {
        this.id = new BrandCategoryLowestPricePk(brandId, categoryId);
        this.brandName = brandName;
        this.categoryName = categoryName;
        this.price = price;
    }
}
