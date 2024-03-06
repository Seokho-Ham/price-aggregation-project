package com.assignment.item.service;

import com.assignment.item.service.dto.ItemCreateEvent;
import com.assignment.item.service.dto.ItemDeleteEvent;
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

    public void produceItemCreateEvent(Long itemId) {
        eventPublisher.publishEvent(new ItemCreateEvent(itemId));
    }

    public void produceItemUpdateEvent(Long itemId) {
        eventPublisher.publishEvent(new ItemUpdateEvent(itemId));
    }

    public void produceItemDeleteEvent(Long itemId) {
        eventPublisher.publishEvent(new ItemDeleteEvent(itemId));
    }
}
