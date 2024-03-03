package com.musinsa.assignment.item.service.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ItemCreateDto {

    private final String itemName;
    private final double price;
    private final Long brandId;
    private final Long categoryId;

}
