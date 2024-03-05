package com.assignment.brand.service.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BrandUpdateDto {

    private final Long brandId;
    private final String brandName;

}
