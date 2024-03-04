package com.musinsa.assignment.aggregation.service;

import com.musinsa.assignment.brand.service.dto.BrandChangeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BrandChangeEventListener {

    private final AggregationService aggregationService;

    @EventListener
    public void handleBrandChange(BrandChangeEvent event) {
        aggregationService.aggregate(event.getBrandId());
    }

}
