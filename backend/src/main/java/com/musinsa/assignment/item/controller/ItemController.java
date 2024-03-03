package com.musinsa.assignment.item.controller;

import com.musinsa.assignment.common.dto.ApplicationResponse;
import com.musinsa.assignment.item.controller.dto.ItemCreateRequest;
import com.musinsa.assignment.item.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
