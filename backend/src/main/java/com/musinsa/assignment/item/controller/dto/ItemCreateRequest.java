package com.musinsa.assignment.item.controller.dto;

import com.musinsa.assignment.item.service.dto.ItemCreateDto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

@Getter
public class ItemCreateRequest {

    @NotNull
    private String itemName;

    @NotNull
    @Positive
    private Double price;

    @NotNull
    private Long brandId;

    @NotNull
    private Long categoryId;

    public ItemCreateDto toDto() {
        return new ItemCreateDto(this.itemName, this.price, this.brandId, this.categoryId);
    }

}
