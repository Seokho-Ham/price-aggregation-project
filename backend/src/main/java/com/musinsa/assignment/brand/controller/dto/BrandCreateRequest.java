package com.musinsa.assignment.brand.controller.dto;

import com.musinsa.assignment.brand.service.dto.BrandCreateDto;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BrandCreateRequest {

    @NotEmpty
    private final String brandName;

    public BrandCreateDto toDto() {
        return new BrandCreateDto(this.brandName);
    }
}
