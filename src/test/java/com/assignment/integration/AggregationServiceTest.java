package com.assignment.integration;

import com.assignment.aggregation.controller.dto.CategoriesLowestPriceBrandsResponse;
import com.assignment.aggregation.controller.dto.CategoryLowestAndHighestBrandResponse;
import com.assignment.aggregation.controller.dto.LowestTotalPriceBrandResponse;
import com.assignment.aggregation.domain.*;
import com.assignment.aggregation.exception.CategoryHighestPriceBrandNotFoundException;
import com.assignment.aggregation.exception.CategoryLowestPriceBrandNotFoundException;
import com.assignment.aggregation.exception.LowestTotalPriceBrandNotFoundException;
import com.assignment.aggregation.service.AggregationService;
import com.assignment.brand.domain.Brand;
import com.assignment.brand.domain.BrandRepository;
import com.assignment.category.domain.Category;
import com.assignment.category.domain.CategoryRepository;
import com.assignment.category.exception.CategoryNotFoundException;
import com.assignment.item.domain.Item;
import com.assignment.item.domain.ItemRepository;
import com.assignment.item.service.dto.ItemDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("[통합테스트] 집계 데이터 조회 테스트")
class AggregationServiceTest extends IntegrationTest {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    BrandRepository brandRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    BrandTotalPriceRepository brandTotalPriceRepository;

    @Autowired
    BrandLowestPriceInfoRepository brandLowestPriceInfoRepository;

    @Autowired
    CategoryHighestPriceBrandRepository categoryHighestPriceBrandRepository;

    @Autowired
    CategoryLowestPriceBrandRepository categoryLowestPriceBrandRepository;

    @Autowired
    AggregationService aggregationService;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    void clearRedisCache() {
        Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection().serverCommands().flushAll();
    }

    @Nested
    @DisplayName("집계 데이터 중")
    class SearchFromCacheTest {

        @BeforeEach
        void setData() {
            setBaseData();
            aggregationService.aggregateAllDatas();
        }

        @AfterEach
        void clearData() {
            clearDatabase();
            clearRedisCache();
        }

        @Nested
        @DisplayName("브랜드의 카테고리별 최저가 총액 정보를 조회할 때")
        class SearchLowestTotalPriceBrandTest {

            String lowestBrandName = "브랜드1";

            @DisplayName("캐시 저장소에서 데이터를 가져온다.")
            @Test
            void get_data_from_cache() {
                LowestTotalPriceBrandResponse result = aggregationService.getLowestTotalBrand();

                assertAll(() -> {
                    assertThat(result.getContent().getBrandName()).isEqualTo(lowestBrandName);
                    assertThat(result.getContent().getCategories()).hasSize(2);
                    assertThat(result.getContent().getTotalPrice()).isEqualTo(2000D);
                });
            }

            @DisplayName("캐시 저장소에 데이터가 없을 경우 DB로부터 데이터를 가져온다.")
            @Test
            void get_data_from_database() {
                clearRedisCache();

                LowestTotalPriceBrandResponse result = aggregationService.getLowestTotalBrand();

                assertAll(() -> {
                    assertThat(result.getContent().getBrandName()).isEqualTo(lowestBrandName);
                    assertThat(result.getContent().getCategories()).hasSize(2);
                    assertThat(result.getContent().getTotalPrice()).isEqualTo(2000D);
                });
            }

            @DisplayName("집계 데이터가 존재하지 않을 경우 예외를 반환한다.")
            @Test
            void data_not_found() {
                brandTotalPriceRepository.deleteAll();
                brandLowestPriceInfoRepository.deleteAll();
                clearRedisCache();

                assertThatThrownBy(() -> aggregationService.getLowestTotalBrand())
                    .isInstanceOf(LowestTotalPriceBrandNotFoundException.class);
            }
        }

        @Nested
        @DisplayName("카테고리의 최저가, 최고가 브랜드 정보를 조회할 때")
        class SearchCategoryLowestAndHighestPriceBrandTest {

