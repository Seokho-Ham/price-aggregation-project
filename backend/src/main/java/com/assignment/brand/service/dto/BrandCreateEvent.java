package com.assignment.brand.service.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BrandCreateEvent {

    private final Long brandId;
}
