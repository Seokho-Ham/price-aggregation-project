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
public class CategoryPriceBrandPk implements Serializable {

    private Long categoryId;
    private Long brandId;

    public CategoryPriceBrandPk(Long categoryId, Long brandId) {
        this.categoryId = categoryId;
        this.brandId = brandId;
    }
}