            String categoryName = "상의";
            String lowestBrandName = "브랜드1";
            String highestBrandName = "브랜드3";

            @DisplayName("전달된 카테고리명에 해당하는 데이터를 캐시 저장소에서 가져온다.")
            @Test
            void get_data_from_cache() {
                CategoryLowestAndHighestBrandResponse result = aggregationService.getCategoryLowestAndHighestPriceBrand("상의");

                System.out.println(result.getLowestPriceBrand().get(0).getBrandName());
                System.out.println(result.getHighestPriceBrand().get(0).getBrandName());

                assertAll(() -> {
                    assertThat(result.getCategoryName()).isEqualTo(categoryName);
                    assertThat(result.getLowestPriceBrand().get(0).getBrandName()).isEqualTo(lowestBrandName);
                    assertThat(result.getHighestPriceBrand().get(0).getBrandName()).isEqualTo(highestBrandName);
                });
            }

            @DisplayName("전달된 카테고리명에 해당하는 데이터가 캐시 저장소에 없을 경우 DB로부터 데이터를 가져온다.")
            @Test
            void get_data_from_database() {
                clearRedisCache();

                CategoryLowestAndHighestBrandResponse result = aggregationService.getCategoryLowestAndHighestPriceBrand("상의");

                assertAll(() -> {
                    assertThat(result.getCategoryName()).isEqualTo(categoryName);
                    assertThat(result.getLowestPriceBrand().get(0).getBrandName()).isEqualTo(lowestBrandName);
                    assertThat(result.getHighestPriceBrand().get(0).getBrandName()).isEqualTo(highestBrandName);
                });
            }

            @DisplayName("전달된 카테고리명의 카테고리 정보가 존재하지 않을 경우 예외를 반환한다.")
            @Test
            void category_not_found() {
                assertThatThrownBy(() -> aggregationService.getCategoryLowestAndHighestPriceBrand("없는 카테고리"))
                    .isInstanceOf(CategoryNotFoundException.class);
            }

            @DisplayName("최저가 집계 데이터가 존재하지 않을 경우 예외를 반환한다.")
            @Test
            void lowest_data_not_found() {
                clearRedisCache();
                categoryLowestPriceBrandRepository.deleteAll();

                assertThatThrownBy(() -> aggregationService.getCategoryLowestAndHighestPriceBrand(categoryName))
                    .isInstanceOf(CategoryLowestPriceBrandNotFoundException.class);
            }

            @DisplayName("최고가 집계 데이터가 존재하지 않을 경우 예외를 반환한다.")
            @Test
            void highest_data_not_found() {
                clearRedisCache();
                categoryHighestPriceBrandRepository.deleteAll();

                assertThatThrownBy(() -> aggregationService.getCategoryLowestAndHighestPriceBrand(categoryName))
                    .isInstanceOf(CategoryHighestPriceBrandNotFoundException.class);
            }
        }

        @Nested
        @DisplayName("전체 카테고리별 최저가 브랜드 정보를 조회할 때")
        class SearchAllCategoryLowestPriceBrandsTest {
            @DisplayName("캐시 저장소에서 데이터를 가져온다.")
            @Test
            void get_data_from_cache() {
                CategoriesLowestPriceBrandsResponse result = aggregationService.getCategoriesLowestPriceBrands();

                assertAll(() -> {
                    assertThat(result.getContent()).hasSize(2);
                    assertThat(result.getTotalPrice()).isEqualTo(2000);
                });
            }

            @DisplayName("캐시 저장소에 데이터가 없을 경우 DB로부터 데이터를 가져온다.")
            @Test
            void get_data_from_database() {
                clearRedisCache();

                CategoriesLowestPriceBrandsResponse result = aggregationService.getCategoriesLowestPriceBrands();

                assertAll(() -> {
                    assertThat(result.getContent()).hasSize(2);
                    assertThat(result.getTotalPrice()).isEqualTo(2000);
                });
            }

