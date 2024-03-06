package com.assignment.brand.service.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BrandDeleteEvent {

    private final Long brandId;
}
