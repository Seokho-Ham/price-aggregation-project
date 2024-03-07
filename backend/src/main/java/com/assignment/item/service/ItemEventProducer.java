package com.assignment.item.service;

import com.assignment.item.service.dto.ItemCreateEvent;
import com.assignment.item.service.dto.ItemDeleteEvent;
import com.assignment.item.service.dto.ItemDto;
import com.assignment.item.service.dto.ItemUpdateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ItemEventProducer {

    private final ApplicationEventPublisher eventPublisher;

    public void produceItemCreateEvent(ItemDto dto) {
        eventPublisher.publishEvent(new ItemCreateEvent(dto));
    }

    public void produceItemUpdateEvent(ItemDto dto) {
        eventPublisher.publishEvent(new ItemUpdateEvent(dto));
    }

    public void produceItemDeleteEvent(ItemDto dto) {
        eventPublisher.publishEvent(new ItemDeleteEvent(dto));
    }
}
