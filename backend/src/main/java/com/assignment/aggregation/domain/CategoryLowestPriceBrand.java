package com.assignment.aggregation.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class CategoryLowestPriceBrand {

    @Id
    private Long categoryId;
    private String categoryName;
    private Long brandId;
    private String brandName;
    private Double price;

    public CategoryLowestPriceBrand(Long categoryId, String categoryName, Long brandId, String brandName, Double price) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.brandId = brandId;
        this.brandName = brandName;
        this.price = price;
    }
}
