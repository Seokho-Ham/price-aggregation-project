package com.musinsa.assignment.aggregation.service;

import com.musinsa.assignment.item.service.dto.ItemChangeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ItemChangeEventListener {

    private final AggregationService aggregationService;

    @EventListener
    public void handleItemChange(ItemChangeEvent event) {
        aggregationService.reaggreateByItemId(event.getItemId());
        log.info("[aggregation-item] 상품 데이터 변경에 따른 데이터 집계 성공");
    }

}
