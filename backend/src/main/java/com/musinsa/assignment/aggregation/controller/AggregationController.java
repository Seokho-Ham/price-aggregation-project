package com.musinsa.assignment.aggregation.controller;

import com.musinsa.assignment.aggregation.controller.dto.CategoryLowestAndHighestBrandResponse;
import com.musinsa.assignment.aggregation.controller.dto.CategoriesLowestPriceBrandsResponse;
import com.musinsa.assignment.aggregation.service.AggregationService;
import com.musinsa.assignment.common.dto.ApplicationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AggregationController {

    private final AggregationService aggregationService;

    @GetMapping("/aggregations/category/{categoryName}/brand/lowest-highest")
    public ApplicationResponse<CategoryLowestAndHighestBrandResponse> getData(@PathVariable String categoryName) {
        CategoryLowestAndHighestBrandResponse response = aggregationService.getLowestHighestPriceBrandForSingleCategory(categoryName);
        return ApplicationResponse.success(response);
    }

    @GetMapping("/aggregations/categories/brands/lowest")
    public ApplicationResponse<CategoriesLowestPriceBrandsResponse> getLowestPriceBrandsByCategories() {
        CategoriesLowestPriceBrandsResponse lowestPriceBrands = aggregationService.getLowestPriceBrandsForAllCategories();
        return ApplicationResponse.success(lowestPriceBrands);
    }

    @PostMapping("/aggregations")
    public ApplicationResponse<Void> aggregateData() {
        aggregationService.aggregateLowestHighestPriceBrandForAllCategories();
        return ApplicationResponse.success();
    }
}
