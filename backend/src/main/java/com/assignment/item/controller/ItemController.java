package com.assignment.item.controller;

import com.assignment.item.controller.dto.ItemUpdateRequest;
import com.assignment.item.service.ItemService;
import com.assignment.common.dto.ApplicationResponse;
import com.assignment.item.controller.dto.ItemCreateRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "상품 API")
@Validated
@RequiredArgsConstructor
@RequestMapping("/items")
@RestController
public class ItemController {

    private final ItemService itemService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ApplicationResponse<Void> createItem(@RequestBody @Valid ItemCreateRequest request) {
        itemService.create(request.toDto());
        return ApplicationResponse.success();
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{itemId}")
    public ApplicationResponse<Void> updateItem(@PathVariable("itemId") @Positive Long itemId, @RequestBody @Valid ItemUpdateRequest request) {
        itemService.update(request.toDto(itemId));
        return ApplicationResponse.success();
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{itemId}")
    public ApplicationResponse<Void> deleteItem(@PathVariable("itemId") @Positive Long itemId) {
        itemService.delete(itemId);
        return ApplicationResponse.success();
    }

}