            @DisplayName("집계 데이터가 존재하지 않을 경우 예외를 반환한다.")
            @Test
            void data_not_found() {
                clearRedisCache();
                categoryLowestPriceBrandRepository.deleteAll();

                assertThatThrownBy(() -> aggregationService.getCategoriesLowestPriceBrands())
                    .isInstanceOf(CategoryLowestPriceBrandNotFoundException.class);
            }
        }
    }

    @Nested
    @DisplayName("브랜드 정보에 변경이 발생했을때")
    class HandleBrandEventTest {

        @BeforeEach
        void clearData() {
            clearDatabase();
        }

        @DisplayName("브랜드 생성 시 브랜드의 카테고리별 데이터를 집계한다.")
        @Test
        void create_brand() {
            Category category = categoryRepository.save(new Category("상의"));
            Brand brand = brandRepository.save(new Brand("브랜드1"));
            Item item = itemRepository.save(new Item("상품1", 1000D, brand.getId(), category.getId()));

            aggregationService.aggregateOnBrandCreate(brand.getId());

            BrandLowestPriceInfo result = brandLowestPriceInfoRepository.findByBrandIdAndCategoryId(brand.getId(), category.getId()).get();

            assertAll(() -> {
                assertThat(result.getBrandId()).isEqualTo(brand.getId());
                assertThat(result.getBrandId()).isEqualTo(brand.getId());
                assertThat(result.getPrice()).isEqualTo(item.getPrice());
            });
        }

        @DisplayName("브랜드 수정 시 수정된 브랜드의 카테고리별 데이터와 카테고리 최저가, 최고가 데이터를 집계한다.")
        @Test
        void update_brand() {
            Category category = categoryRepository.save(new Category("상의"));
            Brand brand = brandRepository.save(new Brand("브랜드1"));
            Item item = itemRepository.save(new Item("상품1", 1000D, brand.getId(), category.getId()));
            aggregationService.aggregateAllDatas();

            brand.updateName("새로운 브랜드명");
            brandRepository.save(brand);

            aggregationService.aggregateOnBrandUpdate(brand.getId());

            BrandLowestPriceInfo brandLowestPriceInfo = brandLowestPriceInfoRepository.findByBrandIdAndCategoryId(brand.getId(), category.getId()).get();
            CategoryLowestPriceBrand categoryLowestPriceBrand = categoryLowestPriceBrandRepository.findAllByCategoryId(category.getId()).get(0);
            CategoryHighestPriceBrand categoryHighestPriceBrand = categoryHighestPriceBrandRepository.findAllByCategoryId(category.getId()).get(0);

            assertAll(() -> {
                assertThat(brandLowestPriceInfo.getBrandName()).isEqualTo(brand.getName());
                assertThat(categoryLowestPriceBrand.getBrandName()).isEqualTo(brand.getName());
                assertThat(categoryHighestPriceBrand.getBrandName()).isEqualTo(brand.getName());
            });
        }

        @DisplayName("브랜드 삭제 시 해당 브랜드의 카테고리별 데이터는 삭제하며 카테고리 최저가, 최고가 정보를 재집계한다.")
        @Test
        void delete_brand() {
            Category category = categoryRepository.save(new Category("상의"));
            Brand brand = brandRepository.save(new Brand("브랜드1"));
            Item item = itemRepository.save(new Item("상품1", 1000D, brand.getId(), category.getId()));
            aggregationService.aggregateAllDatas();

            brand.delete();
            brandRepository.save(brand);

            aggregationService.aggregateOnBrandDelete(brand.getId());

            List<BrandLowestPriceInfo> brandLowestPriceInfo = brandLowestPriceInfoRepository.findAllByBrandIdOrderById(brand.getId());
            List<CategoryLowestPriceBrand> categoryLowestPriceBrand = categoryLowestPriceBrandRepository.findAllByCategoryId(category.getId());
            List<CategoryHighestPriceBrand> categoryHighestPriceBrand = categoryHighestPriceBrandRepository.findAllByCategoryId(category.getId());

            assertAll(() -> {
                assertThat(brandLowestPriceInfo).isEmpty();
                assertThat(categoryLowestPriceBrand).isEmpty();
                assertThat(categoryHighestPriceBrand).isEmpty();
            });

        }
    }

