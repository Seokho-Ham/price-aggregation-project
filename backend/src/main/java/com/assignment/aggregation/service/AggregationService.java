package com.assignment.aggregation.service;

import com.assignment.aggregation.controller.dto.CategoryPriceResponse;
import com.assignment.aggregation.controller.dto.LowestTotalPriceBrandResponse;
import com.assignment.aggregation.controller.dto.TotalPriceBrandResponse;
import com.assignment.aggregation.domain.*;
import com.assignment.aggregation.exception.LowestTotalPriceBrandNotFoundException;
import com.assignment.aggregation.repository.AggregationCacheRepository;
import com.assignment.aggregation.repository.AggregationQueryRepository;
import com.assignment.item.service.dto.ItemDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AggregationService {

    private final AggregationWriter aggregationWriter;
    private final AggregationCacheWriter aggregationCacheWriter;
    private final AggregationCacheRepository aggregationCacheRepository;
    private final BrandLowestPriceInfoRepository brandLowestPriceInfoRepository;
    private final CategoryLowestPriceBrandRepository categoryLowestPriceBrandRepository;
    private final CategoryHighestPriceBrandRepository categoryHighestPriceBrandRepository;
    private final AggregationQueryRepository aggregationQueryRepository;

    public LowestTotalPriceBrandResponse getLowestTotalBrand() {
        List<BrandLowestPriceInfo> brandLowestPriceData = findBrandLowestPriceInfos();

        if (brandLowestPriceData.isEmpty()) {
            throw new LowestTotalPriceBrandNotFoundException();
        }

        String brandName = brandLowestPriceData.stream().map(BrandLowestPriceInfo::getBrandName).findFirst().get();
        Double totalPrice = brandLowestPriceData.stream().mapToDouble(BrandLowestPriceInfo::getPrice).sum();
        List<CategoryPriceResponse> categories = brandLowestPriceData.stream()
            .map(data -> new CategoryPriceResponse(data.getCategoryName(), data.getPrice()))
            .toList();

        return new LowestTotalPriceBrandResponse(new TotalPriceBrandResponse(brandName, categories, totalPrice));
    }

    public void aggregateOnBrandCreate(Long brandId) {
        aggregationWriter.aggregateAllBrandLowestPriceInfoByBrandId(brandId);
    }

    public void aggregateOnBrandUpdate(Long brandId) {
        aggregationWriter.deleteAllBrandLowestPriceInfoByBrandId(brandId);

        List<BrandLowestPriceInfo> brandLowestPriceInfos = aggregationWriter.aggregateAllBrandLowestPriceInfoByBrandId(brandId);
        if (ObjectUtils.isNotEmpty(brandLowestPriceInfos)) {
            aggregationCacheWriter.saveAggregatedBrandDataInCache(brandLowestPriceInfos);
        }

        aggregateCategoryLowestPriceBrandForAllCategories();
        aggregateCategoryHighestPriceBrandForAllCategories();
    }

    public void aggregateOnBrandDelete(Long brandId) {
        aggregationWriter.deleteBrandTotalPriceByBrandId(brandId);
        aggregationWriter.deleteAllBrandLowestPriceInfoByBrandId(brandId);
        aggregationWriter.deleteAllCategoryLowestPriceBrandByBrandId(brandId);
        aggregationWriter.deleteAllCategoryHighestPriceBrandByBrandId(brandId);

        aggregationCacheWriter.deleteBrandTotalPriceByBrandId(brandId);
        aggregationCacheWriter.deleteAllBrandLowestPriceInfoByBrandId(brandId);
        aggregationCacheWriter.deleteAllCategoryLowestPriceBrandByBrandId(brandId);
        aggregationCacheWriter.deleteAllCategoryHighestPriceBrandByBrandId(brandId);

        aggregateCategoryLowestPriceBrandForAllCategories();
        aggregateCategoryHighestPriceBrandForAllCategories();
    }

    /**
     * 상품 정보가 추가 됐을때 데이터를 재집계하는 기능입니다.
     * <p/>
     * <p>
     * [카테고리의 최저가, 최고가 갱신 로직]
     * 1. 상품이 속한 카테고리의 최저가, 최고가 정보를 조회한다.
     * 2. 조회한 결과에서 아래 조건을 검사한다.
     * - 최저가: (해당 상품의 가격 <= 현시점 최저가)
     * - 최고가: (해당 상품의 가격 >= 현시점 최고가)
     * 3. 조건에 해당하면 해당 카테고리에 대해 데이터를 삭제 후 재집계한다.
     * <p>
     * [브랜드의 카테고리별 최저가 갱신 로직]
     * 1. 상품이 속한 브랜드의 해당 카테고리의 최저가 정보를 조회한다.
     * 2. 다음 조건을 검사한다.
     * - 해당 상품의 가격 < 브랜드의 해당 카테고리의 최저가
     * 3. 조건에 해당하면 상품이 속한 브랜드의 카테고리의 최저가 정보를 삭제 후 재집계한다.
     */
    public void aggregateOnItemCreate(ItemDto itemDto) {
        List<CategoryLowestPriceBrand> categoryLowestPriceBrands = categoryLowestPriceBrandRepository.findAllByCategoryId(itemDto.getCategoryId());
        if (categoryLowestPriceBrands.stream().anyMatch(brand -> brand.getPrice() >= itemDto.getPrice())) {
            aggregationWriter.deleteAllCategoryLowestPriceBrandByCategoryId(itemDto.getCategoryId());
            aggregateCategoryLowestPriceBrandForSingleCategory(itemDto.getCategoryId());
        }

        List<CategoryHighestPriceBrand> categoryHighestPriceBrands = categoryHighestPriceBrandRepository.findAllByCategoryId(itemDto.getCategoryId());
        if (categoryHighestPriceBrands.stream().anyMatch(brand -> brand.getPrice() <= itemDto.getPrice())) {
            aggregationWriter.deleteAllCategoryHighestPriceBrandByCategoryId(itemDto.getCategoryId());
            aggregateCategoryHighestPriceBrandForSingleCategory(itemDto.getCategoryId());
        }

        Optional<BrandLowestPriceInfo> brandLowestPriceInfo = brandLowestPriceInfoRepository.findByBrandIdAndCategoryId(itemDto.getBrandId(), itemDto.getCategoryId());
        brandLowestPriceInfo.ifPresentOrElse(
            info -> {
                if (itemDto.getPrice() < info.getPrice()) {
                    aggregationWriter.deleteOneBrandLowestPriceInfoByBrandIdAndCategoryId(itemDto.getBrandId(), itemDto.getCategoryId());
                    List<BrandLowestPriceInfo> brandLowestPriceInfos = aggregationWriter.aggregateOneBrandLowestPriceInfoByBrandIdAndCategoryId(itemDto.getBrandId(), itemDto.getCategoryId());
                    aggregationCacheWriter.saveAggregatedBrandDataInCache(brandLowestPriceInfos);
                }
            },
            () -> {
                List<BrandLowestPriceInfo> brandLowestPriceInfos = aggregationWriter.aggregateOneBrandLowestPriceInfoByBrandIdAndCategoryId(itemDto.getBrandId(), itemDto.getCategoryId());
                aggregationCacheWriter.saveAggregatedBrandDataInCache(brandLowestPriceInfos);
            }
        );

    }

    /**
     * 상품 정보가 업데이트 됐을때 데이터를 재집계하는 기능입니다.
     * <p/>
     * <p>
     * [카테고리의 최저가, 최고가 갱신 로직]
     * 1. 상품이 속한 카테고리의 최저가, 최고가 정보를 삭제한다.
     * 2. 해당 카테고리에 대한 데이터를 삭제 후 재집계한다.
     * <p>
     * [브랜드의 카테고리별 최저가 갱신 로직]
     * 1. 상품이 속한 브랜드의 해당 카테고리의 최저가 정보를 삭제한다.
     * 2. 상품이 속한 브랜드의 카테고리의 최저가 정보를 재집계한다.
     */
    public void aggregateOnItemUpdate(ItemDto itemDto) {

        aggregationWriter.deleteAllCategoryLowestPriceBrandByCategoryId(itemDto.getCategoryId());
        aggregateCategoryLowestPriceBrandForSingleCategory(itemDto.getCategoryId());

        aggregationWriter.deleteAllCategoryHighestPriceBrandByCategoryId(itemDto.getCategoryId());
        aggregateCategoryHighestPriceBrandForSingleCategory(itemDto.getCategoryId());

        aggregationWriter.deleteOneBrandLowestPriceInfoByBrandIdAndCategoryId(itemDto.getBrandId(), itemDto.getCategoryId());
        List<BrandLowestPriceInfo> brandLowestPriceInfos = aggregationWriter.aggregateOneBrandLowestPriceInfoByBrandIdAndCategoryId(itemDto.getBrandId(), itemDto.getCategoryId());
        if (ObjectUtils.isEmpty(brandLowestPriceInfos)) {
            brandLowestPriceInfos = aggregationWriter.aggregateAllBrandLowestPriceInfoByBrandId(itemDto.getBrandId());
        }
        aggregationCacheWriter.saveAggregatedBrandDataInCache(brandLowestPriceInfos);

    }


    /**
     * 상품 정보가 삭제 됐을때 데이터를 재집계하는 기능입니다.
     * <p/>
     * <p>
     * [카테고리의 최저가, 최고가 갱신 로직]
     * 1. 상품이 속한 카테고리의 최저가, 최고가 정보를 조회한다.
     * 2. 조회한 결과에서 아래 조건을 검사한다.
     * - 최저가: (해당 상품의 가격 == 현시점 최저가)
     * - 최고가: (해당 상품의 가격 == 현시점 최고가)
     * 3. 조건에 해당하면 해당 상품이 속한 브랜드Id를 바탕으로 데이터를 삭제 후 재집계한다.
     * <p>
     * [브랜드의 카테고리별 최저가 갱신 로직]
     * 1. 상품이 속한 브랜드의 해당 카테고리의 최저가 정보를 조회한다.
     * 2. 다음 조건을 검사한다.
     * - 해당 상품의 가격 == 브랜드의 해당 카테고리의 최저가
     * 3. 조건에 해당하면 상품이 속한 브랜드의 카테고리의 최저가 정보를 삭제 후 재집계한다.
     */
    public void aggregateOnItemDelete(ItemDto itemDto) {

        List<CategoryLowestPriceBrand> categoryLowestPriceBrands = categoryLowestPriceBrandRepository.findAllByCategoryId(itemDto.getCategoryId());
        if (categoryLowestPriceBrands.stream().anyMatch(data -> data.getPrice().equals(itemDto.getPrice()))) {
            aggregationWriter.deleteAllCategoryLowestPriceBrandByBrandId(itemDto.getBrandId());
            aggregateCategoryLowestPriceBrandForSingleCategory(itemDto.getCategoryId());
        }

        List<CategoryHighestPriceBrand> categoryHighestPriceBrands = categoryHighestPriceBrandRepository.findAllByCategoryId(itemDto.getCategoryId());
        if (categoryHighestPriceBrands.stream().anyMatch(data -> data.getPrice().equals(itemDto.getPrice()))) {
            aggregationWriter.deleteAllCategoryHighestPriceBrandByBrandId(itemDto.getBrandId());
            aggregateCategoryHighestPriceBrandForSingleCategory(itemDto.getCategoryId());
        }

        Optional<BrandLowestPriceInfo> brandLowestPriceInfo = brandLowestPriceInfoRepository.findByBrandIdAndCategoryId(itemDto.getBrandId(), itemDto.getCategoryId());
        if (brandLowestPriceInfo.isPresent() && itemDto.getPrice().equals(brandLowestPriceInfo.get().getPrice())) {
            aggregationWriter.deleteOneBrandLowestPriceInfoByBrandIdAndCategoryId(itemDto.getBrandId(), itemDto.getCategoryId());
        }
        List<BrandLowestPriceInfo> brandLowestPriceInfos = aggregationWriter.aggregateOneBrandLowestPriceInfoByBrandIdAndCategoryId(itemDto.getBrandId(), itemDto.getCategoryId());
        if (ObjectUtils.isEmpty(brandLowestPriceInfos)) {
            brandLowestPriceInfos = aggregationWriter.aggregateAllBrandLowestPriceInfoByBrandId(itemDto.getBrandId());
        }
        aggregationCacheWriter.saveAggregatedBrandDataInCache(brandLowestPriceInfos);

    }

    private void aggregateCategoryLowestPriceBrandForSingleCategory(Long categoryId) {
        List<CategoryLowestPriceBrand> entities = aggregationWriter.aggregateCategoryLowestPriceBrandByCategoryId(categoryId);
        if (ObjectUtils.isNotEmpty(entities)) {
            aggregationCacheWriter.saveAggregatedCategoryLowestPriceBrandsInCache(entities);
        }
    }

    private void aggregateCategoryLowestPriceBrandForAllCategories() {
        List<CategoryLowestPriceBrand> categoryLowestPriceBrands = aggregationWriter.aggregateCategoryLowestPriceBrand();
        if (ObjectUtils.isNotEmpty(categoryLowestPriceBrands)) {
            aggregationCacheWriter.saveAggregatedCategoryLowestPriceBrandsInCache(categoryLowestPriceBrands);
        }
    }

    private void aggregateCategoryHighestPriceBrandForSingleCategory(Long categoryId) {
        List<CategoryHighestPriceBrand> entities = aggregationWriter.aggregateCategoryHighestPriceBrandByCategoryId(categoryId);
        if (ObjectUtils.isNotEmpty(entities)) {
            aggregationCacheWriter.saveAggregatedCategoryHighestPriceBrandsInCache(entities);
        }
    }

    private void aggregateCategoryHighestPriceBrandForAllCategories() {
        List<CategoryHighestPriceBrand> categoryHighestPriceBrands = aggregationWriter.aggregateCategoryHighestPriceBrand();
        if (ObjectUtils.isNotEmpty(categoryHighestPriceBrands)) {
            aggregationCacheWriter.saveAggregatedCategoryHighestPriceBrandsInCache(categoryHighestPriceBrands);
        }
    }

    private List<BrandLowestPriceInfo> findBrandLowestPriceInfos() {
        return aggregationCacheRepository.getLowestTotalPriceBrand()
            .orElseGet(() -> {
                BrandTotalPrice brandPrice = aggregationQueryRepository.getLowestTotalPriceBrand();
                return brandLowestPriceInfoRepository.findAllByBrandIdOrderById(brandPrice.getBrandId());
            });
    }


}
