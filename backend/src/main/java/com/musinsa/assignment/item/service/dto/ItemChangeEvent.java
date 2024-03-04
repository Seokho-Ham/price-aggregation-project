package com.musinsa.assignment.item.service.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ItemChangeEvent {

    private final Long itemId;
}