    @Nested
    @DisplayName("상품 정보에 변경이 발생했을때")
    class HandleItemEventTest {

        @BeforeEach
        void clearData() {
            clearDatabase();
        }

        @Nested
        @DisplayName("상품 생성 시 ")
        class ItemCreateTest {
            @DisplayName("해당 상품의 가격이 현재 카테고리의 최저가와 같거나 낮을 경우 해당 카테고리 최저가 정보를 재집계한다.")
            @Test
            void create_item_aggregate_category_lowest_price() {
                Category category = categoryRepository.save(new Category("상의"));
                Brand brand = brandRepository.save(new Brand("브랜드1"));
                Item item1 = itemRepository.save(new Item("상품1", 1000D, brand.getId(), category.getId()));
                aggregationService.aggregateAllDatas();

                CategoryLowestPriceBrand beforeResult = categoryLowestPriceBrandRepository.findAllByCategoryId(category.getId()).get(0);

                Item item2 = itemRepository.save(new Item("상품2", 500D, brand.getId(), category.getId()));
                aggregationService.aggregateOnItemCreate(new ItemDto(brand.getId(), category.getId(), item2.getPrice()));

                CategoryLowestPriceBrand afterResult = categoryLowestPriceBrandRepository.findAllByCategoryId(category.getId()).get(0);

                assertAll(() -> {
                    assertThat(beforeResult.getPrice()).isEqualTo(item1.getPrice());
                    assertThat(afterResult.getPrice()).isEqualTo(item2.getPrice());
                });
            }

            @DisplayName("해당 상품의 가격이 현재 카테고리의 최저가보다 높을 경우 해당 카테고리 최저가 정보를 집계하지 않는다.")
            @Test
            void create_item_not_aggregate_category_lowest_price() {
                Category category = categoryRepository.save(new Category("상의"));
                Brand brand = brandRepository.save(new Brand("브랜드1"));
                Item item1 = itemRepository.save(new Item("상품1", 1000D, brand.getId(), category.getId()));
                aggregationService.aggregateAllDatas();

                CategoryLowestPriceBrand beforeResult = categoryLowestPriceBrandRepository.findAllByCategoryId(category.getId()).get(0);

                Item item2 = itemRepository.save(new Item("상품2", 1500D, brand.getId(), category.getId()));
                aggregationService.aggregateOnItemCreate(new ItemDto(brand.getId(), category.getId(), item2.getPrice()));

                CategoryLowestPriceBrand afterResult = categoryLowestPriceBrandRepository.findAllByCategoryId(category.getId()).get(0);

                assertThat(beforeResult.getPrice()).isEqualTo(afterResult.getPrice());
            }

            @DisplayName("해당 상품의 가격이 현재 카테고리의 최고가와 같거나 높을 경우 해당 카테고리 최고가 정보를 재집계한다.")
            @Test
            void create_item_aggregate_category_highest_price() {
                Category category = categoryRepository.save(new Category("상의"));
                Brand brand = brandRepository.save(new Brand("브랜드1"));
                Item item1 = itemRepository.save(new Item("상품1", 1000D, brand.getId(), category.getId()));
                aggregationService.aggregateAllDatas();

                CategoryLowestPriceBrand beforeResult = categoryLowestPriceBrandRepository.findAllByCategoryId(category.getId()).get(0);

                Item item2 = itemRepository.save(new Item("상품2", 2000D, brand.getId(), category.getId()));
                aggregationService.aggregateOnItemCreate(new ItemDto(brand.getId(), category.getId(), item2.getPrice()));

                CategoryHighestPriceBrand afterResult = categoryHighestPriceBrandRepository.findAllByCategoryId(category.getId()).get(0);

                assertAll(() -> {
                    assertThat(beforeResult.getPrice()).isEqualTo(item1.getPrice());
                    assertThat(afterResult.getPrice()).isEqualTo(item2.getPrice());
                });
            }

