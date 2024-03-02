package com.musinsa.assignment.brand.service;

import com.musinsa.assignment.brand.domain.Brand;
import com.musinsa.assignment.brand.domain.BrandRepository;
import com.musinsa.assignment.brand.exception.BrandDuplicationException;
import com.musinsa.assignment.brand.exception.BrandNotFoundException;
import com.musinsa.assignment.brand.service.dto.BrandCreateDto;
import com.musinsa.assignment.brand.service.dto.BrandUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class BrandService {

    private final BrandRepository brandRepository;

    @Transactional
    public void create(BrandCreateDto requestDto) {
        validateBrandNameDuplicate(requestDto.getBrandName());
        brandRepository.save(new Brand(requestDto.getBrandName()));
    }

    @Transactional
    public void update(BrandUpdateDto requestDto) {
        validateBrandNameDuplicate(requestDto.getBrandName());
        Brand brand = brandRepository.findByIdAndDeletedIsFalse(requestDto.getBrandId())
            .orElseThrow(BrandNotFoundException::new);
        brand.updateName(requestDto.getBrandName());
    }

    @Transactional
    public void delete(Long brandId) {
        Brand brand = brandRepository.findByIdAndDeletedIsFalse(brandId)
            .orElseThrow(BrandNotFoundException::new);
        brand.delete();
    }

    //생성시에는 모든 데이터 기반으로 브랜드명 조회
    private void validateBrandNameDuplicate(String brandName) {
        Optional<Brand> existingBrand = brandRepository.findByName(brandName);
        if (existingBrand.isPresent()) {
            throw new BrandDuplicationException();
        }
    }
}
