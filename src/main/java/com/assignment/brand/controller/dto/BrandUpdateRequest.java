package com.assignment.brand.controller.dto;

import com.assignment.brand.service.dto.BrandUpdateDto;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BrandUpdateRequest {

    @NotEmpty
    private String brandName;

    public BrandUpdateRequest(String brandName) {
        this.brandName = brandName;
    }

    public BrandUpdateDto toDto(Long id) {
        return new BrandUpdateDto(id, this.brandName);
    }

}