            @DisplayName("해당 상품의 가격이 현재 카테고리의 최고가보다 낮을 경우 해당 카테고리 최고가 정보를 집계하지 않는다.")
            @Test
            void create_item_not_aggregate_category_highest_price() {
                Category category = categoryRepository.save(new Category("상의"));
                Brand brand = brandRepository.save(new Brand("브랜드1"));
                Item item1 = itemRepository.save(new Item("상품1", 1000D, brand.getId(), category.getId()));
                aggregationService.aggregateAllDatas();

                CategoryLowestPriceBrand beforeResult = categoryLowestPriceBrandRepository.findAllByCategoryId(category.getId()).get(0);

                Item item2 = itemRepository.save(new Item("상품2", 900D, brand.getId(), category.getId()));
                aggregationService.aggregateOnItemCreate(new ItemDto(brand.getId(), category.getId(), item2.getPrice()));

                CategoryHighestPriceBrand afterResult = categoryHighestPriceBrandRepository.findAllByCategoryId(category.getId()).get(0);

                assertThat(beforeResult.getPrice()).isEqualTo(afterResult.getPrice());
            }

            @DisplayName("해당 상품의 가격이 현재 브랜드의 최저가 카테고리 가격보다 낮을 경우 해당 정보를 재집계한다.")
            @Test
            void create_item_aggregate_brand_lowest_price() {
                Category category = categoryRepository.save(new Category("상의"));
                Brand brand = brandRepository.save(new Brand("브랜드1"));
                Item item1 = itemRepository.save(new Item("상품1", 1000D, brand.getId(), category.getId()));
                aggregationService.aggregateAllDatas();

                Item item2 = itemRepository.save(new Item("상품2", 500D, brand.getId(), category.getId()));
                BrandLowestPriceInfo beforeResult = brandLowestPriceInfoRepository.findAllByBrandIdOrderById(brand.getId()).get(0);

                aggregationService.aggregateOnItemCreate(new ItemDto(brand.getId(), category.getId(), item2.getPrice()));

                BrandLowestPriceInfo afterResult = brandLowestPriceInfoRepository.findAllByBrandIdOrderById(brand.getId()).get(0);

                assertAll(() -> {
                    assertThat(beforeResult.getPrice()).isEqualTo(item1.getPrice());
                    assertThat(afterResult.getPrice()).isEqualTo(item2.getPrice());
                });
            }

            @DisplayName("해당 상품의 가격이 현재 브랜드의 최저가 카테고리 가격보다 높을 경우 해당 정보를 집계하지 않는다.")
            @Test
            void create_item_not_aggregate_brand_lowest_price() {
                Category category = categoryRepository.save(new Category("상의"));
                Brand brand = brandRepository.save(new Brand("브랜드1"));
                Item item1 = itemRepository.save(new Item("상품1", 1000D, brand.getId(), category.getId()));
                aggregationService.aggregateAllDatas();

                Item item2 = itemRepository.save(new Item("상품2", 1500D, brand.getId(), category.getId()));
                BrandLowestPriceInfo beforeResult = brandLowestPriceInfoRepository.findAllByBrandIdOrderById(brand.getId()).get(0);

                aggregationService.aggregateOnItemCreate(new ItemDto(brand.getId(), category.getId(), item2.getPrice()));

                BrandLowestPriceInfo afterResult = brandLowestPriceInfoRepository.findAllByBrandIdOrderById(brand.getId()).get(0);

                assertThat(beforeResult.getPrice()).isEqualTo(afterResult.getPrice());
            }
        }

        @Nested
        @DisplayName("상품 수정 시 ")
        class ItemUpdateTest {

