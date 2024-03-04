package com.musinsa.assignment.aggregation.domain;

import jakarta.persistence.Id;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash("category:brand:highest")
public class CategoryHighestPriceBrand {

    @Id
    private Long id;
    private String categoryName;
    private BrandPriceInfo highestPriceBrand;

    public CategoryHighestPriceBrand(Long id, String categoryName, BrandPriceInfo highestPriceBrand) {
        this.id = id;
        this.categoryName = categoryName;
        this.highestPriceBrand = highestPriceBrand;
    }
}
