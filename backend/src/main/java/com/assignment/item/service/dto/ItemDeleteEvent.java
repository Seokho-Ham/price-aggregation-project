package com.assignment.item.service.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ItemDeleteEvent {

    private final Long itemId;
}
