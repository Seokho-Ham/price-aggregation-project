package com.assignment.item.controller;

import com.assignment.item.controller.dto.ItemUpdateRequest;
import com.assignment.item.service.ItemService;
import com.assignment.common.dto.ApplicationResponse;
import com.assignment.item.controller.dto.ItemCreateRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "상품 API")
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
