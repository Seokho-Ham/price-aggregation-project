package com.assignment.aggregation.service;

import com.assignment.aggregation.controller.dto.CategoryPriceResponse;
import com.assignment.aggregation.controller.dto.LowestTotalPriceBrandResponse;
import com.assignment.aggregation.controller.dto.TotalPriceBrandResponse;
import com.assignment.aggregation.domain.BrandLowestPriceInfo;
import com.assignment.aggregation.domain.BrandLowestPriceInfoRepository;
import com.assignment.aggregation.domain.BrandTotalPrice;
import com.assignment.aggregation.exception.LowestTotalPriceBrandNotFoundException;
import com.assignment.aggregation.repository.AggregationCacheRepository;
import com.assignment.aggregation.repository.AggregationQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AggregationService {

    private final AggregationWriter aggregationWriter;
    private final AggregationCacheRepository aggregationCacheRepository;
    private final BrandLowestPriceInfoRepository brandLowestPriceInfoRepository;
    private final AggregationQueryRepository aggregationQueryRepository;

    @Transactional(readOnly = true)
    public LowestTotalPriceBrandResponse getLowestTotalBrand() {
        List<BrandLowestPriceInfo> brandLowestPriceData = aggregationCacheRepository.getLowestTotalPriceBrand()
            .orElseGet(() -> {
                BrandTotalPrice brandPrice = aggregationQueryRepository.getLowestTotalPriceBrand();
                return brandLowestPriceInfoRepository.findAllById_BrandId(brandPrice.getBrandId());
            });

        if (brandLowestPriceData.isEmpty()) {
            throw new LowestTotalPriceBrandNotFoundException();
        }

        String brandName = brandLowestPriceData.stream().map(BrandLowestPriceInfo::getBrandName).findFirst().get();
        Double totalPrice = brandLowestPriceData.stream().mapToDouble(BrandLowestPriceInfo::getPrice).sum();
        List<CategoryPriceResponse> categories = brandLowestPriceData.stream()
            .map(data -> new CategoryPriceResponse(data.getCategoryName(), data.getPrice()))
            .toList();

        return new LowestTotalPriceBrandResponse(new TotalPriceBrandResponse(brandName, categories, totalPrice));
    }

    @Transactional
    public void aggregateOnBrandCreate(Long brandId) {
        aggregationWriter.aggregateBrandLowestPriceInfoByBrandId(brandId);
    }
    @Transactional
    public void aggregateOnBrandUpdate() {
        aggregationWriter.aggregateCategoryLowestPriceBrand();
        aggregationWriter.aggregateCategoryHighestPriceBrand();
    }
    @Transactional
    public void aggregateOnBrandDelete(Long brandId) {
        aggregationWriter.deleteBrandLowestPriceInfoByBrandId(brandId);
        aggregationWriter.aggregateCategoryLowestPriceBrand();
        aggregationWriter.aggregateCategoryHighestPriceBrand();
    }


}
