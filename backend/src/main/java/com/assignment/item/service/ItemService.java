package com.assignment.item.service;

import com.assignment.item.service.dto.ItemUpdateDto;
import com.assignment.item.service.dto.ItemCreateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ItemService {

    private final ItemWriter itemWriter;
    private final ItemEventProducer itemEventProducer;

    public void create(ItemCreateDto requestDto) {
        Long itemId = itemWriter.create(requestDto);
        itemEventProducer.produceItemChangeEvent(itemId);
    }

    public void update(ItemUpdateDto requestDto) {
        Long itemId = itemWriter.update(requestDto);
        itemEventProducer.produceItemChangeEvent(itemId);
    }

    public void delete(Long itemId) {
        Long deletedItemId = itemWriter.delete(itemId);
        itemEventProducer.produceItemChangeEvent(deletedItemId);
    }

}
