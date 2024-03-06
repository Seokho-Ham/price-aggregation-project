package com.assignment.aggregation.domain;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class BrandLowestPriceInfo {

    @EmbeddedId
    private BrandLowestPriceInfoPk id;
    private String brandName;
    private String categoryName;
    private Double price;

    public BrandLowestPriceInfo(Long brandId, Long categoryId, String brandName, String categoryName, double price) {
        this.id = new BrandLowestPriceInfoPk(brandId, categoryId);
        this.brandName = brandName;
        this.categoryName = categoryName;
        this.price = price;
    }
}
