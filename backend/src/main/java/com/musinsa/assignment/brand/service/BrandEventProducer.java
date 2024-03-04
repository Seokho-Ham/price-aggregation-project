package com.musinsa.assignment.brand.service;

import com.musinsa.assignment.brand.service.dto.BrandChangeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class BrandEventProducer {

    private final ApplicationEventPublisher eventPublisher;

    public void produceBrandChangeEvent(Long brandId) {
        eventPublisher.publishEvent(new BrandChangeEvent(brandId));
    }

}