            @DisplayName("해당 상품이 속한 카테고리 최저가 정보를 재집계한다.")
            @Test
            void update_item_aggregate_category_lowest_price() {
                Category category = categoryRepository.save(new Category("상의"));
                Brand brand = brandRepository.save(new Brand("브랜드1"));
                Item item = itemRepository.save(new Item("상품1", 1000D, brand.getId(), category.getId()));
                aggregationService.aggregateAllDatas();

                CategoryLowestPriceBrand beforeResult = categoryLowestPriceBrandRepository.findAllByCategoryId(category.getId()).get(0);

                item.update("상품2", 500D);
                itemRepository.save(item);
                aggregationService.aggregateOnItemUpdate(new ItemDto(brand.getId(), category.getId(), item.getPrice()));

                CategoryLowestPriceBrand afterResult = categoryLowestPriceBrandRepository.findAllByCategoryId(category.getId()).get(0);

                assertAll(() -> {
                    assertThat(beforeResult.getPrice()).isEqualTo(1000D);
                    assertThat(afterResult.getPrice()).isEqualTo(500D);
                });
            }

            @DisplayName("해당 상품이 속한 카테고리 최고가 정보를 재집계한다.")
            @Test
            void update_item_aggregate_category_highest_price() {
                Category category = categoryRepository.save(new Category("상의"));
                Brand brand = brandRepository.save(new Brand("브랜드1"));
                Item item = itemRepository.save(new Item("상품1", 1000D, brand.getId(), category.getId()));
                aggregationService.aggregateAllDatas();

                CategoryLowestPriceBrand beforeResult = categoryLowestPriceBrandRepository.findAllByCategoryId(category.getId()).get(0);

                item.update("상품2", 2000D);
                itemRepository.save(item);
                aggregationService.aggregateOnItemUpdate(new ItemDto(brand.getId(), category.getId(), item.getPrice()));

                CategoryHighestPriceBrand afterResult = categoryHighestPriceBrandRepository.findAllByCategoryId(category.getId()).get(0);

                assertAll(() -> {
                    assertThat(beforeResult.getPrice()).isEqualTo(1000D);
                    assertThat(afterResult.getPrice()).isEqualTo(2000D);
                });
            }

            @DisplayName("해당 브랜드의 해당 카테고리 최저가 정보를 재집계한다.")
            @Test
            void update_item_aggregate_brand_lowest_price() {
                Category category = categoryRepository.save(new Category("상의"));
                Brand brand = brandRepository.save(new Brand("브랜드1"));
                Item item = itemRepository.save(new Item("상품1", 1000D, brand.getId(), category.getId()));
                aggregationService.aggregateAllDatas();

                item.update("상품2", 500D);
                itemRepository.save(item);
                BrandLowestPriceInfo beforeResult = brandLowestPriceInfoRepository.findAllByBrandIdOrderById(brand.getId()).get(0);

                aggregationService.aggregateOnItemUpdate(new ItemDto(brand.getId(), category.getId(), item.getPrice()));

                BrandLowestPriceInfo afterResult = brandLowestPriceInfoRepository.findAllByBrandIdOrderById(brand.getId()).get(0);

                assertAll(() -> {
                    assertThat(beforeResult.getPrice()).isEqualTo(1000D);
                    assertThat(afterResult.getPrice()).isEqualTo(500D);
                });
            }
        }

        @Nested
        @DisplayName("상품 삭제 시 ")
        class ItemDeleteTest {
            @DisplayName("해당 상품이 속한 현재 카테고리 최저가와 동일한 가격일 경우 가격 정보를 재집계한다.")
            @Test
            void delete_item_aggregate_category_lowest_price() {
                Category category = categoryRepository.save(new Category("상의"));
                Brand brand = brandRepository.save(new Brand("브랜드1"));
                Item item1 = itemRepository.save(new Item("상품1", 1000D, brand.getId(), category.getId()));
                Item item2 = itemRepository.save(new Item("상품2", 2000D, brand.getId(), category.getId()));
                aggregationService.aggregateAllDatas();

                CategoryLowestPriceBrand beforeResult = categoryLowestPriceBrandRepository.findAllByCategoryId(category.getId()).get(0);

                item1.delete();
                itemRepository.save(item1);
                aggregationService.aggregateOnItemDelete(new ItemDto(brand.getId(), category.getId(), item1.getPrice()));

                CategoryLowestPriceBrand afterResult = categoryLowestPriceBrandRepository.findAllByCategoryId(category.getId()).get(0);

                assertAll(() -> {
                    assertThat(beforeResult.getPrice()).isEqualTo(item1.getPrice());
                    assertThat(afterResult.getPrice()).isEqualTo(item2.getPrice());
                });
            }

