package com.assignment.integration;

import com.assignment.aggregation.domain.*;
import com.assignment.aggregation.service.AggregationWriter;
import com.assignment.brand.domain.Brand;
import com.assignment.brand.domain.BrandRepository;
import com.assignment.category.domain.Category;
import com.assignment.category.domain.CategoryRepository;
import com.assignment.item.domain.Item;
import com.assignment.item.domain.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;


@DisplayName("[통합테스트] 데이터 집계 테스트")
class AggregationWriterTest extends IntegrationTest{

    @Autowired
    AggregationWriter aggregationWriter;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    BrandRepository brandRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    BrandLowestPriceInfoRepository brandLowestPriceInfoRepository;

    @Autowired
    CategoryLowestPriceBrandRepository lowestPriceBrandRepository;

    @Autowired
    CategoryHighestPriceBrandRepository highestPriceBrandRepository;

    @BeforeEach
    void clearData() {
        categoryRepository.deleteAll();
        brandRepository.deleteAll();
        itemRepository.deleteAll();
        brandLowestPriceInfoRepository.deleteAll();
        lowestPriceBrandRepository.deleteAll();
        highestPriceBrandRepository.deleteAll();
    }

    @DisplayName("특정 브랜드의 카테고리별 최저가 가격 정보를 집계하여 저장한다.")
    @Test
    void aggregate_brand_category_lowest_price_success() {
        Category category = categoryRepository.save(new Category("카테고리1"));
        Brand brand = brandRepository.save(new Brand("브랜드1"));
        Item lowerPriceItem = itemRepository.save(new Item("아이템1", 10000D, brand.getId(), category.getId()));
        Item higherPriceItem = itemRepository.save(new Item("아이템2", 20000D, brand.getId(), category.getId()));

        aggregationWriter.aggregateAllBrandLowestPriceInfoByBrandId(brand.getId());

        BrandLowestPriceInfo result = brandLowestPriceInfoRepository.findByBrandIdAndCategoryId(brand.getId(), category.getId()).get();

        assertAll(() -> {
            assertThat(result.getBrandName()).isEqualTo(brand.getName());
            assertThat(result.getCategoryName()).isEqualTo(category.getName());
            assertThat(result.getPrice()).isEqualTo(lowerPriceItem.getPrice());
        });
    }

    @DisplayName("카테고리별로 최저가 브랜드의 정보를 집계한다.")
    @Test
    void aggregate_category_lowest_price_brand_success() {
        Category category = categoryRepository.save(new Category("상의"));
        Brand brand1 = brandRepository.save(new Brand("브랜드1"));
        Brand brand2 = brandRepository.save(new Brand("브랜드2"));
        Item lowerPriceItem = itemRepository.save(new Item("상의1", 10000D, brand1.getId(), category.getId()));
        Item higherPriceItem = itemRepository.save(new Item("상의2", 20000D, brand2.getId(), category.getId()));

        aggregationWriter.aggregateCategoryLowestPriceBrand();

        CategoryLowestPriceBrand categoryLowestPriceBrand = lowestPriceBrandRepository.findAllByCategoryId(category.getId()).get(0);

        assertAll(() -> {
            assertThat(categoryLowestPriceBrand.getCategoryId()).isEqualTo(category.getId());
            assertThat(categoryLowestPriceBrand.getBrandId()).isEqualTo(brand1.getId());
            assertThat(categoryLowestPriceBrand.getPrice()).isEqualTo(lowerPriceItem.getPrice());
        });

    }

    @DisplayName("카테고리별로 최고가 브랜드의 정보를 집계한다.")
    @Test
    void aggregate_category_highest_price_brand_success() {
        Category category = categoryRepository.save(new Category("상의"));
        Brand brand1 = brandRepository.save(new Brand("브랜드1"));
        Brand brand2 = brandRepository.save(new Brand("브랜드2"));
        Item lowerPriceItem = itemRepository.save(new Item("상의1", 10000D, brand1.getId(), category.getId()));
        Item higherPriceItem = itemRepository.save(new Item("상의2", 20000D, brand2.getId(), category.getId()));

        aggregationWriter.aggregateCategoryHighestPriceBrand();

        CategoryHighestPriceBrand categoryHighestPriceBrand = highestPriceBrandRepository.findAllByCategoryId(category.getId()).get(0);

        assertAll(() -> {
            assertThat(categoryHighestPriceBrand.getCategoryId()).isEqualTo(category.getId());
            assertThat(categoryHighestPriceBrand.getBrandId()).isEqualTo(brand2.getId());
            assertThat(categoryHighestPriceBrand.getPrice()).isEqualTo(higherPriceItem.getPrice());

        });

    }


}
