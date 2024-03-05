package com.assignment.aggregation.service;

import com.assignment.aggregation.controller.dto.BrandPriceInfoResponse;
import com.assignment.aggregation.controller.dto.CategoryLowestAndHighestBrandResponse;
import com.assignment.aggregation.domain.*;
import com.assignment.aggregation.exception.CacheNotFoundException;
import com.assignment.category.exception.CategoryNotFoundException;
import com.google.common.collect.Lists;
import com.assignment.aggregation.controller.dto.CategoriesLowestPriceBrandsResponse;
import com.assignment.aggregation.controller.dto.CategoryLowestPriceBrandResponse;
import com.assignment.category.domain.Category;
import com.assignment.category.domain.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
@Service
public class AggregationReaderService {

    private final CategoryRepository categoryRepository;
    private final CategoryLowestPriceBrandRedisRepository categoryLowestPriceBrandRedisRepository;
    private final CategoryHighestPriceBrandRedisRepository categoryHighestPriceBrandRedisRepository;
    private final BrandTotalPriceRedisRepository brandTotalPriceRedisRepository;

    /**
     * 카테고리별 최저가 브랜드와 가격을 조회하는 api
     *
     * @return 카테고리별 최저가 브랜드 정보
     */
    public CategoriesLowestPriceBrandsResponse getLowestPriceBrandsForAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        List<Long> categoryIds = categories.stream().map(Category::getId)
            .toList();

        Iterable<CategoryLowestPriceBrand> lowestPriceBrands = categoryLowestPriceBrandRedisRepository.findAllById(categoryIds);
        if (!lowestPriceBrands.iterator().hasNext()) {
            throw new CacheNotFoundException();
        }

        List<CategoryLowestPriceBrandResponse> result = StreamSupport.stream(lowestPriceBrands.spliterator(), false)
            .map(data -> new CategoryLowestPriceBrandResponse(data.getCategoryName(), data.getLowestPriceBrand().getBrandName(), data.getLowestPriceBrand().getPrice()))
            .toList();
        return new CategoriesLowestPriceBrandsResponse(result);
    }

    /**
     * 특정 카테고리의 최저가, 최고가 브랜드와 가격을 조회하는 api
     *
     * @param categoryName
     * @return 해당 카테고리의 최저가, 최고가 브랜드
     */
    public CategoryLowestAndHighestBrandResponse getLowestHighestPriceBrandForSingleCategory(String categoryName) {
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

    /**
     * 총액이 가장 낮은 브랜드의 정보를 조회하는 api
     *
     * @return 총액 최저가 브랜드
     */
    public BrandTotalPrice getLowestTotalPriceBrand() {
        List<BrandTotalPrice> brands = Lists.newArrayList(brandTotalPriceRedisRepository.findAll());
        return brands.stream()
            .min(Comparator.comparing(BrandTotalPrice::getTotalPrice))
            .orElseThrow(CacheNotFoundException::new);
    }


}
