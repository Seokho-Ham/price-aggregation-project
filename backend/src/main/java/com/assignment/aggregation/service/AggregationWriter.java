package com.assignment.aggregation.service;

import com.assignment.aggregation.domain.*;
import com.assignment.aggregation.repository.AggregationCacheRepository;
import com.assignment.aggregation.repository.AggregationQueryRepository;
import com.assignment.aggregation.repository.dto.BrandCategoryDto;
import com.assignment.aggregation.repository.dto.CategoryPriceBrandDto;
import com.assignment.common.exception.CacheDataParsingException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static java.util.stream.Collectors.groupingBy;

@RequiredArgsConstructor
@Slf4j
@Service
public class AggregationWriter {

    private final AggregationQueryRepository aggregationQueryRepository;
    private final BrandLowestPriceInfoRepository brandLowestPriceInfoRepository;
    private final BrandTotalPriceRepository brandTotalPriceRepository;
    private final CategoryLowestPriceBrandRepository categoryLowestPriceBrandRepository;
    private final CategoryHighestPriceBrandRepository categoryHighestPriceBrandRepository;
    private final AggregationCacheRepository aggregationCacheRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 전달받은 brandId에 해당하는 브랜드의 카테고리별 최저가 정보를 집계하는 로직
     */
    @Transactional
    public void aggregateBrandLowestPriceInfoByBrandId(Long brandId) {
        List<BrandCategoryDto> dtos = aggregationQueryRepository.findByBrandLowestPriceByCategoryId(brandId);

        if (dtos.isEmpty()) {
            log.error("[aggregate-fail]-lowest-total-price-brand: 브랜드에 관련된 상품 정보가 존재하지 않아 집계에 실패하였습니다.");
            return;
        }

        List<BrandLowestPriceInfo> brandLowestPriceInfoEntities = saveBrandLowestPriceInfosToDatabase(dtos);
        saveBrandTotalPriceToDatabaseByBrandId(brandId, dtos);
        saveAggregatedBrandDataInCache(brandLowestPriceInfoEntities);
    }


    /**
     * 전체 카테고리에 대해 최저가 상품을 가진 브랜드와 가격 정보를 집계하는 로직

     * 1. 카테고리별로 각 브랜드가 가진 최저가 정보를 조회한다.
     * 2. 각 카테고리별로 최저가를 가진 브랜드를 필터링하여 집계 데이터로 변환 후 repository에 저장.
     * 3. 집계한 정보를 redisCache에 저장한다.
     */
    @Transactional
    public void aggregateCategoryLowestPriceBrand() {

        List<CategoryPriceBrandDto> dtos = aggregationQueryRepository.getCategoryLowestPriceBrandDtos();

        if (dtos.isEmpty()) {
            log.error("[aggregate-fail]-category-lowest-price: 해당 카테고리에 속한 상품 정보가 존재하지 않아 집계에 실패하였습니다.");
            return;
        }

        List<CategoryLowestPriceBrand> entities = saveCategoryLowestPriceBrandsInDatabase(dtos);
        saveAggregatedCategoryLowestPriceBrandsInCache(entities);
    }

    /**
     * 전체 카테고리에 대해 최고가 상품을 가진 브랜드와 가격 정보를 집계하는 로직

     * 1. 카테고리별로 각 브랜드가 가진 최고가 정보를 조회한다.
     * 2. 각 카테고리별로 최고가를 가진 브랜드를 필터링하여 집계 데이터로 변환 후 repository에 저장.
     * 3. 집계한 정보를 redisCache에 저장한다.
     */
    @Transactional
    public void aggregateCategoryHighestPriceBrand() {
        List<CategoryPriceBrandDto> dtos = aggregationQueryRepository.getCategoryHighestPriceBrandDtos();

        if (dtos.isEmpty()) {
            log.error("[aggregate-fail]-category-highest-price: 해당 카테고리에 속한 상품 정보가 존재하지 않아 집계에 실패하였습니다.");
            return;
        }

        List<CategoryHighestPriceBrand> entities = saveCategoryHighestPriceBrandInDatabase(dtos);
        saveAggregatedCategoryHighestPriceBrandsInCache(entities);
    }

    @Transactional
    public void aggregateAllBrandData() {
        List<BrandCategoryDto> dtos = aggregationQueryRepository.getAllBrandData();

        if (dtos.isEmpty()) {
            log.error("[aggregate-fail]-category-lowest-price: 해당 카테고리에 속한 상품 정보가 존재하지 않아 집계에 실패하였습니다.");
            return;
        }

        List<BrandLowestPriceInfo> brandLowestPriceInfoEntities = saveBrandLowestPriceInfosToDatabase(dtos);
        saveAllBrandTotalPriceToDatabase(dtos);
        saveAggregatedBrandDataInCache(brandLowestPriceInfoEntities);
        aggregateCategoryLowestPriceBrand();
        aggregateCategoryHighestPriceBrand();
    }

    @Transactional
    public void deleteBrandLowestPriceInfoByBrandId(Long brandId) {
        brandLowestPriceInfoRepository.deleteAllById_BrandId(brandId);
    }

