package com.assignment.brand.service;

import com.assignment.brand.domain.Brand;
import com.assignment.brand.domain.BrandRepository;
import com.assignment.brand.service.dto.BrandCreateDto;
import com.assignment.brand.service.dto.BrandUpdateDto;
import com.assignment.brand.exception.BrandDuplicateException;
import com.assignment.brand.exception.BrandNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class BrandWriter {

    private final BrandRepository brandRepository;

    @Transactional
    public Long create(BrandCreateDto requestDto) {
        validateBrandNameDuplicate(requestDto.getBrandName());
        Brand brand = brandRepository.save(new Brand(requestDto.getBrandName()));
        return brand.getId();
    }

    @Transactional
    public Long update(BrandUpdateDto requestDto) {
        validateBrandNameDuplicate(requestDto.getBrandName());
        Brand brand = brandRepository.findByIdAndDeletedIsFalse(requestDto.getBrandId())
            .orElseThrow(BrandNotFoundException::new);
        brand.updateName(requestDto.getBrandName());
        return brand.getId();
    }

    @Transactional
    public Long delete(Long brandId) {
        Brand brand = brandRepository.findByIdAndDeletedIsFalse(brandId)
            .orElseThrow(BrandNotFoundException::new);
        brand.delete();
        return brand.getId();
    }

    //생성시에는 모든 데이터 기반으로 브랜드명 조회
    private void validateBrandNameDuplicate(String brandName) {
        Optional<Brand> existingBrand = brandRepository.findByName(brandName);
        if (existingBrand.isPresent()) {
            throw new BrandDuplicateException();
        }
    }
}
