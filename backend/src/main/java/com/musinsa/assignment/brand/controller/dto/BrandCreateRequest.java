package com.musinsa.assignment.brand.controller.dto;

import com.musinsa.assignment.brand.service.dto.BrandCreateDto;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BrandCreateRequest {

    @NotEmpty
    private String brandName;

    public BrandCreateRequest(String brandName) {
        this.brandName = brandName;
    }

    public BrandCreateDto toDto() {
        return new BrandCreateDto(this.brandName);
    }
}
