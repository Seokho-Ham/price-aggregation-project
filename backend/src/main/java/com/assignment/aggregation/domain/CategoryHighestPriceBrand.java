package com.assignment.aggregation.domain;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class CategoryHighestPriceBrand {

    @EmbeddedId
    private CategoryPriceBrandPk id;
    private String categoryName;
    private String brandName;
    private Integer price;

    public CategoryHighestPriceBrand(Long categoryId, Long brandId, String categoryName, String brandName, Integer price) {
        this.id = new CategoryPriceBrandPk(categoryId, brandId);
        this.categoryName = categoryName;
        this.brandName = brandName;
        this.price = price;
    }
}
