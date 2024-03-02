package com.musinsa.assignment.brand.service;

import com.musinsa.assignment.brand.controller.dto.BrandCreateRequest;
import com.musinsa.assignment.brand.domain.Brand;
import com.musinsa.assignment.brand.domain.BrandRepository;
import com.musinsa.assignment.brand.exception.BrandDuplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class BrandService {

    private final BrandRepository brandRepository;

    @Transactional
    public void create(BrandCreateRequest request) {

        Optional<Brand> existingBrand = brandRepository.findByName(request.getBrandName());

        if (existingBrand.isPresent()) {
            throw new BrandDuplicationException();
        }

        Brand newBrand = new Brand(request.getBrandName());
        brandRepository.save(newBrand);
    }
}
