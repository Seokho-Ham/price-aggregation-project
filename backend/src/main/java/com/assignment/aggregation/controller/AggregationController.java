package com.assignment.aggregation.controller;

import com.assignment.aggregation.controller.dto.CategoryLowestAndHighestBrandResponse;
import com.assignment.aggregation.controller.dto.CategoriesLowestPriceBrandsResponse;
import com.assignment.aggregation.domain.BrandTotalPrice;
import com.assignment.aggregation.service.AggregationReaderService;
import com.assignment.aggregation.service.AggregationService;
import com.assignment.common.dto.ApplicationResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "데이터 집계 API")
@RequiredArgsConstructor
@RequestMapping("/aggregations")
@RestController
public class AggregationController {

    private final AggregationService aggregationService;
    private final AggregationReaderService aggregationReaderService;

    @Schema(name = "단일 카테고리 최저가, 최고가 브랜드 정보 조회 API")
    @GetMapping("/categories/{categoryName}/lowest-highest-price-brand")
    public ApplicationResponse<CategoryLowestAndHighestBrandResponse> getData(@PathVariable String categoryName) {
        CategoryLowestAndHighestBrandResponse response = aggregationReaderService.getLowestHighestPriceBrandForSingleCategory(categoryName);
        return ApplicationResponse.success(response);
    }

    @Schema(name = "카테고리별 최저가 브랜드 정보 조회 API")
    @GetMapping("/categories/lowest-price-brands")
    public ApplicationResponse<CategoriesLowestPriceBrandsResponse> getLowestPriceBrandsByCategories() {
        CategoriesLowestPriceBrandsResponse lowestPriceBrands = aggregationReaderService.getLowestPriceBrandsForAllCategories();
        return ApplicationResponse.success(lowestPriceBrands);
    }

    @Schema(name = "상품 총액 최저가 브랜드 정보 조회 API")
    @GetMapping("/brands/lowest-total-price")
    public ApplicationResponse<BrandTotalPrice> getData() {
        return ApplicationResponse.success(aggregationReaderService.getLowestTotalPriceBrand());
    }

    @Schema(name = "데이터 재집계 API", description = "수동으로 집계 데이터를 최신화할때 사용하는 api입니다.")
    @PostMapping
    public ApplicationResponse<Void> aggregateData() {
        aggregationService.reaggregateAllData();
        return ApplicationResponse.success();
    }
}
