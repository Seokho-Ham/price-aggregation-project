package com.assignment.aggregation.controller;

import com.assignment.aggregation.controller.dto.CategoriesLowestPriceBrandsResponse;
import com.assignment.aggregation.controller.dto.CategoryLowestAndHighestBrandResponse;
import com.assignment.aggregation.controller.dto.LowestTotalPriceBrandResponse;
import com.assignment.aggregation.service.AggregationService;
import com.assignment.common.dto.ApplicationResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
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

    @Schema(name = "단일 카테고리 최저가, 최고가 브랜드 정보 조회 API")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/categories/{categoryName}/lowest-highest-price-brand")
    public ApplicationResponse<CategoryLowestAndHighestBrandResponse> getData(@PathVariable @NotEmpty String categoryName) {
        return ApplicationResponse.success(aggregationService.getCategoryLowestAndHighestPriceBrand(categoryName));
    }

    @Schema(name = "카테고리별 최저가 브랜드 정보 조회 API")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/categories/lowest-price-brands")
    public ApplicationResponse<CategoriesLowestPriceBrandsResponse> getLowestPriceBrandsByCategories() {
        return ApplicationResponse.success(aggregationService.getCategoriesLowestPriceBrands());
    }

    @Schema(name = "상품 총액 최저가 브랜드 정보 조회 API")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/brands/lowest-total-price")
    public ApplicationResponse<LowestTotalPriceBrandResponse> getData() {
        return ApplicationResponse.success(aggregationService.getLowestTotalBrand());
    }

    @Schema(name = "데이터 재집계 API", description = "수동으로 집계 데이터를 최신화할때 사용하는 api입니다.")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ApplicationResponse<Void> aggregateData() {
        aggregationService.aggregateAllDatas();
        return ApplicationResponse.success();
    }
}
