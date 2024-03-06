package com.assignment.integration;

import com.assignment.aggregation.domain.BrandCategoryLowestPrice;
import com.assignment.aggregation.domain.BrandCategoryLowestPricePk;
import com.assignment.aggregation.domain.BrandCategoryLowestPriceRepository;
import com.assignment.aggregation.service.AggregationService;
import com.assignment.brand.domain.Brand;
import com.assignment.brand.domain.BrandRepository;
import com.assignment.category.domain.Category;
import com.assignment.category.domain.CategoryRepository;
import com.assignment.item.domain.Item;
import com.assignment.item.domain.ItemRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("[통합테스트] 데이터 집계 테스트")
class AggregationServiceTest extends IntegrationTest{

    @Autowired
    AggregationService aggregationService;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    BrandRepository brandRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    BrandCategoryLowestPriceRepository brandCategoryLowestPriceRepository;

    @AfterEach
    void clearData() {
        categoryRepository.deleteAll();
        brandRepository.deleteAll();
        itemRepository.deleteAll();
        brandCategoryLowestPriceRepository.deleteAll();
    }

    @DisplayName("특정 브랜드의 카테고리별 최저가 가격 정보를 집계하여 저장한다.")
    @Test
    void aggregate_brand_category_lowest_price_success() {
        Category category = categoryRepository.save(new Category("카테고리1"));
        Brand brand = brandRepository.save(new Brand("브랜드1"));
        Item lowerPriceItem = itemRepository.save(new Item("아이템1", 10000D, brand.getId(), category.getId()));
        Item higherPriceItem = itemRepository.save(new Item("아이템2", 20000D, brand.getId(), category.getId()));

        aggregationService.aggregateBrandCategoryLowestPriceByBrandId(brand.getId());

        BrandCategoryLowestPrice result = brandCategoryLowestPriceRepository.findById(new BrandCategoryLowestPricePk(brand.getId(), category.getId())).get();

        assertAll(() -> {
            assertThat(result.getBrandName()).isEqualTo(brand.getName());
            assertThat(result.getCategoryName()).isEqualTo(category.getName());
            assertThat(result.getPrice()).isEqualTo(lowerPriceItem.getPrice());
        });
    }

    @DisplayName("특정 브랜드의 카테고리별 최저가 가격 정보를 집계하여 저장할때 이미 동일한 key를 가진 정보가 있을경우 정보를 업데이트한다.")
    @Test
    void reaggregate_brand_category_lowest_price_success() {
        Category category = categoryRepository.save(new Category("카테고리1"));
        Brand brand = brandRepository.save(new Brand("브랜드1"));
        Item item1 = itemRepository.save(new Item("아이템1", 10000D, brand.getId(), category.getId()));
        Item item2 = itemRepository.save(new Item("아이템2", 20000D, brand.getId(), category.getId()));
        aggregationService.aggregateBrandCategoryLowestPriceByBrandId(brand.getId());

        Item lowestPriceItem = itemRepository.save(new Item("아이템3", 7000D, brand.getId(), category.getId()));
        aggregationService.aggregateBrandCategoryLowestPriceByBrandId(brand.getId());

        BrandCategoryLowestPrice result = brandCategoryLowestPriceRepository.findById(new BrandCategoryLowestPricePk(brand.getId(), category.getId())).get();

        assertAll(() -> {
            assertThat(result.getBrandName()).isEqualTo(brand.getName());
            assertThat(result.getCategoryName()).isEqualTo(category.getName());
            assertThat(result.getPrice()).isEqualTo(lowestPriceItem.getPrice());
        });
    }

    @DisplayName("브랜드의 정보가 존재하지 않을 경우 집계하지 않는다.")
    @Test
    void name() {
        aggregationService.aggregateBrandCategoryLowestPriceByBrandId(1L);
    }

}
