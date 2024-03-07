package com.assignment.aggregation.controller;

import com.assignment.aggregation.controller.dto.CategoriesLowestPriceBrandsResponse;
import com.assignment.aggregation.controller.dto.CategoryLowestAndHighestBrandResponse;
import com.assignment.aggregation.controller.dto.LowestTotalPriceBrandResponse;
import com.assignment.aggregation.service.AggregationService;
import com.assignment.common.dto.ApplicationResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "데이터 집계 API")
@Validated
@RequiredArgsConstructor
@RequestMapping("/aggregations")
@RestController
public class AggregationController {

    private final AggregationService aggregationService;

    /**
     * [요구사항 1번 API]
     * 카테고리 별 최저가격 브랜드와 상품 가격, 총액을 조회하는 API
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/categories/lowest-price-brands")
    public ApplicationResponse<CategoriesLowestPriceBrandsResponse> getLowestPriceBrandsByCategories() {
        return ApplicationResponse.success(aggregationService.getCategoriesLowestPriceBrands());
    }

    /**
     * [요구사항 2번 API]
     * 단일 브랜드로 모든 카테고리 상품을 구매할 때 최저가격에 판매하는 브랜드와 카테고리의 상품가격, 총액을 조회하는 API
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/brands/lowest-total-price")
    public ApplicationResponse<LowestTotalPriceBrandResponse> getData() {
        return ApplicationResponse.success(aggregationService.getLowestTotalBrand());
    }

    /**
     * [요구사항 3번 API]
     * 카테고리 이름으로 최저, 최고 가격 브랜드와 상품 가격을 조회하는 API
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/categories/lowest-highest-price-brand")
    public ApplicationResponse<CategoryLowestAndHighestBrandResponse> getData(@RequestParam("categoryName") @NotNull @NotEmpty String categoryName) {
        return ApplicationResponse.success(aggregationService.getCategoryLowestAndHighestPriceBrand(categoryName));
    }

    /**
     * 데이터베이스 및 캐시 저장소의 모든 집계 데이터를 재집계하는 API
     * - 수동으로 집계가 필요할 때 사용하는 API 입니다.
     */
    @Schema(name = "데이터 재집계 API", description = "수동으로 집계 데이터를 최신화할때 사용하는 api입니다.")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ApplicationResponse<Void> aggregateData() {
        aggregationService.aggregateAllDatas();
        return ApplicationResponse.success();
    }
}
