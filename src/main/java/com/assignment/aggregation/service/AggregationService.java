package com.assignment.aggregation.service;

import com.assignment.aggregation.controller.dto.*;
import com.assignment.aggregation.domain.*;
import com.assignment.aggregation.exception.CategoryHighestPriceBrandNotFoundException;
import com.assignment.aggregation.exception.CategoryLowestPriceBrandNotFoundException;
import com.assignment.aggregation.exception.LowestTotalPriceBrandNotFoundException;
import com.assignment.aggregation.repository.AggregationCacheRepository;
import com.assignment.aggregation.repository.AggregationQueryRepository;
import com.assignment.aggregation.repository.dto.BrandCategoryDto;
import com.assignment.category.domain.Category;
import com.assignment.category.domain.CategoryRepository;
import com.assignment.category.exception.CategoryNotFoundException;
import com.assignment.item.service.dto.ItemDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.groupingBy;

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
    private final CategoryRepository categoryRepository;

    public LowestTotalPriceBrandResponse getLowestTotalBrand() {
        List<BrandLowestPriceInfo> brandLowestPriceData = findBrandLowestPriceInfos();

        if (ObjectUtils.isEmpty(brandLowestPriceData)) {
            throw new LowestTotalPriceBrandNotFoundException();
        }

        String brandName = brandLowestPriceData.stream().map(BrandLowestPriceInfo::getBrandName).findFirst().get();
        Double totalPrice = brandLowestPriceData.stream().mapToDouble(BrandLowestPriceInfo::getPrice).sum();
        List<CategoryPriceResponse> categories = brandLowestPriceData.stream()
            .map(data -> new CategoryPriceResponse(data.getCategoryName(), data.getPrice()))
            .toList();

        return new LowestTotalPriceBrandResponse(new TotalPriceBrandResponse(brandName, categories, totalPrice));
    }

    public CategoryLowestAndHighestBrandResponse getCategoryLowestAndHighestPriceBrand(String categoryName) {
        Category category = categoryRepository.findByName(categoryName)
            .orElseThrow(CategoryNotFoundException::new);
        List<CategoryLowestPriceBrand> lowestPriceBrands = findCategoryLowestPriceBrands(category.getId());
        List<CategoryHighestPriceBrand> highestPriceBrands = findCategoryHighestPriceBrands(category.getId());

        if (ObjectUtils.isEmpty(lowestPriceBrands)) {
            throw new CategoryLowestPriceBrandNotFoundException();
        }
        if(ObjectUtils.isEmpty(highestPriceBrands)) {
            throw new CategoryHighestPriceBrandNotFoundException();
        }

        List<BrandPriceInfoResponse> lowestPriceBrandResponses = lowestPriceBrands.stream()
            .map(brand -> new BrandPriceInfoResponse(brand.getBrandName(), brand.getPrice()))
            .toList();

        List<BrandPriceInfoResponse> highestPriceBrandResponses = highestPriceBrands.stream()
            .map(brand -> new BrandPriceInfoResponse(brand.getBrandName(), brand.getPrice()))
            .toList();

        return new CategoryLowestAndHighestBrandResponse(category.getName(), lowestPriceBrandResponses, highestPriceBrandResponses);
    }

    public CategoriesLowestPriceBrandsResponse getCategoriesLowestPriceBrands() {
        List<CategoryLowestPriceBrand> result = findCategoryLowestPriceBrands();

        if (ObjectUtils.isEmpty(result)) {
            throw new CategoryLowestPriceBrandNotFoundException();
        }

        List<CategoryLowestPriceBrandResponse> lowestPriceBrandResponses = result.stream()
            .collect(groupingBy(CategoryLowestPriceBrand::getCategoryName))
            .entrySet()
            .stream()
            .map(entry -> {
                String categoryName = entry.getKey();
                List<String> brandNames = entry.getValue().stream()
                    .map(CategoryLowestPriceBrand::getBrandName)
                    .toList();
                double price = entry.getValue().stream()
                    .mapToDouble(CategoryLowestPriceBrand::getPrice)
                    .findFirst()
                    .orElse(0D);
                return new CategoryLowestPriceBrandResponse(categoryName, brandNames, price);
            })
            .toList();

        double totalPrice = lowestPriceBrandResponses.stream()
            .mapToDouble(CategoryLowestPriceBrandResponse::getPrice)
            .sum();

        return new CategoriesLowestPriceBrandsResponse(lowestPriceBrandResponses, totalPrice);
    }

    /**
     * 브랜드 정보가 추가 됐을때 데이터를 재집계하는 기능입니다.
     *
     * 1. 추가된 브랜드의 정보가 담긴 브랜드 최저가 정보를 집계한다.
     *
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void aggregateOnBrandCreate(Long brandId) {
        aggregationWriter.aggregateBrandLowestPriceInfoForOneBrandAndAllCategories(brandId);
    }

    /**
     * 브랜드 정보가 업데이트 됐을때 데이터를 재집계하는 기능입니다.
     *
     * 1. 업데이트 브랜드의 정보가 담긴 브랜드 최저가 정보를 재집계 후 기존 데이터를 삭제한다.
     * 2. 카테고리별 최저가, 최고가 데이터를 재집계한다.
     *
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void aggregateOnBrandUpdate(Long brandId) {
        List<BrandLowestPriceInfo> originalData = brandLowestPriceInfoRepository.findAllByBrandIdOrderById(brandId);

        List<BrandLowestPriceInfo> brandLowestPriceInfos = aggregationWriter.aggregateBrandLowestPriceInfoForOneBrandAndAllCategories(brandId);
        if (ObjectUtils.isNotEmpty(brandLowestPriceInfos)) {
            aggregationCacheWriter.saveAggregatedBrandDataInCache(brandLowestPriceInfos);
        }
        aggregationWriter.deleteAllOriginalBrandLowestPriceInfoData(originalData);

        List<CategoryLowestPriceBrand> originalLowestPriceBrands = categoryLowestPriceBrandRepository.findAll();
        aggregateCategoryLowestPriceBrandForAllCategories();
        categoryLowestPriceBrandRepository.deleteAll(originalLowestPriceBrands);

        List<CategoryHighestPriceBrand> originalHighestPriceBrands = categoryHighestPriceBrandRepository.findAll();
        aggregateCategoryHighestPriceBrandForAllCategories();
        categoryHighestPriceBrandRepository.deleteAll(originalHighestPriceBrands);
    }

    /**
     * 브랜드 정보가 삭제 됐을때 데이터를 재집계하는 기능입니다.
     *
     * 1. 삭제된 브랜드의 정보가 담긴 데이터를 DB와 캐시로부터 삭제한다.
     * 2. 카테고리별 최저가, 최고가 데이터를 재집계한다.
     *
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
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
     * 3. 조건에 해당하면 해당 카테고리에 대해 데이터를 재집계 후 기존 데이터를 삭제한다..
     * <p>
     * [브랜드의 카테고리별 최저가 갱신 로직]
     * 1. 상품이 속한 브랜드의 해당 카테고리의 최저가 정보를 조회한다.
     * 2. 다음 조건을 검사한다.
     * - 해당 상품의 가격 < 브랜드의 해당 카테고리의 최저가
     * 3. 조건에 해당하면 상품이 속한 브랜드의 카테고리의 최저가 정보를 재집계 후 기존 데이터를 삭제한다.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void aggregateOnItemCreate(ItemDto itemDto) {
        List<CategoryLowestPriceBrand> originalLowestPriceBrands = categoryLowestPriceBrandRepository.findAllByCategoryId(itemDto.getCategoryId());
        if (originalLowestPriceBrands.stream().anyMatch(brand -> brand.getPrice() >= itemDto.getPrice())) {
            aggregateCategoryLowestPriceBrandForSingleCategory(itemDto.getCategoryId());
            aggregationWriter.deleteAllOriginalCategoryLowestPriceBrands(originalLowestPriceBrands);
        }

        List<CategoryHighestPriceBrand> originalHighestPriceBrands = categoryHighestPriceBrandRepository.findAllByCategoryId(itemDto.getCategoryId());
        if (originalHighestPriceBrands.stream().anyMatch(brand -> brand.getPrice() <= itemDto.getPrice())) {
            aggregateCategoryHighestPriceBrandForSingleCategory(itemDto.getCategoryId());
            aggregationWriter.deleteAllOriginalCategoryHighestPriceBrands(originalHighestPriceBrands);
        }

        Optional<BrandLowestPriceInfo> brandLowestPriceInfo = brandLowestPriceInfoRepository.findByBrandIdAndCategoryId(itemDto.getBrandId(), itemDto.getCategoryId());
        brandLowestPriceInfo.ifPresentOrElse(
            info -> {
                if (itemDto.getPrice() < info.getPrice()) {
                    List<BrandLowestPriceInfo> brandLowestPriceInfos = aggregationWriter.aggregateBrandLowestPriceInfoForOneBrandAndOneCategory(itemDto.getBrandId(), itemDto.getCategoryId());
                    aggregationCacheWriter.saveAggregatedBrandDataInCache(brandLowestPriceInfos);
                    aggregationWriter.deleteOriginalBrandLowestPriceInfo(brandLowestPriceInfo.get());
                }
            },
            () -> {
                List<BrandLowestPriceInfo> brandLowestPriceInfos = aggregationWriter.aggregateBrandLowestPriceInfoForOneBrandAndOneCategory(itemDto.getBrandId(), itemDto.getCategoryId());
                aggregationCacheWriter.saveAggregatedBrandDataInCache(brandLowestPriceInfos);
            }
        );

    }

    /**
     * 상품 정보가 업데이트 됐을때 데이터를 재집계하는 기능입니다.
     * <p/>
     * <p>
     * [카테고리의 최저가, 최고가 갱신 로직]
     * 1. 상품이 속한 카테고리의 최저가, 최고가 정보를 재집계한다.
     * 2. 해당 카테고리에 대한 기존 데이터를 삭제한다.
     * <p>
     * [브랜드의 카테고리별 최저가 갱신 로직]
     * 1. 상품이 속한 브랜드의 카테고리의 최저가 정보를 재집계한다.
     * 2. 상품이 속한 브랜드의 해당 카테고리의 기존 최저가 정보를 삭제한다.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void aggregateOnItemUpdate(ItemDto itemDto) {

        List<CategoryLowestPriceBrand> originalLowestPriceBrands = categoryLowestPriceBrandRepository.findAllByCategoryId(itemDto.getCategoryId());
        aggregateCategoryLowestPriceBrandForSingleCategory(itemDto.getCategoryId());
        aggregationWriter.deleteAllOriginalCategoryLowestPriceBrands(originalLowestPriceBrands);

        List<CategoryHighestPriceBrand> originalHighestPriceBrands = categoryHighestPriceBrandRepository.findAllByCategoryId(itemDto.getCategoryId());
        aggregateCategoryHighestPriceBrandForSingleCategory(itemDto.getCategoryId());
        aggregationWriter.deleteAllOriginalCategoryHighestPriceBrands(originalHighestPriceBrands);

        Optional<BrandLowestPriceInfo> originalBrandLowestPriceInfo = brandLowestPriceInfoRepository.findByBrandIdAndCategoryId(itemDto.getBrandId(), itemDto.getCategoryId());
        List<BrandLowestPriceInfo> brandLowestPriceInfos = aggregationWriter.aggregateBrandLowestPriceInfoForOneBrandAndOneCategory(itemDto.getBrandId(), itemDto.getCategoryId());
        if (ObjectUtils.isEmpty(brandLowestPriceInfos)) {
            brandLowestPriceInfos = aggregationWriter.aggregateBrandLowestPriceInfoForOneBrandAndAllCategories(itemDto.getBrandId());
        }
        aggregationCacheWriter.saveAggregatedBrandDataInCache(brandLowestPriceInfos);
        originalBrandLowestPriceInfo.ifPresent(aggregationWriter::deleteOriginalBrandLowestPriceInfo);

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
     * 3. 조건에 해당하면 해당 상품이 속한 브랜드Id를 바탕으로 데이터를 재집계 후 기존 데이터를 삭제한다.
     * <p>
     * [브랜드의 카테고리별 최저가 갱신 로직]
     * 1. 상품이 속한 브랜드의 해당 카테고리의 최저가 정보를 조회한다.
     * 2. 다음 조건을 검사한다.
     *      - 해당 상품의 가격 == 브랜드의 해당 카테고리의 최저가
     * 3. 조건에 해당하면 상품이 속한 브랜드의 카테고리의 최저가 정보를 재집계 후 기존 데이터를 삭제한다.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void aggregateOnItemDelete(ItemDto itemDto) {

        List<CategoryLowestPriceBrand> originalLowestPriceBrands = categoryLowestPriceBrandRepository.findAllByCategoryId(itemDto.getCategoryId());
        if (originalLowestPriceBrands.stream().anyMatch(data -> data.getPrice().equals(itemDto.getPrice()))) {
            aggregateCategoryLowestPriceBrandForSingleCategory(itemDto.getCategoryId());
            aggregationWriter.deleteAllOriginalCategoryLowestPriceBrands(originalLowestPriceBrands);
        }

        List<CategoryHighestPriceBrand> originalHighestPriceBrands = categoryHighestPriceBrandRepository.findAllByCategoryId(itemDto.getCategoryId());
        if (originalHighestPriceBrands.stream().anyMatch(data -> data.getPrice().equals(itemDto.getPrice()))) {
            aggregateCategoryHighestPriceBrandForSingleCategory(itemDto.getCategoryId());
            aggregationWriter.deleteAllOriginalCategoryHighestPriceBrands(originalHighestPriceBrands);
        }

        Optional<BrandLowestPriceInfo> originalBrandLowestPriceInfo = brandLowestPriceInfoRepository.findByBrandIdAndCategoryId(itemDto.getBrandId(), itemDto.getCategoryId());
        List<BrandLowestPriceInfo> brandLowestPriceInfos = aggregationWriter.aggregateBrandLowestPriceInfoForOneBrandAndOneCategory(itemDto.getBrandId(), itemDto.getCategoryId());
        if (ObjectUtils.isEmpty(brandLowestPriceInfos)) {
            brandLowestPriceInfos = aggregationWriter.aggregateBrandLowestPriceInfoForOneBrandAndAllCategories(itemDto.getBrandId());
        }
        aggregationCacheWriter.saveAggregatedBrandDataInCache(brandLowestPriceInfos);
        originalBrandLowestPriceInfo.ifPresent(aggregationWriter::deleteOriginalBrandLowestPriceInfo);


    }

    /**
     * 전체 집계 데이터를 재생성할때 실행하는 기능입니다.(수동으로 데이터를 최신화해야할때만 사용합니다.)
     *
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void aggregateAllDatas() {
        List<BrandCategoryDto> dtos = aggregationQueryRepository.getAllBrandData();

        if (dtos.isEmpty()) {
            log.error("[aggregate-fail]-category-lowest-price: 해당 카테고리에 속한 상품 정보가 존재하지 않아 집계에 실패하였습니다.");
            return;
        }

        List<BrandLowestPriceInfo> brandLowestPriceInfos = aggregationWriter.aggregateAllBrandLowestPriceInfosToDatabase(dtos);
        List<CategoryLowestPriceBrand> categoryLowestPriceBrands = aggregationWriter.aggregateCategoryLowestPriceBrandForAllCategories();
        List<CategoryHighestPriceBrand> categoryHighestPriceBrands = aggregationWriter.aggregateCategoryHighestPriceBrandForAllCategories();

        aggregationCacheWriter.saveAggregatedBrandDataInCache(brandLowestPriceInfos);
        aggregationCacheWriter.saveAggregatedCategoryLowestPriceBrandsInCache(categoryLowestPriceBrands);
        aggregationCacheWriter.saveAggregatedCategoryHighestPriceBrandsInCache(categoryHighestPriceBrands);
    }

    private void aggregateCategoryLowestPriceBrandForSingleCategory(Long categoryId) {
        List<CategoryLowestPriceBrand> entities = aggregationWriter.aggregateCategoryLowestPriceBrandForCategory(categoryId);
        if (ObjectUtils.isNotEmpty(entities)) {
            aggregationCacheWriter.saveAggregatedCategoryLowestPriceBrandsInCache(entities);
        }
    }

    private void aggregateCategoryLowestPriceBrandForAllCategories() {
        List<CategoryLowestPriceBrand> categoryLowestPriceBrands = aggregationWriter.aggregateCategoryLowestPriceBrandForAllCategories();
        if (ObjectUtils.isNotEmpty(categoryLowestPriceBrands)) {
            aggregationCacheWriter.saveAggregatedCategoryLowestPriceBrandsInCache(categoryLowestPriceBrands);
        }
    }

    private void aggregateCategoryHighestPriceBrandForSingleCategory(Long categoryId) {
        List<CategoryHighestPriceBrand> entities = aggregationWriter.aggregateCategoryHighestPriceBrandForCategory(categoryId);
        if (ObjectUtils.isNotEmpty(entities)) {
            aggregationCacheWriter.saveAggregatedCategoryHighestPriceBrandsInCache(entities);
        }
    }

    private void aggregateCategoryHighestPriceBrandForAllCategories() {
        List<CategoryHighestPriceBrand> categoryHighestPriceBrands = aggregationWriter.aggregateCategoryHighestPriceBrandForAllCategories();
        if (ObjectUtils.isNotEmpty(categoryHighestPriceBrands)) {
            aggregationCacheWriter.saveAggregatedCategoryHighestPriceBrandsInCache(categoryHighestPriceBrands);
        }
    }

    private List<BrandLowestPriceInfo> findBrandLowestPriceInfos() {
        return aggregationCacheRepository.findLowestTotalPriceBrand()
            .orElseGet(() -> {
                BrandTotalPrice brandPrice = aggregationQueryRepository.getLowestTotalPriceBrand();
                if (ObjectUtils.isEmpty(brandPrice)) {
                    return null;
                }
                return brandLowestPriceInfoRepository.findAllByBrandIdOrderById(brandPrice.getBrandId());
            });
    }

    private List<CategoryLowestPriceBrand> findCategoryLowestPriceBrands(Long categoryId) {
        return aggregationCacheRepository.findCategoryLowestPriceBrands(String.valueOf(categoryId))
            .orElseGet(() -> categoryLowestPriceBrandRepository.findAllByCategoryId(categoryId));
    }

    private List<CategoryHighestPriceBrand> findCategoryHighestPriceBrands(Long categoryId) {
        return aggregationCacheRepository.findCategoryHighestPriceBrands(String.valueOf(categoryId))
            .orElseGet(() -> categoryHighestPriceBrandRepository.findAllByCategoryId(categoryId));
    }


    private List<CategoryLowestPriceBrand> findCategoryLowestPriceBrands() {
        return aggregationCacheRepository.findAllCategoriesLowestPriceBrands()
            .orElseGet(categoryLowestPriceBrandRepository::findAll);
    }



}
