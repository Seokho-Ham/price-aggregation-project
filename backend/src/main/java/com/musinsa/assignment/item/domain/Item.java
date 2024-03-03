package com.musinsa.assignment.item.domain;

import com.musinsa.assignment.common.domain.BaseTimeEntity;
import com.musinsa.assignment.item.exception.InvalidPriceException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
        validatePrice(price);
        this.name = name;
        this.price = price;
        this.brandId = brandId;
        this.categoryId = categoryId;
        this.deleted = false;
    }

    private void validatePrice(double price) {
        if (price < 0) {
            throw new InvalidPriceException();
        }
    }
}
