package com.assignment.item.service;

import com.assignment.category.exception.CategoryNotFoundException;
import com.assignment.item.exception.ItemDuplicateException;
import com.assignment.item.service.dto.ItemUpdateDto;
import com.assignment.brand.domain.BrandRepository;
import com.assignment.brand.exception.BrandNotFoundException;
import com.assignment.category.domain.CategoryRepository;
import com.assignment.item.domain.Item;
import com.assignment.item.domain.ItemRepository;
import com.assignment.item.exception.ItemNotFoundException;
import com.assignment.item.service.dto.ItemCreateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ItemWriter {

    private final ItemRepository itemRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public Long create(ItemCreateDto requestDto) {
        validateBrandExists(requestDto.getBrandId());
        validateCategoryExists(requestDto.getCategoryId());
        validateItemDuplicate(requestDto.getBrandId(), requestDto.getItemName());
        Item item = itemRepository.save(new Item(requestDto.getItemName(), requestDto.getPrice(), requestDto.getBrandId(), requestDto.getCategoryId()));
        return item.getId();
    }

    @Transactional
    public Long update(ItemUpdateDto itemUpdateDto) {
        Item item = itemRepository.findByIdAndDeletedIsFalse(itemUpdateDto.getItemId())
            .orElseThrow(ItemNotFoundException::new);
        item.update(itemUpdateDto.getItemName(), itemUpdateDto.getPrice());
        return item.getId();
    }

    @Transactional
    public Long delete(Long itemId) {
        Item item = itemRepository.findByIdAndDeletedIsFalse(itemId)
            .orElseThrow(ItemNotFoundException::new);
        item.delete();
        return item.getId();
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
