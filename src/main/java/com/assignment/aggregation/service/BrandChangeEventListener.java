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
        aggregationService.aggregateOnBrandUpdate(event.getBrandId());
    }

    @TransactionalEventListener
    public void handleBrandDelete(BrandDeleteEvent event) {
        aggregationService.aggregateOnBrandDelete(event.getBrandId());
    }
}
