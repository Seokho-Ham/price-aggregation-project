package com.assignment.brand.service;

import com.assignment.brand.service.dto.BrandCreateEvent;
import com.assignment.brand.service.dto.BrandDeleteEvent;
import com.assignment.brand.service.dto.BrandUpdateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class BrandEventProducer {

    private final ApplicationEventPublisher eventPublisher;

    public void produceBrandCreateEvent(Long brandId) {
        eventPublisher.publishEvent(new BrandCreateEvent(brandId));
    }

    public void produceBrandUpdateEvent(Long brandId) {
        eventPublisher.publishEvent(new BrandUpdateEvent(brandId));
    }

    public void produceBrandDeleteEvent(Long brandId) {
        eventPublisher.publishEvent(new BrandDeleteEvent(brandId));
    }

}
