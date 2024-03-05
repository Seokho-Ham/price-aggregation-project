package com.assignment.aggregation.service;

import com.assignment.aggregation.domain.*;
import com.assignment.brand.domain.Brand;
import com.assignment.brand.domain.BrandRepository;
import com.assignment.category.exception.CategoryNotFoundException;
import com.assignment.item.domain.ItemRepository;
import com.assignment.item.exception.ItemNotFoundException;
import com.assignment.aggregation.repository.AggregationQueryRepository;
import com.assignment.aggregation.repository.dto.BrandCategoryDataDto;
import com.assignment.brand.exception.BrandNotFoundException;
import com.assignment.category.domain.Category;
import com.assignment.category.domain.CategoryRepository;
import com.assignment.item.domain.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class AggregationService {

    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ItemRepository itemRepository;
    private final AggregationQueryRepository aggregationQueryRepository;
    private final CategoryLowestPriceBrandRedisRepository categoryLowestPriceBrandRedisRepository;
    private final CategoryHighestPriceBrandRedisRepository categoryHighestPriceBrandRedisRepository;
    private final BrandTotalPriceRedisRepository brandTotalPriceRedisRepository;

    /**
     * 전체 집계 데이터를 재집계 하는 api
     */
    public void reaggregateAllData() {
        categoryHighestPriceBrandRedisRepository.deleteAll();
        categoryLowestPriceBrandRedisRepository.deleteAll();
        brandTotalPriceRedisRepository.deleteAll();
        aggregateLowestHighestPriceBrandForAllCategories();
        aggregateBrandTotalPriceForAllBrands();
    }

    /**
     * 특정 brandId의 정보가 담긴 데이터를 재집계하는 api
     * @param brandId
     */
    public void reaggreateByBrandId(Long brandId) {
        removeBrandTotalPriceCacheByBrandId(brandId);
        aggregateBrandTotalPriceByBrandId(brandId);
        aggregateLowestHighestPriceBrandForAllCategories();
    }

    /**
     * 특정 itemId의 정보를 바탕으로 데이터를 재집계하는 api
     * @param itemId
     */
    public void reaggreateByItemId(Long itemId) {
        Optional<Item> findItem = itemRepository.findById(itemId);

        if (findItem.isEmpty()) {
            log.error("[aggregate] 데이터 집계에 실패(상품 정보가 존재하지 않음) - itemId: {}", itemId);
            return;
        }

        Item item = findItem.get();

        removeBrandTotalPriceCacheByBrandId(item.getBrandId());
        removeLowestAndHighestBrandCacheByCategoryId(item.getCategoryId());
        aggregateBrandTotalPriceByBrandId(item.getBrandId());
        aggregateLowestHighestPriceBrandForSingleCategory(item.getId());
    }

    /**
     * 특정 브랜드의 카테고리별 최저가 상품 총액 정보를 집계하는 기능
     * @param brandId
     */
    private void aggregateBrandTotalPriceByBrandId(Long brandId) {
        List<BrandCategoryDataDto> dtos = aggregationQueryRepository.findByBrandId(brandId);

        if (dtos.isEmpty()) {
            log.error("[aggregate] 데이터 집계에 실패(브랜드에 해당하는 상품이 존재하지 않음) - brandId: {}", brandId);
            return;
        }

        BrandTotalPrice brandTotalPrice = convertToBrandTotalPrice(brandId, dtos);
        brandTotalPriceRedisRepository.save(brandTotalPrice);
    }

    /**
     * 모든 브랜드의 카테고리별 최저가 상품 총액 정보를 집계하는 api
     */
    private void aggregateBrandTotalPriceForAllBrands() {
        List<BrandCategoryDataDto> brandData = aggregationQueryRepository.getAllBrandData();

        List<BrandTotalPrice> result = brandData.stream()
            .collect(Collectors.groupingBy(BrandCategoryDataDto::getBrandId))
            .entrySet().stream()
            .map(entry -> convertToBrandTotalPrice(entry.getKey(), entry.getValue()))
            .toList();

        brandTotalPriceRedisRepository.saveAll(result);
    }

    /**
     * 특정 카테고리의 최저가 브랜드와 가격을 집계하는 api
     *
     * @param itemId
     */
    private void aggregateLowestHighestPriceBrandForSingleCategory(Long itemId) {
        itemRepository.findByIdAndDeletedIsFalse(itemId)
            .ifPresentOrElse(
                item -> {
                    Category category = categoryRepository.findById(item.getCategoryId())
                        .orElseThrow(CategoryNotFoundException::new);
                    aggregateLowestAndHighestPriceBrand(category);
                },
                () -> log.error("[aggregate] 데이터 집계에 실패(상품 정보가 존재하지 않음) - itemId: {}", itemId));
    }

    /**
     * 전체 카테고리별로 최저가 브랜드와 가격을 집계하는 api
     */
    private void aggregateLowestHighestPriceBrandForAllCategories() {
        List<Category> categories = categoryRepository.findAll();

        if (categories.isEmpty()) {
            log.error("[aggregate] 데이터 집계에 실패(카테고리가 존재하지 않음)");
            return;
        }

        categories.forEach(this::aggregateLowestAndHighestPriceBrand);
    }

    private void aggregateLowestAndHighestPriceBrand(Category category) {
        BrandPriceInfo lowestPriceBrandInfo = getLowestPriceBrandInfo(category.getId());
        BrandPriceInfo highestPriceBrandInfo = getHighestPriceBrandInfo(category.getId());

        CategoryLowestPriceBrand categoryLowestPriceBrand = new CategoryLowestPriceBrand(
            category.getId(),
            category.getName(),
            lowestPriceBrandInfo
        );
        CategoryHighestPriceBrand categoryHighestPriceBrand = new CategoryHighestPriceBrand(
            category.getId(),
            category.getName(),
            highestPriceBrandInfo
        );

        categoryLowestPriceBrandRedisRepository.save(categoryLowestPriceBrand);
        categoryHighestPriceBrandRedisRepository.save(categoryHighestPriceBrand);
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

    private void removeBrandTotalPriceCacheByBrandId(Long brandId) {
        brandTotalPriceRedisRepository.deleteById(brandId);
    }

    private void removeLowestAndHighestBrandCacheByCategoryId(Long categoryId) {
        categoryLowestPriceBrandRedisRepository.deleteById(categoryId);
        categoryHighestPriceBrandRedisRepository.deleteById(categoryId);
    }

    private BrandTotalPrice convertToBrandTotalPrice(Long brandId, List<BrandCategoryDataDto> dtos) {
        String brandName = dtos.stream().map(BrandCategoryDataDto::getBrandName).distinct().findFirst().orElseThrow();
        double totalPrice = dtos.stream().mapToDouble(BrandCategoryDataDto::getPrice).sum();
        List<ItemPriceAndCategoryInfo> items = dtos.stream()
            .map(dto -> new ItemPriceAndCategoryInfo(dto.getCategoryName(), dto.getPrice()))
            .toList();
        return new BrandTotalPrice(brandId, brandName, totalPrice, items);
    }
}
