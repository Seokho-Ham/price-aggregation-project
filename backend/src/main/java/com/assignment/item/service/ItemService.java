package com.assignment.item.service;

import com.assignment.item.service.dto.ItemUpdateDto;
import com.assignment.item.service.dto.ItemCreateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ItemService {

    private final ItemWriter itemWriter;
    private final ItemEventProducer itemEventProducer;

    @Transactional
    public void create(ItemCreateDto requestDto) {
        Long itemId = itemWriter.create(requestDto);
        itemEventProducer.produceItemCreateEvent(itemId);
    }

    @Transactional
    public void update(ItemUpdateDto requestDto) {
        Long itemId = itemWriter.update(requestDto);
        itemEventProducer.produceItemUpdateEvent(itemId);
    }

    @Transactional
    public void delete(Long itemId) {
        Long deletedItemId = itemWriter.delete(itemId);
        itemEventProducer.produceItemDeleteEvent(deletedItemId);
    }

}
