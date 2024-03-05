package com.musinsa.assignment.aggregation.domain;

import jakarta.persistence.Id;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;

@Getter
@RedisHash("brand:total:price")
public class BrandTotalPrice {

    @Id
    private Long id;
    private String brandName;
    private double totalPrice;
    private List<ItemPriceAndCategoryInfo> items;

    public BrandTotalPrice(Long id, String brandName, double totalPrice, List<ItemPriceAndCategoryInfo> items) {
        this.id = id;
        this.brandName = brandName;
        this.totalPrice = totalPrice;
        this.items = items;
    }
}
