package com.assignment.aggregation.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@EqualsAndHashCode
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class BrandCategoryLowestPricePk implements Serializable {

    private Long brandId;
    private Long categoryId;

    public BrandCategoryLowestPricePk(Long brandId, Long categoryId) {
        this.brandId = brandId;
        this.categoryId = categoryId;
    }
}
