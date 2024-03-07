package com.assignment.aggregation.service;

import com.assignment.aggregation.domain.*;
import com.assignment.aggregation.repository.AggregationQueryRepository;
import com.assignment.aggregation.repository.dto.BrandCategoryDto;
import com.assignment.aggregation.repository.dto.CategoryPriceBrandDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    /**
     * 전달받은 id에 해당하는 브랜드의 단일 카테고리의 최저가 정보를 집계하는 로직
     */
    @Transactional
    public List<BrandLowestPriceInfo> aggregateOneBrandLowestPriceInfoByBrandIdAndCategoryId(Long brandId, Long categoryId) {
        Optional<BrandCategoryDto> result = aggregationQueryRepository.findBrandLowestPriceByBrandIdAndCategoryId(brandId, categoryId);

        if (result.isEmpty()) {
            log.error("[aggregate-fail]-lowest-total-price-brand: 브랜드에 관련된 상품 정보가 존재하지 않아 집계에 실패하였습니다.");
            return List.of();
        }
        BrandCategoryDto dto = result.get();
        brandLowestPriceInfoRepository.save(new BrandLowestPriceInfo(dto.getBrandId(), dto.getCategoryId(), dto.getBrandName(), dto.getCategoryName(), dto.getPrice()));

        List<BrandCategoryDto> dtos = aggregationQueryRepository.findBrandLowestPriceByBrandId(brandId);
        List<BrandLowestPriceInfo> entities = dtos.stream()
            .map(data -> new BrandLowestPriceInfo(data.getBrandId(), data.getCategoryId(), data.getBrandName(), data.getCategoryName(), data.getPrice()))
            .toList();
        saveOneBrandTotalPriceToDatabaseByBrandId(brandId, dtos);
        return entities;
    }

    /**
     * 전달받은 brandId에 해당하는 브랜드의 카테고리별 최저가 정보를 집계하는 로직
     *
     * @return
     */
    @Transactional
    public List<BrandLowestPriceInfo> aggregateAllBrandLowestPriceInfoByBrandId(Long brandId) {
        List<BrandCategoryDto> dtos = aggregationQueryRepository.findBrandLowestPriceByBrandId(brandId);

        if (dtos.isEmpty()) {
            log.error("[aggregate-fail]-lowest-total-price-brand: 브랜드에 관련된 상품 정보가 존재하지 않아 집계에 실패하였습니다.");
            return List.of();
        }

        List<BrandLowestPriceInfo> brandLowestPriceInfoEntities = saveBrandLowestPriceInfosToDatabase(dtos);
        saveOneBrandTotalPriceToDatabaseByBrandId(brandId, dtos);
        return brandLowestPriceInfoEntities;
    }


    @Transactional
    public List<CategoryLowestPriceBrand> aggregateCategoryLowestPriceBrandByCategoryId(Long categoryId) {
        List<CategoryPriceBrandDto> dtos = aggregationQueryRepository.getCategoryLowestPriceBrandDtosByCategoryId(categoryId);

        if (dtos.isEmpty()) {
            log.error("[aggregate-fail]-category-lowest-price: 해당 카테고리에 속한 상품 정보가 존재하지 않아 집계에 실패하였습니다.");
            return List.of();
        }

        return saveCategoryLowestPriceBrandsInDatabase(dtos);
    }

    /**
     * 전체 카테고리에 대해 최저가 상품을 가진 브랜드와 가격 정보를 집계하는 로직
     * <p>
     * 1. 카테고리별로 각 브랜드가 가진 최저가 정보를 조회한다.
     * 2. 각 카테고리별로 최저가를 가진 브랜드를 필터링하여 집계 데이터로 변환 후 repository에 저장.
     * 3. 집계한 정보를 redisCache에 저장한다.
     *
     * @return
     */
    @Transactional
    public List<CategoryLowestPriceBrand> aggregateCategoryLowestPriceBrand() {
        List<CategoryPriceBrandDto> dtos = aggregationQueryRepository.findAllCategoryLowestPriceBrandDtos();

        if (dtos.isEmpty()) {
            log.error("[aggregate-fail]-category-lowest-price: 해당 카테고리에 속한 상품 정보가 존재하지 않아 집계에 실패하였습니다.");
            return List.of();
        }

        return saveCategoryLowestPriceBrandsInDatabase(dtos);
    }

    /**
     * 단일 카테고리에 대해 최고가 상품을 가진 브랜드와 가격 정보를 집계하는 로직
     * <p>
     * 1. 단일 카테고리의 각 브랜드가 가진 최고가 정보를 조회한다.
     * 2. 최고가를 가진 브랜드를 필터링하여 집계 데이터로 변환 후 repository에 저장한다.
     *
     */
    @Transactional
    public List<CategoryHighestPriceBrand> aggregateCategoryHighestPriceBrandByCategoryId(Long categoryId) {
        List<CategoryPriceBrandDto> dtos = aggregationQueryRepository.findCategoryHighestPriceBrandDtosByCategoryId(categoryId);

        if (dtos.isEmpty()) {
            log.error("[aggregate-fail]-category-highest-price: 해당 카테고리에 속한 상품 정보가 존재하지 않아 집계에 실패하였습니다.");
            return List.of();
        }

        return saveCategoryHighestPriceBrandInDatabase(dtos);
    }

    /**
     * 전체 카테고리에 대해 최고가 상품을 가진 브랜드와 가격 정보를 집계하는 로직
     * <p>
     * 1. 카테고리별로 각 브랜드가 가진 최고가 정보를 조회한다.
     * 2. 각 카테고리별로 최고가를 가진 브랜드를 필터링하여 집계 데이터로 변환 후 repository에 저장한다.
     *
     * @return
     */
    @Transactional
    public List<CategoryHighestPriceBrand> aggregateCategoryHighestPriceBrand() {
        List<CategoryPriceBrandDto> dtos = aggregationQueryRepository.findAllCategoryHighestPriceBrandDtos();

        if (dtos.isEmpty()) {
            log.error("[aggregate-fail]-category-highest-price: 해당 카테고리에 속한 상품 정보가 존재하지 않아 집계에 실패하였습니다.");
            return List.of();
        }

        return saveCategoryHighestPriceBrandInDatabase(dtos);
    }

    @Transactional
    public void deleteOneBrandLowestPriceInfoByBrandIdAndCategoryId(Long brandId, Long categoryId) {
        brandLowestPriceInfoRepository.deleteByBrandIdAndCategoryId(brandId, categoryId);
    }

    @Transactional
    public void deleteAllBrandLowestPriceInfoByBrandId(Long brandId) {
        brandLowestPriceInfoRepository.deleteAllByBrandId(brandId);
    }

    @Transactional
    public void deleteAllCategoryLowestPriceBrandByCategoryId(Long categoryId) {
        categoryLowestPriceBrandRepository.deleteAllByCategoryId(categoryId);
    }

    @Transactional
    public void deleteAllCategoryHighestPriceBrandByCategoryId(Long categoryId) {
        categoryHighestPriceBrandRepository.deleteAllByCategoryId(categoryId);
    }

    @Transactional
    public void deleteAllCategoryLowestPriceBrandByBrandId(Long brandId) {
        categoryLowestPriceBrandRepository.deleteAllByBrandId(brandId);
    }

    @Transactional
    public void deleteAllCategoryHighestPriceBrandByBrandId(Long brandId) {
        categoryHighestPriceBrandRepository.deleteAllByBrandId(brandId);
    }

    @Transactional
    public void deleteBrandTotalPriceByBrandId(Long brandId) {
        brandTotalPriceRepository.deleteById(brandId);
    }

    @Transactional
    public List<BrandLowestPriceInfo> saveBrandLowestPriceInfosToDatabase(List<BrandCategoryDto> dtos) {
        List<BrandLowestPriceInfo> brandLowestPriceInfoEntities = dtos.stream()
            .map(dto -> new BrandLowestPriceInfo(dto.getBrandId(), dto.getCategoryId(), dto.getBrandName(), dto.getCategoryName(), dto.getPrice()))
            .toList();
        brandLowestPriceInfoRepository.saveAll(brandLowestPriceInfoEntities);
        return brandLowestPriceInfoEntities;
    }

    private void saveOneBrandTotalPriceToDatabaseByBrandId(Long brandId, List<BrandCategoryDto> dtos) {
        double totalPrice = dtos.stream()
            .mapToDouble(BrandCategoryDto::getPrice)
            .sum();

        brandTotalPriceRepository.save(new BrandTotalPrice(brandId, totalPrice));
    }

    public List<BrandTotalPrice> saveAllBrandsTotalPriceToDatabase(List<BrandCategoryDto> dtos) {
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

        return brandTotalPriceRepository.saveAll(entities);
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
            .map(data -> new CategoryHighestPriceBrand(data.getCategoryId(), data.getCategoryName(), data.getBrandId(), data.getBrandName(), data.getPrice()))
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
            .map(data -> new CategoryLowestPriceBrand(data.getCategoryId(), data.getCategoryName(), data.getBrandId(),  data.getBrandName(), data.getPrice()))
            .toList();

        categoryLowestPriceBrandRepository.saveAll(entities);
        return entities;
    }
}
