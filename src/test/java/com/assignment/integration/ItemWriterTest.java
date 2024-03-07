package com.assignment.integration;

import com.assignment.brand.domain.Brand;
import com.assignment.brand.domain.BrandRepository;
import com.assignment.category.domain.Category;
import com.assignment.category.domain.CategoryRepository;
import com.assignment.item.domain.Item;
import com.assignment.item.domain.ItemRepository;
import com.assignment.item.exception.ItemDuplicateException;
import com.assignment.item.service.ItemWriter;
import com.assignment.item.service.dto.ItemCreateDto;
import com.assignment.item.service.dto.ItemUpdateDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("[통합테스트] 상품 생성, 수정, 삭제 테스성")
class ItemWriterTest extends IntegrationTest{

    @Autowired
    private ItemWriter itemWriter;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @AfterEach
    void clearData() {
        itemRepository.deleteAll();
        brandRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @DisplayName("동일한 브랜드에 중복되는 상품이 없다면 상품을 생성한다.")
    @Test
    void create_item_success() {
        String itemName = "상품1";
        double price = 10000;
        Brand brand = brandRepository.save(new Brand("브랜드1"));
        Category category = categoryRepository.save(new Category("카테고리1"));

        itemWriter.create(new ItemCreateDto(itemName, price, brand.getId(), category.getId()));

        Item item = itemRepository.findByBrandIdAndNameAndDeletedIsFalse(brand.getId(), itemName).get();

        assertAll(() -> {
            assertThat(item.getName()).isEqualTo(itemName);
            assertThat(item.getPrice()).isEqualTo(price);
            assertThat(item.getBrandId()).isEqualTo(brand.getId());
            assertThat(item.getCategoryId()).isEqualTo(category.getId());
        });

    }

    @DisplayName("중복되는 상품명이 존재한다면 예외를 반환한다.")
    @Test
    void create_item_fail_by_invalid_brand() {
        String itemName = "상품1";
        double price = 10000;
        Brand brand = brandRepository.save(new Brand("브랜드1"));
        Category category = categoryRepository.save(new Category("카테고리1"));
        itemRepository.save(new Item(itemName, price, brand.getId(), category.getId()));

        assertThatThrownBy(() -> itemWriter.create(new ItemCreateDto(itemName, price, brand.getId(), category.getId())))
            .isInstanceOf(ItemDuplicateException.class);
    }

    @DisplayName("존재하는 상품에 대한 정상적인 업데이트 요청이라면 해당 상품의 정보를 업데이트 한다.")
    @Test
    void update_item_success() {
        String originalName = "상품1";
        double originalPrice = 10000;
        Brand brand = brandRepository.save(new Brand("브랜드1"));
        Category category = categoryRepository.save(new Category("카테고리1"));
        Item item = itemRepository.save(new Item(originalName, originalPrice, brand.getId(), category.getId()));

        String newName = "상품2";
        double newPrice = 20000;
        itemWriter.update(new ItemUpdateDto(item.getId(), newName, newPrice));

        Item updatedItem = itemRepository.findById(item.getId()).get();

        assertAll(() -> {
            assertThat(updatedItem.getName()).isEqualTo(newName);
            assertThat(updatedItem.getPrice()).isEqualTo(newPrice);
        });
    }

    @DisplayName("존재하지 않는 상품에 대한 업데이트 요청일 경우 예외를 반환한다.")
    @Test
    void update_item_fail_by_item_not_found() {
        Long itemId = Long.MAX_VALUE;
        String itemName = "상품1";
        double price = 10000;

        assertThatThrownBy(() -> itemWriter.update(new ItemUpdateDto(itemId, itemName, price)));
    }

    @DisplayName("올바른 상품의 Id를 전달할 경우 상품 정보를 삭제한다.")
    @Test
    void delete_item_success() {
        String itemName = "상품1";
        double price = 10000;
        Brand brand = brandRepository.save(new Brand("브랜드1"));
        Category category = categoryRepository.save(new Category("카테고리1"));
        Item item = itemRepository.save(new Item(itemName, price, brand.getId(), category.getId()));

        itemWriter.delete(item.getId());

        Item deletedItem = itemRepository.findById(item.getId()).get();

        assertAll(() -> {
            assertThat(deletedItem.getDeleted()).isTrue();
            assertThat(deletedItem.getDeletedAt()).isNotNull();
        });
    }
}
