package com.musinsa.assignment.item.service;

import com.musinsa.assignment.brand.domain.BrandRepository;
import com.musinsa.assignment.brand.exception.BrandNotFoundException;
import com.musinsa.assignment.category.domain.CategoryRepository;
import com.musinsa.assignment.category.exception.CategoryNotFoundException;
import com.musinsa.assignment.item.domain.Item;
import com.musinsa.assignment.item.domain.ItemRepository;
import com.musinsa.assignment.item.exception.ItemDuplicateException;
import com.musinsa.assignment.item.exception.ItemNotFoundException;
import com.musinsa.assignment.item.service.dto.ItemCreateDto;
import com.musinsa.assignment.item.service.dto.ItemUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public void create(ItemCreateDto requestDto) {
        validateBrandExists(requestDto.getBrandId());
        validateCategoryExists(requestDto.getCategoryId());
        validateItemDuplicate(requestDto.getBrandId(), requestDto.getItemName());
        itemRepository.save(new Item(requestDto.getItemName(), requestDto.getPrice(), requestDto.getBrandId(), requestDto.getCategoryId()));
    }

    @Transactional
    public void update(ItemUpdateDto itemUpdateDto) {
        Item item = itemRepository.findByIdAndDeletedIsFalse(itemUpdateDto.getItemId())
            .orElseThrow(ItemNotFoundException::new);
        item.update(itemUpdateDto.getItemName(), itemUpdateDto.getPrice());
    }

    @Transactional
    public void delete(Long itemId) {
        Item item = itemRepository.findByIdAndDeletedIsFalse(itemId)
            .orElseThrow(ItemNotFoundException::new);
        item.delete();
    }

    private void validateBrandExists(Long brandId) {
        if (!brandRepository.existsById(brandId)) {
            throw new BrandNotFoundException();
        }
    }

    private void validateCategoryExists(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new CategoryNotFoundException();
        }
    }

    private void validateItemDuplicate(Long brandId, String itemName) {
        Optional<Item> item = itemRepository.findByBrandIdAndName(brandId, itemName);
        if (item.isPresent()) {
            throw new ItemDuplicateException();
        }
    }
}
