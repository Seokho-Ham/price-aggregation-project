package com.assignment.item.controller.dto;

import com.assignment.item.service.dto.ItemUpdateDto;
import lombok.Getter;

@Getter
public class ItemUpdateRequest {

    private String itemName;
    private Double price;

    public ItemUpdateDto toDto(Long itemId) {
        return new ItemUpdateDto(itemId, this.itemName, this.price);
    }

}
