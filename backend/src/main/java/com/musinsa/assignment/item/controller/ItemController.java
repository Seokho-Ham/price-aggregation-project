package com.musinsa.assignment.item.controller;

import com.musinsa.assignment.common.dto.ApplicationResponse;
import com.musinsa.assignment.item.controller.dto.ItemCreateRequest;
import com.musinsa.assignment.item.controller.dto.ItemUpdateRequest;
import com.musinsa.assignment.item.service.ItemServiceFacade;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "상품 API")
@RequiredArgsConstructor
@RequestMapping("/items")
@RestController
public class ItemController {

    private final ItemServiceFacade itemServiceFacade;

    @PostMapping
    public ApplicationResponse<Void> createItem(@RequestBody @Valid ItemCreateRequest request) {
        itemServiceFacade.create(request.toDto());
        return ApplicationResponse.success();
    }

    @PatchMapping("/{itemId}")
    public ApplicationResponse<Void> updateItem(@PathVariable("itemId") Long itemId, @RequestBody ItemUpdateRequest request) {
        itemServiceFacade.update(request.toDto(itemId));
        return ApplicationResponse.success();
    }

    @DeleteMapping("/{itemId}")
    public ApplicationResponse<Void> deleteItem(@PathVariable("itemId") Long itemId) {
        itemServiceFacade.delete(itemId);
        return ApplicationResponse.success();
    }

}
