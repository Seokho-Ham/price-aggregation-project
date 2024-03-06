package com.assignment.aggregation.service;

import com.assignment.aggregation.domain.*;
import com.assignment.aggregation.repository.AggregationQueryRepository;
import com.assignment.aggregation.repository.dto.BrandCategoryDto;
import com.assignment.aggregation.repository.dto.CategoryPriceBrandDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

@RequiredArgsConstructor
@Slf4j
@Service
public class AggregationService {

    private final AggregationQueryRepository aggregationQueryRepository;
    private final BrandLowestPriceInfoRepository brandLowestPriceInfoRepository;
    private final CategoryLowestPriceBrandRepository categoryLowestPriceBrandRepository;
    private final CategoryHighestPriceBrandRepository categoryHighestPriceBrandRepository;

    /**
     * 기능: 전달받은 brandId에 해당하는 브랜드의 카테고리별 최저가 정보를 집계하는 로직
     */
    @Transactional
    public void aggregateBrandLowestPriceInfoByBrandId(Long brandId) {
        /*
        1. 해당 브랜드에서 각 카테고리별로 최저가 상품의 가격정보를 조회.
        2. 테이블에 저장 -> 만약 이미 있으면 업데이트됨
         */
        List<BrandCategoryDto> dtos = aggregationQueryRepository.findByBrandLowestPriceByCategoryId(brandId);

        if (dtos.isEmpty()) {
            log.error("[aggregate-brand_category_lowest_price] 정보가 존재하지 않아 집계에 실패.");
            return;
        }

        List<BrandLowestPriceInfo> brandLowestPriceInfoEntities = dtos.stream()
            .map(dto -> new BrandLowestPriceInfo(dto.getBrandId(), dto.getCategoryId(), dto.getBrandName(), dto.getCategoryName(), dto.getPrice()))
            .toList();
        brandLowestPriceInfoRepository.saveAll(brandLowestPriceInfoEntities);
    }

    /**
     * 전체 카테고리에 대해 최저가 상품을 가진 브랜드와 가격 정보를 집계하는 로직
     * 1. 카테고리별로 각 브랜드가 가진 최저가 정보를 조회한다.
     * 2. 조회한 데이터를 categoryId 를 key로 사용하여 map형태로 변경한다.
     * 3. 각 카테고리별로 최저가를 가진 브랜드를 필터링하여 엔티티로 변환한다.
     * 4. repository에 저장한다.
     */
    @Transactional
    public void aggregateCategoryLowestPriceBrand() {

        List<CategoryPriceBrandDto> dtos = aggregationQueryRepository.getCategoryLowestPriceBrandDtos();

        if (dtos.isEmpty()) {
            log.error("");
            return;
        }

        Map<Long, List<CategoryPriceBrandDto>> brandDtoMapByCategory = dtos
            .stream()
            .collect(groupingBy(CategoryPriceBrandDto::getCategoryId));

        List<CategoryLowestPriceBrand> entities = brandDtoMapByCategory.values()
            .stream()
            .map(value -> value.stream()
                .min(Comparator.comparing(CategoryPriceBrandDto::getPrice))
                .get())
            .map(val -> new CategoryLowestPriceBrand(val.getCategoryId(), val.getCategoryName(), val.getBrandId(), val.getBrandName(), val.getPrice()))
            .toList();

        categoryLowestPriceBrandRepository.saveAll(entities);
    }

    /**
     * 전체 카테고리에 대해 최고가 상품을 가진 브랜드와 가격 정보를 집계하는 로직
     * 1. 카테고리별로 각 브랜드가 가진 최고가 정보를 조회한다.
     * 2. 조회한 데이터를 categoryId 를 key로 사용하여 map형태로 변경한다.
     * 3. 각 카테고리별로 최고가를 가진 브랜드를 필터링하여 엔티티로 변환한다.
     * 4. repository에 저장한다.
     */
    @Transactional
    public void aggregateCategoryHighestPriceBrand() {
        List<CategoryPriceBrandDto> dtos = aggregationQueryRepository.getCategoryHighestPriceBrandDtos();

        if (dtos.isEmpty()) {
            log.error("");
            return;
        }

        Map<Long, List<CategoryPriceBrandDto>> brandDtoMapByCategory = dtos
            .stream()
            .collect(groupingBy(CategoryPriceBrandDto::getCategoryId));;

        List<CategoryHighestPriceBrand> entities = brandDtoMapByCategory.values()
            .stream()
            .map(value -> value.stream()
                .max(Comparator.comparing(CategoryPriceBrandDto::getPrice))
                .get())
            .map(val -> new CategoryHighestPriceBrand(val.getCategoryId(), val.getCategoryName(), val.getBrandId(), val.getBrandName(), val.getPrice()))
            .toList();

        categoryHighestPriceBrandRepository.saveAll(entities);
    }

}
