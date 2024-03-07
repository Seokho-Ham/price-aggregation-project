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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

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

    @Nested
    @DisplayName("브랜드의 카테고리 최저가 정보를 집계할 때")
    class LowestPriceBrandTest {

        @DisplayName("특정 브랜드의 전체 카테고리 최저가 정보를 집계한다.")
        @Test
        void aggregate_brand_all_category_lowest_price_success() {
            Category category1 = categoryRepository.save(new Category("상의"));
            Category category2 = categoryRepository.save(new Category("하의"));
            Brand brand = brandRepository.save(new Brand("브랜드1"));
            itemRepository.save(new Item("상의1", 10000D, brand.getId(), category1.getId()));
            itemRepository.save(new Item("상의2", 20000D, brand.getId(), category1.getId()));
            itemRepository.save(new Item("하의3", 10000D, brand.getId(), category2.getId()));
            itemRepository.save(new Item("하의4", 20000D, brand.getId(), category2.getId()));

            List<BrandLowestPriceInfo> entities = aggregationWriter.aggregateBrandLowestPriceInfoForOneBrandAndAllCategories(brand.getId());

            List<BrandLowestPriceInfo> result = brandLowestPriceInfoRepository.findAllByBrandIdOrderById(brand.getId());

            assertAll(() -> {
                assertThat(result).hasSize(2);
                assertThat(result).containsAll(entities);
            });
        }

        @DisplayName("특정 브랜드의 단일 카테고리의 최저가 정보를 집계한다.")
        @Test
        void aggregate_brand_single_category_lowest_price_success() {
            Category category = categoryRepository.save(new Category("상의"));
            Brand brand = brandRepository.save(new Brand("브랜드1"));
            Item item1 = itemRepository.save(new Item("상의1", 10000D, brand.getId(), category.getId()));
            Item item2 = itemRepository.save(new Item("상의2", 20000D, brand.getId(), category.getId()));

            aggregationWriter.aggregateBrandLowestPriceInfoForOneBrandAndOneCategory(brand.getId(), category.getId());

            BrandLowestPriceInfo result = brandLowestPriceInfoRepository.findAllByBrandIdOrderById(brand.getId()).get(0);

            assertAll(() -> {
                assertThat(result.getCategoryName()).isEqualTo(category.getName());
                assertThat(result.getBrandName()).isEqualTo(brand.getName());
                assertThat(result.getPrice()).isEqualTo(item1.getPrice());
            });
        }

        @DisplayName("상품정보가 없을 경우 빈배열을 반환한다.")
        @Test
        void aggregate_brand_category_lowest_price_fail() {
            Brand brand = brandRepository.save(new Brand("브랜드1"));

            List<BrandLowestPriceInfo> result = aggregationWriter.aggregateBrandLowestPriceInfoForOneBrandAndAllCategories(brand.getId());

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("카테고리의 최저가 브랜드 정보를 집계할 때")
    class CategoryLowestPriceBrandTest {
        @DisplayName("전체 카테고리를 대상으로 정보를 집계한다.")
        @Test
        void aggregate_all_category_lowest_price_brand_success() {
            Category category1 = categoryRepository.save(new Category("상의"));
            Category category2 = categoryRepository.save(new Category("하의"));
            Brand brand1 = brandRepository.save(new Brand("브랜드1"));
            Brand brand2 = brandRepository.save(new Brand("브랜드2"));
            Item lowerPriceItem1 = itemRepository.save(new Item("상의1", 10000D, brand1.getId(), category1.getId()));
            Item higherPriceItem1 = itemRepository.save(new Item("상의2", 20000D, brand2.getId(), category1.getId()));
            Item lowerPriceItem2 = itemRepository.save(new Item("하의1", 10000D, brand1.getId(), category2.getId()));
            Item higherPriceItem2 = itemRepository.save(new Item("하의2", 20000D, brand2.getId(), category2.getId()));

            List<CategoryLowestPriceBrand> categoryLowestPriceBrands = aggregationWriter.aggregateCategoryLowestPriceBrandForAllCategories();

            List<CategoryLowestPriceBrand> result = lowestPriceBrandRepository.findAll();

            assertAll(() -> {
                assertThat(result).hasSize(2);
                assertThat(result).containsAll(categoryLowestPriceBrands);
            });
        }

        @DisplayName("단일 카테고리를 대상으로 정보를 집계한다.")
        @Test
        void aggregate_single_category_lowest_price_brand_success() {
            Category category = categoryRepository.save(new Category("상의"));
            Brand brand1 = brandRepository.save(new Brand("브랜드1"));
            Brand brand2 = brandRepository.save(new Brand("브랜드2"));
            Item lowerPriceItem = itemRepository.save(new Item("상의1", 10000D, brand1.getId(), category.getId()));
            Item higherPriceItem = itemRepository.save(new Item("상의2", 20000D, brand2.getId(), category.getId()));

            aggregationWriter.aggregateCategoryLowestPriceBrandForCategory(category.getId());

            CategoryLowestPriceBrand result = lowestPriceBrandRepository.findAllByCategoryId(category.getId()).get(0);

            assertAll(() -> {
                assertThat(result.getCategoryId()).isEqualTo(category.getId());
                assertThat(result.getBrandId()).isEqualTo(brand1.getId());
                assertThat(result.getPrice()).isEqualTo(lowerPriceItem.getPrice());
            });
        }

        @DisplayName("상품정보가 없을 경우 빈배열을 반환한다.")
        @Test
        void aggregate_category_lowest_price_brand_fail() {
            categoryRepository.save(new Category("상의"));
            brandRepository.save(new Brand("브랜드1"));

            List<CategoryLowestPriceBrand> result = aggregationWriter.aggregateCategoryLowestPriceBrandForAllCategories();

            assertThat(result).isEmpty();
        }


    }

    @Nested
    @DisplayName("카테고리의 최고가 브랜드 정보를 집계할 때")
    class CategoryHighestPriceBrandTest {
        @DisplayName("전체 카테고리를 대상으로 정보를 집계한다.")
        @Test
        void aggregate_all_category_highest_price_brand_success() {
            Category category1 = categoryRepository.save(new Category("상의"));
            Category category2 = categoryRepository.save(new Category("하의"));
            Brand brand1 = brandRepository.save(new Brand("브랜드1"));
            Brand brand2 = brandRepository.save(new Brand("브랜드2"));
            Item lowerPriceItem1 = itemRepository.save(new Item("상의1", 10000D, brand1.getId(), category1.getId()));
            Item higherPriceItem1 = itemRepository.save(new Item("상의2", 20000D, brand2.getId(), category1.getId()));
            Item lowerPriceItem2 = itemRepository.save(new Item("하의1", 10000D, brand1.getId(), category2.getId()));
            Item higherPriceItem2 = itemRepository.save(new Item("하의2", 20000D, brand2.getId(), category2.getId()));

            List<CategoryHighestPriceBrand> entities = aggregationWriter.aggregateCategoryHighestPriceBrandForAllCategories();

            List<CategoryHighestPriceBrand> result = highestPriceBrandRepository.findAll();

            assertAll(() -> {
                assertThat(result).hasSize(2);
                assertThat(result).containsAll(entities);
            });
        }

        @DisplayName("단일 카테고리를 대상으로 정보를 집계한다.")
        @Test
        void aggregate_single_category_highest_price_brand_success() {
            Category category = categoryRepository.save(new Category("상의"));
            Brand brand1 = brandRepository.save(new Brand("브랜드1"));
            Brand brand2 = brandRepository.save(new Brand("브랜드2"));
            Item lowerPriceItem = itemRepository.save(new Item("상의1", 10000D, brand1.getId(), category.getId()));
            Item higherPriceItem = itemRepository.save(new Item("상의2", 20000D, brand2.getId(), category.getId()));

            aggregationWriter.aggregateCategoryHighestPriceBrandForCategory(category.getId());

            CategoryHighestPriceBrand result = highestPriceBrandRepository.findAllByCategoryId(category.getId()).get(0);

            assertAll(() -> {
                assertThat(result.getCategoryId()).isEqualTo(category.getId());
                assertThat(result.getBrandId()).isEqualTo(brand2.getId());
                assertThat(result.getPrice()).isEqualTo(higherPriceItem.getPrice());

            });
        }

        @DisplayName("상품정보가 없을 경우 빈배열을 반환한다.")
        @Test
        void aggregate_category_highest_price_brand_fail() {
            categoryRepository.save(new Category("상의"));
            brandRepository.save(new Brand("브랜드1"));

            List<CategoryHighestPriceBrand> result = aggregationWriter.aggregateCategoryHighestPriceBrandForAllCategories();

            assertThat(result).isEmpty();
        }
    }





}
