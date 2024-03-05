package com.assignment.item.service.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ItemUpdateDto {

    private final Long itemId;
    private final String itemName;
    private final Double price;

}
