package com.assignment.aggregation.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class BrandLowestPriceInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long brandId;
    private Long categoryId;
    private String brandName;
    private String categoryName;
    private Double price;

    public BrandLowestPriceInfo(Long brandId, Long categoryId, String brandName, String categoryName, double price) {
        this.brandId = brandId;
        this.categoryId = categoryId;
        this.brandName = brandName;
        this.categoryName = categoryName;
        this.price = price;
    }
}
