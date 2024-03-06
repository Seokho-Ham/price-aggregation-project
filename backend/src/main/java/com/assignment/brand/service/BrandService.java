package com.assignment.brand.service;

import com.assignment.brand.service.dto.BrandCreateDto;
import com.assignment.brand.service.dto.BrandUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BrandService {

    private final BrandWriter brandWriter;
    private final BrandEventProducer eventProducer;

    @Transactional
    public void create(BrandCreateDto requestDto) {
        Long brandId = brandWriter.create(requestDto);
        eventProducer.produceBrandCreateEvent(brandId);
    }

    @Transactional
    public void update(BrandUpdateDto requestDto) {
        Long brandId = brandWriter.update(requestDto);
        eventProducer.produceBrandUpdateEvent(brandId);
    }

    @Transactional
    public void delete(Long brandId) {
        Long deletedBrandId = brandWriter.delete(brandId);
        eventProducer.produceBrandDeleteEvent(deletedBrandId);
    }

}
