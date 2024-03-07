package com.assignment.aggregation.service;

import com.assignment.brand.service.dto.BrandCreateEvent;
import com.assignment.brand.service.dto.BrandDeleteEvent;
import com.assignment.brand.service.dto.BrandUpdateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class BrandChangeEventListener {

    private final AggregationService aggregationService;

    @TransactionalEventListener
    public void handleBrandCreate(BrandCreateEvent event) {
        aggregationService.aggregateOnBrandCreate(event.getBrandId());
    }

    @TransactionalEventListener
    public void handleBrandUpdate(BrandUpdateEvent event) {
        //카테고리의 최저가, 최고가 정보 업데이트
        aggregationService.aggregateOnBrandUpdate(event.getBrandId());
    }

    @TransactionalEventListener
    public void handleBrandDelete(BrandDeleteEvent event) {
        //해당 브랜드의 카테고리별 데이터 삭제
        //카테고리의 최저가, 최고가 정보 재집계
        aggregationService.aggregateOnBrandDelete(event.getBrandId());
    }
}