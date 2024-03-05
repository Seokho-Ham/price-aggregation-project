package com.assignment.aggregation.domain;

import jakarta.persistence.Id;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash("category:brand:lowest")
public class CategoryLowestPriceBrand {

    @Id
    private Long id;
    private String categoryName;
    private BrandPriceInfo lowestPriceBrand;

    public CategoryLowestPriceBrand(Long id, String categoryName, BrandPriceInfo lowestPriceBrand) {
        this.id = id;
        this.categoryName = categoryName;
        this.lowestPriceBrand = lowestPriceBrand;
    }
}
