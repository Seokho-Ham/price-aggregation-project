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

    private final AggregationWriter aggregationWriter;

    @TransactionalEventListener
    public void handleItemCreate(ItemCreateEvent event) {
        //브랜드의 해당 카테고리 데이터의 가격과 비교해서 갱신
        //현재 해당 아이템이 속한 카테고리의 최저가, 최고가 가격과 비교해서 갱신이 필요하면 정보 업데이트

        log.info("[aggregation-item] 상품 데이터 생성에 따른 데이터 집계 성공");
    }

    @TransactionalEventListener
    public void handleItemUpdate(ItemUpdateEvent event) {
        //브랜드의 해당 카테고리 데이터의 가격과 비교해서 갱신
        //현재 해당 아이템이 속한 카테고리의 최저가, 최고가 가격과 비교해서 갱신이 필요하면 정보 업데이트
        log.info("[aggregation-item] 상품 데이터 변경에 따른 데이터 집계 성공");
    }

    @TransactionalEventListener
    public void handleItemDelete(ItemDeleteEvent event) {
        //브랜드의 해당 카테고리 데이터의 가격과 비교해서 갱신
        //현재 해당 아이템이 속한 카테고리의 최저가, 최고가 가격과 비교해서 갱신이 필요하면 정보 업데이트
        log.info("[aggregation-item] 상품 데이터 삭제에 따른 데이터 집계 성공");
    }

}
