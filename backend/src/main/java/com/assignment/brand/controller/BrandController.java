package com.assignment.brand.controller;

import com.assignment.brand.controller.dto.BrandCreateRequest;
import com.assignment.brand.controller.dto.BrandUpdateRequest;
import com.assignment.brand.service.BrandService;
import com.assignment.common.dto.ApplicationResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "브랜드 API")
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