            @DisplayName("해당 상품이 속한 현재 카테고리 최저가와 동일하지 않은 가격일 경우 데이터를 삭제하고 가격 정보를 집계하지 않는다.")
            @Test
            void delete_item_not_aggregate_category_lowest_price() {
                Category category = categoryRepository.save(new Category("상의"));
                Brand brand = brandRepository.save(new Brand("브랜드1"));
                Item item1 = itemRepository.save(new Item("상품1", 1000D, brand.getId(), category.getId()));
                Item item2 = itemRepository.save(new Item("상품2", 2000D, brand.getId(), category.getId()));
                aggregationService.aggregateAllDatas();

                CategoryLowestPriceBrand beforeResult = categoryLowestPriceBrandRepository.findAllByCategoryId(category.getId()).get(0);

                item2.delete();
                itemRepository.save(item2);
                aggregationService.aggregateOnItemDelete(new ItemDto(brand.getId(), category.getId(), item2.getPrice()));

                CategoryLowestPriceBrand afterResult = categoryLowestPriceBrandRepository.findAllByCategoryId(category.getId()).get(0);

                assertAll(() -> {
                    assertThat(beforeResult.getPrice()).isEqualTo(item1.getPrice());
                    assertThat(beforeResult.getPrice()).isEqualTo(afterResult.getPrice());
                });

            }

            @DisplayName("해당 상품이 속한 현재 카테고리 최고가와 동일한 가격일 경우 가격 정보를 재집계한다.")
            @Test
            void delete_item_aggregate_category_highest_price() {
                Category category = categoryRepository.save(new Category("상의"));
                Brand brand = brandRepository.save(new Brand("브랜드1"));
                Item item1 = itemRepository.save(new Item("상품1", 1000D, brand.getId(), category.getId()));
                Item item2 = itemRepository.save(new Item("상품2", 2000D, brand.getId(), category.getId()));
                aggregationService.aggregateAllDatas();

                CategoryHighestPriceBrand beforeResult = categoryHighestPriceBrandRepository.findAllByCategoryId(category.getId()).get(0);

                item2.delete();
                itemRepository.save(item2);
                aggregationService.aggregateOnItemUpdate(new ItemDto(brand.getId(), category.getId(), item2.getPrice()));

                CategoryHighestPriceBrand afterResult = categoryHighestPriceBrandRepository.findAllByCategoryId(category.getId()).get(0);

                assertAll(() -> {
                    assertThat(beforeResult.getPrice()).isEqualTo(item2.getPrice());
                    assertThat(afterResult.getPrice()).isEqualTo(item1.getPrice());
                });
            }

            @DisplayName("해당 상품이 속한 현재 카테고리 최고가와 동일하지 않은 가격일 경우 가격 정보를 집계하지 않는다.")
            @Test
            void delete_item_not_aggregate_category_highest_price() {
                Category category = categoryRepository.save(new Category("상의"));
                Brand brand = brandRepository.save(new Brand("브랜드1"));
                Item item1 = itemRepository.save(new Item("상품1", 1000D, brand.getId(), category.getId()));
                Item item2 = itemRepository.save(new Item("상품2", 2000D, brand.getId(), category.getId()));
                aggregationService.aggregateAllDatas();

                CategoryHighestPriceBrand beforeResult = categoryHighestPriceBrandRepository.findAllByCategoryId(category.getId()).get(0);

                item1.delete();
                itemRepository.save(item1);
                aggregationService.aggregateOnItemUpdate(new ItemDto(brand.getId(), category.getId(), item1.getPrice()));

                CategoryHighestPriceBrand afterResult = categoryHighestPriceBrandRepository.findAllByCategoryId(category.getId()).get(0);

                assertAll(() -> {
                    assertThat(beforeResult.getPrice()).isEqualTo(item2.getPrice());
                    assertThat(beforeResult.getPrice()).isEqualTo(afterResult.getPrice());
                });
            }

