package com.assignment.item.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Optional<Item> findByBrandIdAndNameAndDeletedIsFalse(Long brandId, String itemName);

    Optional<Item> findByBrandIdAndName(Long brandId, String itemName);

    Optional<Item> findByIdAndDeletedIsFalse(Long itemId);

    Optional<Item> findTopByCategoryIdAndDeletedIsFalseOrderByPriceDesc(Long categoryId);

    Optional<Item> findTopByCategoryIdAndDeletedIsFalseOrderByPriceAsc(Long categoryId);

}
