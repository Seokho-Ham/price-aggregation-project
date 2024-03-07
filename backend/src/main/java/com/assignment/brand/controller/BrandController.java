package com.assignment.brand.controller;

import com.assignment.brand.controller.dto.BrandCreateRequest;
import com.assignment.brand.controller.dto.BrandUpdateRequest;
import com.assignment.brand.service.BrandService;
import com.assignment.common.dto.ApplicationResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "브랜드 API")
@Validated
@RequiredArgsConstructor
@RequestMapping("/brands")
@RestController
public class BrandController {

    private final BrandService brandService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ApplicationResponse<Void> createBrand(@RequestBody @Valid BrandCreateRequest request) {
        brandService.create(request.toDto());
        return ApplicationResponse.success();
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{brandId}")
    public ApplicationResponse<Void> updateBrand(@PathVariable("brandId") @Positive Long brandId, @RequestBody @Valid BrandUpdateRequest request) {
        brandService.update(request.toDto(brandId));
        return ApplicationResponse.success();
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{brandId}")
    public ApplicationResponse<Void> deleteBrand(@PathVariable("brandId") @Positive Long brandId) {
        brandService.delete(brandId);
        return ApplicationResponse.success();
    }

}
