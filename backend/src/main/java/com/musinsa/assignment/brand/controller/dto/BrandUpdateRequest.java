package com.musinsa.assignment.brand.controller.dto;

import com.musinsa.assignment.brand.service.dto.BrandUpdateDto;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BrandUpdateRequest {

    @NotEmpty
    private final String brandName;

    public BrandUpdateDto toDto(Long id) {
        return new BrandUpdateDto(id, this.brandName);
    }

}
