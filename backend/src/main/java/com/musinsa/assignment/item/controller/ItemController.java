package com.musinsa.assignment.item.controller;

import com.musinsa.assignment.common.dto.ApplicationResponse;
import com.musinsa.assignment.item.controller.dto.ItemCreateRequest;
import com.musinsa.assignment.item.controller.dto.ItemUpdateRequest;
import com.musinsa.assignment.item.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/items")
@RestController
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ApplicationResponse<Void> createItem(@RequestBody @Valid ItemCreateRequest request) {
        itemService.create(request.toDto());
        return ApplicationResponse.success();
    }

    @PatchMapping("/{itemId}")
    public ApplicationResponse<Void> updateItem(@PathVariable("itemId") Long itemId, @RequestBody ItemUpdateRequest request) {
        itemService.update(request.toDto(itemId));
        return ApplicationResponse.success();
    }

    @DeleteMapping("/{itemId}")
    public ApplicationResponse<Void> deleteItem(@PathVariable("itemId") Long itemId) {
        itemService.delete(itemId);
        return ApplicationResponse.success();
    }

}
