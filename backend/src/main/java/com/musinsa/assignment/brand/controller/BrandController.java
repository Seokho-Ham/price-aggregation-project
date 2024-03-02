package com.musinsa.assignment.brand.controller;

import com.musinsa.assignment.brand.controller.dto.BrandCreateRequest;
import com.musinsa.assignment.brand.controller.dto.BrandUpdateRequest;
import com.musinsa.assignment.brand.service.BrandService;
import com.musinsa.assignment.common.dto.ApplicationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/brands")
@RestController
public class BrandController {

    private final BrandService brandService;

    @PostMapping
    public ApplicationResponse<Void> createBrand(@RequestBody @Valid BrandCreateRequest request) {
        brandService.create(request.toDto());
        return ApplicationResponse.success();
    }

    @PatchMapping("/{brandId}")
    public ApplicationResponse<Void> updateBrand(@PathVariable("brandId") Long brandId, @RequestBody @Valid BrandUpdateRequest request) {
        brandService.update(request.toDto(brandId));
        return ApplicationResponse.success();
    }

    @DeleteMapping("/{brandId}")
    public ApplicationResponse<Void> deleteBrand(@PathVariable("brandId") Long brandId) {
        brandService.delete(brandId);
        return ApplicationResponse.success();
    }

}
