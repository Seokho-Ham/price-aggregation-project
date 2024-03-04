package com.musinsa.assignment.aggregation.service;

import com.musinsa.assignment.aggregation.domain.BrandPriceInfo;
import com.musinsa.assignment.aggregation.domain.CategoryLowestHighestPriceBrand;
import com.musinsa.assignment.aggregation.domain.CategoryLowestHighestPriceBrandRedisRepository;
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
    private final CategoryLowestHighestPriceBrandRedisRepository categoryLowestHighestPriceBrandRedisRepository;

    public void aggregate(Long itemId) {
        itemRepository.findByIdAndDeletedIsFalse(itemId)
            .ifPresentOrElse(
                item -> aggregateCategoryLowestHighestPriceBrand(item.getCategoryId()),
                () -> log.error("[aggregate-item] 데이터 집계에 실패(상품 존재하지 않음) - itemId: {}", itemId));
    }

    public CategoryLowestHighestPriceBrand getCategoryLowestHighestPriceBrand(String categoryName) {
        Category category = categoryRepository.findByName(categoryName)
            .orElseThrow(CategoryNotFoundException::new);
        return categoryLowestHighestPriceBrandRedisRepository.findById(category.getId())
            .orElseThrow(CacheNotFoundException::new);
    }

    private void aggregateCategoryLowestHighestPriceBrand(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(CategoryNotFoundException::new);
        BrandPriceInfo lowestPriceBrandInfo = getLowestPriceBrandInfo(categoryId);
        BrandPriceInfo highestPriceBrandInfo = getHighestPriceBrandInfo(categoryId);

        CategoryLowestHighestPriceBrand categoryLowestHighestPriceBrand = new CategoryLowestHighestPriceBrand(
            category.getId(),
            category.getName(),
            lowestPriceBrandInfo,
            highestPriceBrandInfo
        );

        categoryLowestHighestPriceBrandRedisRepository.save(categoryLowestHighestPriceBrand);
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
