package com.assignment.brand.service;

import com.assignment.brand.service.dto.BrandCreateDto;
import com.assignment.brand.service.dto.BrandUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BrandService {

    private final BrandWriter brandWriter;
    private final BrandEventProducer eventProducer;

    public void create(BrandCreateDto requestDto) {
        brandWriter.create(requestDto);
    }

    public void update(BrandUpdateDto requestDto) {
        Long brandId = brandWriter.update(requestDto);
        eventProducer.produceBrandChangeEvent(brandId);
    }

    public void delete(Long brandId) {
        Long deletedBrandId = brandWriter.delete(brandId);
        eventProducer.produceBrandChangeEvent(deletedBrandId);
    }

}
