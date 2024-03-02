package com.musinsa.assignment.brand.controller;

import com.musinsa.assignment.brand.controller.dto.BrandCreateRequest;
import com.musinsa.assignment.brand.service.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/brands")
@RestController
public class BrandController {

    private final BrandService brandService;

    @PostMapping
    public ResponseEntity<Void> createBrand(@RequestBody @Valid BrandCreateRequest request) {

        brandService.create(request);

        return ResponseEntity.ok().build();
    }

}
