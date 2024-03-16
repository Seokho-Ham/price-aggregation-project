package com.assignment.item.service.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
// review: IDE 에도 뜨는데 dto class type 을 Record 도 써보셈
public class ItemCreateDto {

    private final String itemName;
    private final double price;
    private final Long brandId;
    private final Long categoryId;

}
