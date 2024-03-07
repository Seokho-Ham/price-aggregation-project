package com.assignment.unit;

import com.assignment.common.exception.InvalidParamException;
import com.assignment.item.domain.Item;
import com.assignment.item.exception.InvalidPriceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("[단위테스트] Item Entity 테스트")
class ItemTest {

    @DisplayName("ItemEntity 생성 시 상품명이 null이거나 빈문자열일 경우 예외를 반환한다.")
    @NullAndEmptySource
    @ParameterizedTest
    void item_name_is_null_or_empty(String itemName) {
        assertThatThrownBy(() -> new Item(itemName, 1000D, 1L, 1L))
            .isInstanceOf(InvalidParamException.class);
    }

    @DisplayName("ItemEntity 생성 시 0원 이하의 가격을 값으로 전달할 경우 예외를 반환한다.")
    @Test
    void item_price_is_lower_than_zero() {
        assertThatThrownBy(() -> new Item("상품1", -1000D, 1L, 1L))
            .isInstanceOf(InvalidPriceException.class);
    }

    @DisplayName("ItemEntity 업데이트 시 상품명이 null이거나 빈문자열일 않으면 예외를 반환한다.")
    @NullAndEmptySource
    @ParameterizedTest
    void invalid_item_name_on_update(String itemName) {
        Item item = new Item("상품1", 1000D, 1L, 1L);
        assertThatThrownBy(() -> item.update(itemName, 20000D))
            .isInstanceOf(InvalidParamException.class);
    }

}
