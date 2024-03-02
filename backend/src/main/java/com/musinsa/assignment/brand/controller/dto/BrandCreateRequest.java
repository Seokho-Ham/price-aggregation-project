package com.musinsa.assignment.brand.controller.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BrandCreateRequest {

    @NotEmpty
    private final String brandName;

}
