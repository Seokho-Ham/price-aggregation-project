package com.assignment.item.controller.dto;

import com.assignment.item.service.dto.ItemUpdateDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

@Getter
public class ItemUpdateRequest {

    @NotNull
    @NotEmpty
    private String itemName;

    @Positive
    private Double price;

    public ItemUpdateDto toDto(Long itemId) {
        return new ItemUpdateDto(itemId, this.itemName, this.price);
    }

}
