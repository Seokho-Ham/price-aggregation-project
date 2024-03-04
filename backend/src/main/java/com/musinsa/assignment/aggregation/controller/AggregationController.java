package com.musinsa.assignment.aggregation.controller;

import com.musinsa.assignment.aggregation.controller.dto.CategoryLowestAndHighestBrandResponse;
import com.musinsa.assignment.aggregation.service.AggregationService;
import com.musinsa.assignment.common.dto.ApplicationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AggregationController {

    private final AggregationService aggregationService;

    @GetMapping("/aggregations/category/brand/lowest-highest")
    public ApplicationResponse<CategoryLowestAndHighestBrandResponse> getData(@RequestParam("categoryName") String categoryName) {
        CategoryLowestAndHighestBrandResponse response = aggregationService.getCategoryLowestHighestPriceBrand(categoryName);
        return ApplicationResponse.success(response);
    }

}
