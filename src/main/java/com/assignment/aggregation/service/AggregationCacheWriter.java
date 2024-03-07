package com.assignment.aggregation.service;

import com.assignment.aggregation.domain.BrandLowestPriceInfo;
import com.assignment.aggregation.domain.CategoryHighestPriceBrand;
import com.assignment.aggregation.domain.CategoryLowestPriceBrand;
import com.assignment.aggregation.repository.AggregationCacheRepository;
import com.assignment.common.exception.CacheDataParsingException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.util.stream.Collectors.groupingBy;

@RequiredArgsConstructor
@Service
public class AggregationCacheWriter {

    private final AggregationCacheRepository aggregationCacheRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void saveAggregatedBrandDataInCache(List<BrandLowestPriceInfo> entities) {
        Map<String, String> brandCategoryPriceCacheData = new HashMap<>();
        Set<ZSetOperations.TypedTuple<Object>> brandTotalPriceCacheData = new HashSet<>();

        entities.stream()
            .collect(groupingBy(BrandLowestPriceInfo::getBrandId))
            .forEach((brandId, value) -> {
                double totalPrice = value.stream()
                    .mapToDouble(BrandLowestPriceInfo::getPrice)
                    .sum();
                try {
                    brandCategoryPriceCacheData.put(String.valueOf(brandId), objectMapper.writeValueAsString(value));
                    brandTotalPriceCacheData.add(new DefaultTypedTuple<>(String.valueOf(brandId), totalPrice));
                } catch (JsonProcessingException e) {
                    throw new CacheDataParsingException();
                }
            });

        aggregationCacheRepository.saveAllBrandCategoryPriceCacheData(brandCategoryPriceCacheData);
        aggregationCacheRepository.saveAllBrandTotalPrice(brandTotalPriceCacheData);
    }

    public void saveAggregatedCategoryHighestPriceBrandsInCache(List<CategoryHighestPriceBrand> entities) {
        Map<String, String> categoryHighestPriceBrandCacheData = new HashMap<>();
        entities.stream()
            .collect(groupingBy(CategoryHighestPriceBrand::getCategoryId))
            .forEach((brandId, value) -> {
                try {
                    categoryHighestPriceBrandCacheData.put(String.valueOf(brandId), objectMapper.writeValueAsString(value));
                } catch (JsonProcessingException e) {
                    throw new CacheDataParsingException();
                }
            });
        aggregationCacheRepository.saveCategoryHighestPriceBrandCacheData(categoryHighestPriceBrandCacheData);
    }

    public void saveAggregatedCategoryLowestPriceBrandsInCache(List<CategoryLowestPriceBrand> entities) {
        Map<String, String> categoryLowestPriceBrandCacheData = new HashMap<>();
        entities.stream()
            .collect(groupingBy(CategoryLowestPriceBrand::getCategoryId))
            .forEach((brandId, value) -> {
                try {
                    categoryLowestPriceBrandCacheData.put(String.valueOf(brandId), objectMapper.writeValueAsString(value));
                } catch (JsonProcessingException e) {
                    throw new CacheDataParsingException();
                }
            });
        aggregationCacheRepository.saveCategoryLowestPriceBrandCacheData(categoryLowestPriceBrandCacheData);
    }

    public void deleteBrandTotalPriceByBrandId(Long brandId) {
        aggregationCacheRepository.deleteBrandTotalPriceCacheByBrandId(String.valueOf(brandId));
    }

    public void deleteAllBrandLowestPriceInfoByBrandId(Long brandId) {
        aggregationCacheRepository.deleteAllBrandCategoryPriceCacheByBrandId(String.valueOf(brandId));
    }

    public void deleteAllCategoryLowestPriceBrandByBrandId(Long brandId) {
        aggregationCacheRepository.deleteAllCategoryLowestPriceBrandCacheByBrandId(String.valueOf(brandId));
    }

    public void deleteAllCategoryHighestPriceBrandByBrandId(Long brandId) {
        aggregationCacheRepository.deleteAllCategoryHighestPriceBrandCacheByBrandId(String.valueOf(brandId));
    }
}
