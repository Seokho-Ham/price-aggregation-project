package com.assignment.aggregation.service;

import com.assignment.aggregation.domain.BrandCategoryLowestPrice;
import com.assignment.aggregation.domain.BrandCategoryLowestPriceRepository;
import com.assignment.aggregation.repository.AggregationQueryRepository;
import com.assignment.aggregation.repository.dto.BrandCategoryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class AggregationService {

    private final AggregationQueryRepository aggregationQueryRepository;
    private final BrandCategoryLowestPriceRepository brandCategoryLowestPriceRepository;

    /**
     * 기능: 전달받은 brandId에 해당하는 브랜드의 카테고리별 최저가 정보를 집계하는 로직
     */
    @Transactional
    public void aggregateBrandCategoryLowestPriceByBrandId(Long brandId) {
        /*
        1. 해당 브랜드에서 각 카테고리별로 최저가 상품의 가격정보를 조회.
        2. 테이블에 저장 -> 만약 이미 있으면 업데이트됨
         */
        List<BrandCategoryDto> dtos = aggregationQueryRepository.findByBrandLowestPriceByCategoryId(brandId);

        if (dtos.isEmpty()) {
            log.error("[aggregate-brand_category_lowest_price] 정보가 존재하지 않아 집계에 실패.");
            return;
        }

        List<BrandCategoryLowestPrice> brandCategoryLowestPriceEntities = dtos.stream()
            .map(dto -> new BrandCategoryLowestPrice(dto.getBrandId(), dto.getCategoryId(), dto.getBrandName(), dto.getCategoryName(), dto.getPrice()))
            .toList();
        brandCategoryLowestPriceRepository.saveAll(brandCategoryLowestPriceEntities);
    }

}
