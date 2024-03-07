package com.assignment.aggregation.controller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class LowestTotalPriceBrandResponse {

    private final TotalPriceBrandResponse content;

    @JsonCreator
    public LowestTotalPriceBrandResponse(@JsonProperty("최저가") TotalPriceBrandResponse content) {
        this.content = content;
    }
}
