package com.musinsa.assignment.aggregation.service;

import com.musinsa.assignment.aggregation.controller.dto.BrandPriceInfoResponse;
import com.musinsa.assignment.aggregation.controller.dto.CategoryLowestAndHighestBrandResponse;
import com.musinsa.assignment.aggregation.domain.*;
import com.musinsa.assignment.aggregation.exception.CacheNotFoundException;
import com.musinsa.assignment.brand.domain.Brand;
import com.musinsa.assignment.brand.domain.BrandRepository;
import com.musinsa.assignment.brand.exception.BrandNotFoundException;
import com.musinsa.assignment.category.domain.Category;
import com.musinsa.assignment.category.domain.CategoryRepository;
import com.musinsa.assignment.category.exception.CategoryNotFoundException;
import com.musinsa.assignment.item.domain.Item;
import com.musinsa.assignment.item.domain.ItemRepository;
import com.musinsa.assignment.item.exception.ItemNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class AggregationService {

    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ItemRepository itemRepository;
    private final CategoryLowestPriceBrandRedisRepository categoryLowestPriceBrandRedisRepository;
    private final CategoryHighestPriceBrandRedisRepository categoryHighestPriceBrandRedisRepository;

    public CategoryLowestAndHighestBrandResponse getCategoryLowestHighestPriceBrand(String categoryName) {
        Category category = categoryRepository.findByName(categoryName)
            .orElseThrow(CategoryNotFoundException::new);
        CategoryHighestPriceBrand highestPriceBrand = categoryHighestPriceBrandRedisRepository.findById(category.getId())
            .orElseThrow(CacheNotFoundException::new);
        CategoryLowestPriceBrand lowestPriceBrand = categoryLowestPriceBrandRedisRepository.findById(category.getId())
            .orElseThrow(CacheNotFoundException::new);

        return new CategoryLowestAndHighestBrandResponse(
            category.getName(),
            BrandPriceInfoResponse.from(lowestPriceBrand.getLowestPriceBrand()),
            BrandPriceInfoResponse.from(highestPriceBrand.getHighestPriceBrand())
        );
    }

    public void aggregate(Long itemId) {
        itemRepository.findByIdAndDeletedIsFalse(itemId)
            .ifPresentOrElse(
                item -> {
                    Category category = categoryRepository.findById(item.getCategoryId())
                        .orElseThrow(CategoryNotFoundException::new);
                    aggregateCategoryLowestPriceBrand(category);
                    aggregateCategoryHighestPriceBrand(category);
                },
                () -> log.error("[aggregate-item] 데이터 집계에 실패(상품 존재하지 않음) - itemId: {}", itemId));
    }

    private void aggregateCategoryLowestPriceBrand(Category category) {
        BrandPriceInfo lowestPriceBrandInfo = getLowestPriceBrandInfo(category.getId());
        CategoryLowestPriceBrand categoryLowestPriceBrand = new CategoryLowestPriceBrand(
            category.getId(),
            category.getName(),
            lowestPriceBrandInfo
        );

        categoryLowestPriceBrandRedisRepository.save(categoryLowestPriceBrand);
    }

    private void aggregateCategoryHighestPriceBrand(Category category) {
        BrandPriceInfo highestPriceBrandInfo = getHighestPriceBrandInfo(category.getId());
        CategoryHighestPriceBrand categoryLowestPriceBrand = new CategoryHighestPriceBrand(
            category.getId(),
            category.getName(),
            highestPriceBrandInfo
        );

        categoryHighestPriceBrandRedisRepository.save(categoryLowestPriceBrand);
    }

    private BrandPriceInfo getLowestPriceBrandInfo(Long categoryId) {
        Item item = itemRepository.findTopByCategoryIdAndDeletedIsFalseOrderByPriceAsc(categoryId)
            .orElseThrow(ItemNotFoundException::new);
        Brand brand = brandRepository.findByIdAndDeletedIsFalse(item.getBrandId())
            .orElseThrow(BrandNotFoundException::new);
        return new BrandPriceInfo(brand.getName(), item.getPrice());
    }

    private BrandPriceInfo getHighestPriceBrandInfo(Long categoryId) {
        Item item = itemRepository.findTopByCategoryIdAndDeletedIsFalseOrderByPriceDesc(categoryId)
            .orElseThrow(ItemNotFoundException::new);
        Brand brand = brandRepository.findByIdAndDeletedIsFalse(item.getBrandId())
            .orElseThrow(BrandNotFoundException::new);
        return new BrandPriceInfo(brand.getName(), item.getPrice());
    }
}
