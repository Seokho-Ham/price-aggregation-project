package com.assignment.item.service;

import com.assignment.item.service.dto.ItemDto;
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
        ItemDto dto = itemWriter.create(requestDto);
        itemEventProducer.produceItemCreateEvent(dto);
    }

    @Transactional
    public void update(ItemUpdateDto requestDto) {
        ItemDto dto = itemWriter.update(requestDto);
        itemEventProducer.produceItemUpdateEvent(dto);
    }

    @Transactional
    public void delete(Long itemId) {
        ItemDto dto = itemWriter.delete(itemId);
        itemEventProducer.produceItemDeleteEvent(dto);
    }

}