            @DisplayName("해당 상품이 속한 브랜드의 카테고리 최저가와 동일한 가격일 경우 가격 정보를 재집계한다.")
            @Test
            void delete_item_aggregate_brand_lowest_price() {
                Category category = categoryRepository.save(new Category("상의"));
                Brand brand = brandRepository.save(new Brand("브랜드1"));
                Item item1 = itemRepository.save(new Item("상품1", 1000D, brand.getId(), category.getId()));
                Item item2 = itemRepository.save(new Item("상품2", 2000D, brand.getId(), category.getId()));
                aggregationService.aggregateAllDatas();

                item1.delete();
                itemRepository.save(item1);
                BrandLowestPriceInfo beforeResult = brandLowestPriceInfoRepository.findAllByBrandIdOrderById(brand.getId()).get(0);

                aggregationService.aggregateOnItemUpdate(new ItemDto(brand.getId(), category.getId(), item1.getPrice()));

                BrandLowestPriceInfo afterResult = brandLowestPriceInfoRepository.findAllByBrandIdOrderById(brand.getId()).get(0);

                assertAll(() -> {
                    assertThat(beforeResult.getPrice()).isEqualTo(item1.getPrice());
                    assertThat(afterResult.getPrice()).isEqualTo(item2.getPrice());
                });
            }

            @DisplayName("해당 상품이 속한 브랜드의 카테고리 최저가와 동일한 가격이 아닐 경우 가격 정보를 집계하지 않는다.")
            @Test
            void delete_item_not_aggregate_brand_lowest_price() {
                Category category = categoryRepository.save(new Category("상의"));
                Brand brand = brandRepository.save(new Brand("브랜드1"));
                Item item1 = itemRepository.save(new Item("상품1", 1000D, brand.getId(), category.getId()));
                Item item2 = itemRepository.save(new Item("상품2", 2000D, brand.getId(), category.getId()));
                aggregationService.aggregateAllDatas();

                item2.delete();
                itemRepository.save(item2);
                BrandLowestPriceInfo beforeResult = brandLowestPriceInfoRepository.findAllByBrandIdOrderById(brand.getId()).get(0);

                aggregationService.aggregateOnItemUpdate(new ItemDto(brand.getId(), category.getId(), item2.getPrice()));

                BrandLowestPriceInfo afterResult = brandLowestPriceInfoRepository.findAllByBrandIdOrderById(brand.getId()).get(0);

                assertAll(() -> {
                    assertThat(beforeResult.getPrice()).isEqualTo(item1.getPrice());
                    assertThat(beforeResult.getPrice()).isEqualTo(afterResult.getPrice());
                });
            }
        }
    }

    void setBaseData() {
        Category category1 = categoryRepository.save(new Category("상의"));
        Category category2 = categoryRepository.save(new Category("하의"));

        //최저가 브랜드
        Brand brand1 = brandRepository.save(new Brand("브랜드1"));
        Brand brand2 = brandRepository.save(new Brand("브랜드2"));
        Brand brand3 = brandRepository.save(new Brand("브랜드3"));

        itemRepository.save(new Item("상의1", 1000D, brand1.getId(), category1.getId()));
        itemRepository.save(new Item("상의2", 2000D, brand2.getId(), category1.getId()));
        itemRepository.save(new Item("상의3", 3000D, brand3.getId(), category1.getId()));

        itemRepository.save(new Item("바지1", 1000D, brand1.getId(), category2.getId()));
        itemRepository.save(new Item("바지2", 2000D, brand2.getId(), category2.getId()));
        itemRepository.save(new Item("바지3", 3000D, brand3.getId(), category2.getId()));
    }

    void clearDatabase() {
        categoryRepository.deleteAll();
        brandRepository.deleteAll();
        itemRepository.deleteAll();
        brandTotalPriceRepository.deleteAll();
        brandLowestPriceInfoRepository.deleteAll();
        categoryHighestPriceBrandRepository.deleteAll();
        categoryLowestPriceBrandRepository.deleteAll();
    }


}
