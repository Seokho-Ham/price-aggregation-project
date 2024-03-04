package com.musinsa.assignment.aggregation.domain;

import jakarta.persistence.Id;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash("category:brand:lowest:highest")
public class CategoryLowestHighestPriceBrand {

    @Id
    private Long id;
    private String categoryName;
    private BrandPriceInfo lowestPriceBrand;
    private BrandPriceInfo highestPriceBrand;

    public CategoryLowestHighestPriceBrand(Long id, String categoryName, BrandPriceInfo lowestPriceBrand, BrandPriceInfo highestPriceBrand) {
        this.id = id;
        this.categoryName = categoryName;
        this.lowestPriceBrand = lowestPriceBrand;
        this.highestPriceBrand = highestPriceBrand;
    }
}
