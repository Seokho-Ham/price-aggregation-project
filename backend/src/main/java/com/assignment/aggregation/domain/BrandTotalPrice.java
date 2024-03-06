package com.assignment.aggregation.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class BrandTotalPrice {

    @Id
    private Long brandId;
    private Double price;

    public BrandTotalPrice(Long brandId, Double price) {
        this.brandId = brandId;
        this.price = price;
    }
}
