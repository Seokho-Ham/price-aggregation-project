package com.musinsa.assignment.aggregation.service;

import com.musinsa.assignment.item.service.dto.ItemChangeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class ItemChangeEventListener {

    private final AggregationService aggregationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleItemChange(ItemChangeEvent event) {
        aggregationService.aggregate(event.getItemId());
        log.info("[aggregation-item] 아이템 데이터 변경에 따른 데이터 집계 성공");
    }

}
