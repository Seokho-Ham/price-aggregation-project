package com.assignment.item.domain;

import com.assignment.common.domain.BaseTimeEntity;
import com.assignment.common.exception.InvalidParamException;
import com.assignment.item.exception.InvalidPriceException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Item extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    @Column(name = "item_name")
    private String name;

    private Double price;
    private Long brandId;
    private Long categoryId;
    private Boolean deleted;
    private LocalDateTime deletedAt;

    public Item(String name, Double price, Long brandId, Long categoryId) {
        validateName(name);
        validatePrice(price);
        this.name = name;
        this.price = price;
        this.brandId = brandId;
        this.categoryId = categoryId;
        this.deleted = false;
    }

    public void update(String itemName, Double price) {
        validateName(itemName);
        validatePrice(price);
        this.name = itemName;
        this.price = price;
    }

    public void delete() {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    private void validateName(String itemName) {
        if (ObjectUtils.isEmpty(itemName)) {
            throw new InvalidParamException();
        }
    }

    private void validatePrice(Double price) {
        if (ObjectUtils.isEmpty(price)) {
            throw new InvalidParamException();
        }

        if (price < 0) {
            throw new InvalidPriceException();
        }
    }
}
