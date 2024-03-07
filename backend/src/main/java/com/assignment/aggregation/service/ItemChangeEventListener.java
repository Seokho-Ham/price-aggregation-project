package com.assignment.aggregation.service;

import com.assignment.item.service.dto.ItemCreateEvent;
import com.assignment.item.service.dto.ItemDeleteEvent;
import com.assignment.item.service.dto.ItemUpdateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class ItemChangeEventListener {

    private final AggregationService aggregationService;

    @TransactionalEventListener
    public void handleItemCreate(ItemCreateEvent event) {
        aggregationService.aggregateOnItemCreate(event.getItemDto());
    }

    @TransactionalEventListener
    public void handleItemUpdate(ItemUpdateEvent event) {
        aggregationService.aggregateOnItemUpdate(event.getItemDto());
    }

    @TransactionalEventListener
    public void handleItemDelete(ItemDeleteEvent event) {
        aggregationService.aggregateOnItemDelete(event.getItemDto());
    }

}
