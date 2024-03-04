package com.musinsa.assignment.item.service;

import com.musinsa.assignment.item.service.dto.ItemChangeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ItemEventProducer {

    private final ApplicationEventPublisher eventPublisher;

    public void produceItemChangeEvent(Long itemId) {
        eventPublisher.publishEvent(new ItemChangeEvent(itemId));
    }

}
