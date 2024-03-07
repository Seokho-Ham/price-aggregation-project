package com.assignment.item.service.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ItemDto {

    private final Long brandId;
    private final Long categoryId;
    private final Double price;

}
