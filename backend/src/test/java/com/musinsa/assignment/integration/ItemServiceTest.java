package com.musinsa.assignment.integration;

import com.musinsa.assignment.brand.domain.Brand;
import com.musinsa.assignment.brand.domain.BrandRepository;
import com.musinsa.assignment.category.domain.Category;
import com.musinsa.assignment.category.domain.CategoryRepository;
import com.musinsa.assignment.item.domain.Item;
import com.musinsa.assignment.item.domain.ItemRepository;
import com.musinsa.assignment.item.exception.ItemDuplicateException;
import com.musinsa.assignment.item.service.ItemService;
import com.musinsa.assignment.item.service.dto.ItemCreateDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("[통합테스트] 상품 생성, 수정, 삭제 테스성")
class ItemServiceTest extends IntegrationTest{

    @Autowired
    private ItemService itemService;

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

        itemService.create(new ItemCreateDto(itemName, price, brand.getId(), category.getId()));

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

        assertThatThrownBy(() -> itemService.create(new ItemCreateDto(itemName, price, brand.getId(), category.getId())))
            .isInstanceOf(ItemDuplicateException.class);
    }

}