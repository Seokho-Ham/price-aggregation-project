package com.musinsa.assignment.brand.service;

import com.musinsa.assignment.brand.service.dto.BrandCreateDto;
import com.musinsa.assignment.brand.service.dto.BrandUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BrandServiceFacade {

    private final BrandService brandService;
    private final BrandEventProducer eventProducer;

    public void create(BrandCreateDto requestDto) {
        Long brandId = brandService.create(requestDto);
        eventProducer.produceBrandChangeEvent(brandId);
    }

    public void update(BrandUpdateDto requestDto) {
        Long brandId = brandService.update(requestDto);
        eventProducer.produceBrandChangeEvent(brandId);
    }

    public void delete(Long brandId) {
        Long deletedBrandId = brandService.delete(brandId);
        eventProducer.produceBrandChangeEvent(deletedBrandId);
    }

}
