package com.assignment.aggregation.service;

import com.assignment.brand.service.dto.BrandChangeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class BrandChangeEventListener {

    private final AggregationService aggregationService;

    @EventListener
    public void handleBrandChange(BrandChangeEvent event) {
        aggregationService.reaggreateByBrandId(event.getBrandId());
        log.info("[aggregation-brand] 브랜드 데이터 변경에 따른 데이터 집계 성공");
    }

}