    private List<BrandLowestPriceInfo> saveBrandLowestPriceInfosToDatabase(List<BrandCategoryDto> dtos) {
        List<BrandLowestPriceInfo> brandLowestPriceInfoEntities = dtos.stream()
            .map(dto -> new BrandLowestPriceInfo(dto.getBrandId(), dto.getCategoryId(), dto.getBrandName(), dto.getCategoryName(), dto.getPrice()))
            .toList();
        brandLowestPriceInfoRepository.saveAll(brandLowestPriceInfoEntities);
        return brandLowestPriceInfoEntities;
    }

    private void saveBrandTotalPriceToDatabaseByBrandId(Long brandId, List<BrandCategoryDto> dtos) {
        double totalPrice = dtos.stream()
            .mapToDouble(BrandCategoryDto::getPrice)
            .sum();

        brandTotalPriceRepository.save(new BrandTotalPrice(brandId, totalPrice));
    }

    private void saveAllBrandTotalPriceToDatabase(List<BrandCategoryDto> dtos) {
        List<BrandTotalPrice> entities = dtos.stream()
            .collect(groupingBy(BrandCategoryDto::getBrandId))
            .entrySet()
            .stream()
            .map(entry -> {
                Long brandId = entry.getKey();
                double totalPrice = entry.getValue()
                    .stream()
                    .mapToDouble(BrandCategoryDto::getPrice)
                    .sum();
                return new BrandTotalPrice(brandId, totalPrice);
            })
            .toList();

        brandTotalPriceRepository.saveAll(entities);
    }

    private List<CategoryHighestPriceBrand> saveCategoryHighestPriceBrandInDatabase(List<CategoryPriceBrandDto> dtos) {
        List<CategoryHighestPriceBrand> entities = dtos
            .stream()
            .collect(groupingBy(CategoryPriceBrandDto::getCategoryId)).values()
            .stream()
            .map(dtoList -> {
                double maxPrice = dtoList.stream()
                    .mapToDouble(CategoryPriceBrandDto::getPrice)
                    .max().orElseThrow();

                return dtoList.stream()
                    .filter(dto -> dto.getPrice().equals(maxPrice))
                    .toList();
            })
            .flatMap(Collection::stream)
            .map(val -> new CategoryHighestPriceBrand(val.getCategoryId(), val.getCategoryName(), val.getBrandId(), val.getBrandName(), val.getPrice()))
            .toList();

        categoryHighestPriceBrandRepository.saveAll(entities);
        return entities;
    }

    private List<CategoryLowestPriceBrand> saveCategoryLowestPriceBrandsInDatabase(List<CategoryPriceBrandDto> dtos) {
        List<CategoryLowestPriceBrand> entities = dtos
            .stream()
            .collect(groupingBy(CategoryPriceBrandDto::getCategoryId)).values()
            .stream()
            .map(dtoList -> {
                double minPrice = dtoList.stream()
                    .mapToDouble(CategoryPriceBrandDto::getPrice)
                    .min().orElseThrow();

                return dtoList.stream()
                    .filter(dto -> dto.getPrice().equals(minPrice))
                    .toList();
            })
            .flatMap(Collection::stream)
            .map(val -> new CategoryLowestPriceBrand(val.getCategoryId(), val.getCategoryName(), val.getBrandId(), val.getBrandName(), val.getPrice()))
            .toList();

        categoryLowestPriceBrandRepository.saveAll(entities);
        return entities;
    }

    private void saveAggregatedBrandDataInCache(List<BrandLowestPriceInfo> entities) {
        Map<String, String> brandCategoryPriceCacheData = new HashMap<>();
        Set<ZSetOperations.TypedTuple<Object>> brandTotalPriceCacheData = new HashSet<>();

        entities.stream()
            .collect(groupingBy(entity -> entity.getId().getBrandId()))
            .forEach((brandId, value) -> {
                double totalPrice = value.stream()
                    .mapToDouble(BrandLowestPriceInfo::getPrice)
                    .sum();
                try {
                    brandCategoryPriceCacheData.put(String.valueOf(brandId), objectMapper.writeValueAsString(value));
                    brandTotalPriceCacheData.add(new DefaultTypedTuple<>(String.valueOf(brandId), totalPrice));
                } catch (JsonProcessingException e) {
                    //파싱 실패
                    throw new RuntimeException(e);
                }
            });

        aggregationCacheRepository.saveAllBrandCategoryPriceCacheData(brandCategoryPriceCacheData);
        aggregationCacheRepository.saveAllBrandTotalPrice(brandTotalPriceCacheData);
    }

    private void saveAggregatedCategoryHighestPriceBrandsInCache(List<CategoryHighestPriceBrand> entities) {
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

    private void saveAggregatedCategoryLowestPriceBrandsInCache(List<CategoryLowestPriceBrand> entities) {
        Map<String, String> categoryHighestPriceBrandCacheData = new HashMap<>();

        entities.stream()
            .collect(groupingBy(CategoryLowestPriceBrand::getCategoryId))
            .forEach((brandId, value) -> {
                try {
                    categoryHighestPriceBrandCacheData.put(String.valueOf(brandId), objectMapper.writeValueAsString(value));
                } catch (JsonProcessingException e) {
                    throw new CacheDataParsingException();
                }
            });
        aggregationCacheRepository.saveCategoryLowestPriceBrandCacheData(categoryHighestPriceBrandCacheData);
    }

}
