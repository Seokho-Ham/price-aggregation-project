package com.musinsa.assignment.item.service;

import com.musinsa.assignment.item.service.dto.ItemCreateDto;
import com.musinsa.assignment.item.service.dto.ItemUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ItemServiceFacade {

    private final ItemService itemService;
    private final ItemEventProducer itemEventProducer;

    public void create(ItemCreateDto requestDto) {
        Long itemId = itemService.create(requestDto);
        itemEventProducer.produceItemChangeEvent(itemId);
    }

    public void update(ItemUpdateDto requestDto) {
        Long itemId = itemService.update(requestDto);
        itemEventProducer.produceItemChangeEvent(itemId);
    }

    public void delete(Long itemId) {
        Long deletedItemId = itemService.delete(itemId);
        itemEventProducer.produceItemChangeEvent(deletedItemId);
    }

}
