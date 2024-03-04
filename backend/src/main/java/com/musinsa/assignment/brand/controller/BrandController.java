package com.musinsa.assignment.brand.controller;

import com.musinsa.assignment.brand.controller.dto.BrandCreateRequest;
import com.musinsa.assignment.brand.controller.dto.BrandUpdateRequest;
import com.musinsa.assignment.brand.service.BrandServiceFacade;
import com.musinsa.assignment.common.dto.ApplicationResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "브랜드 API")
@RequiredArgsConstructor
@RequestMapping("/brands")
@RestController
public class BrandController {

    private final BrandServiceFacade brandServiceFacade;

    @PostMapping
    public ApplicationResponse<Void> createBrand(@RequestBody @Valid BrandCreateRequest request) {
        brandServiceFacade.create(request.toDto());
        return ApplicationResponse.success();
    }

    @PatchMapping("/{brandId}")
    public ApplicationResponse<Void> updateBrand(@PathVariable("brandId") Long brandId, @RequestBody @Valid BrandUpdateRequest request) {
        brandServiceFacade.update(request.toDto(brandId));
        return ApplicationResponse.success();
    }

    @DeleteMapping("/{brandId}")
    public ApplicationResponse<Void> deleteBrand(@PathVariable("brandId") Long brandId) {
        brandServiceFacade.delete(brandId);
        return ApplicationResponse.success();
    }

}
