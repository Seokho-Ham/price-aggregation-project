package com.musinsa.assignment.unit;

import com.musinsa.assignment.item.domain.Item;
import com.musinsa.assignment.item.exception.InvalidPriceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("[단위테스트] Item Entity 테스트")
class ItemTest {

    @DisplayName("ItemEntity 생성 시 0원 이하의 가격을 값으로 전달할 경우 예외를 반환한다.")
    @Test
    void item_price_is_lower_than_zero() {
        assertThatThrownBy(() -> new Item("상품1", -1000D, 1L, 1L))
            .isInstanceOf(InvalidPriceException.class);
    }

}
