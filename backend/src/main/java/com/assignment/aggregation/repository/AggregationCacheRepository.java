package com.assignment.aggregation.repository;

import com.assignment.aggregation.domain.BrandLowestPriceInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Repository
public class AggregationCacheRepository {

    private static final String CATEGORY_LOWEST_PRICE_KEY = "category:lowest:price:brand";
    private static final String CATEGORY_HIGHEST_PRICE_KEY = "category:highest:price:brand";
    private static final String BRAND_TOTAL_PRICE_INFO_KEY = "brand:total:price";
    private static final String BRAND_PRICE_INFO_KEY = "brand:price:info";

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Redis 저장소에 카테고리별 최저가 브랜드 데이터를 적재합니다.
     */
    public void saveCategoryLowestPriceBrandCacheData(Map<String, String> data) {
        redisTemplate.opsForHash()
            .putAll(CATEGORY_LOWEST_PRICE_KEY, data);
    }

    /**
     * Redis 저장소에 카테고리별 최고가 브랜드 데이터를 적재합니다.
     */
    public void saveCategoryHighestPriceBrandCacheData(Map<String, String> data) {
        redisTemplate.opsForHash()
            .putAll(CATEGORY_HIGHEST_PRICE_KEY, data);
    }

    /**
     * Redis 저장소에 브랜드의 카테고리별 최저가 데이터를 적재합니다.
     */
    public void saveAllBrandCategoryPriceCacheData(Map<String, String> data) {
        redisTemplate.opsForHash()
            .putAll(BRAND_PRICE_INFO_KEY, data);
    }

    /**
     * Redis Sorted Set에 value:브랜드Id score: 총액 형태로 데이터를 적재합니다.
     */
    public void saveAllBrandTotalPrice(Set<ZSetOperations.TypedTuple<Object>> data) {
        redisTemplate.opsForZSet()
            .add(BRAND_TOTAL_PRICE_INFO_KEY, data);
    }

    public void deleteBrandTotalPriceCacheByBrandId(String brandId) {
        redisTemplate.opsForZSet()
            .remove(BRAND_TOTAL_PRICE_INFO_KEY, brandId);
    }

    public void deleteAllBrandCategoryPriceCacheByBrandId(String brandId) {
        redisTemplate.opsForHash()
            .delete(BRAND_PRICE_INFO_KEY, brandId);
    }

    public void deleteAllCategoryLowestPriceBrandCacheByBrandId(String brandId) {
        redisTemplate.opsForHash()
            .delete(CATEGORY_LOWEST_PRICE_KEY, brandId);
    }

    public void deleteAllCategoryHighestPriceBrandCacheByBrandId(String brandId) {
        redisTemplate.opsForHash()
            .delete(CATEGORY_HIGHEST_PRICE_KEY, brandId);
    }

    /**
     * Redis 저장소로부터 총액 최저가에 해당하는 브랜드 데이터를 조회합니다.
     */
    public Optional<List<BrandLowestPriceInfo>> getLowestTotalPriceBrand() {
        Set<Object> result = redisTemplate.opsForZSet().range(BRAND_TOTAL_PRICE_INFO_KEY, 0, 0);
        if (result != null && !result.isEmpty()) {
            String brandId = (String) result.iterator().next();
            return getTotalPriceBrandData(brandId);
        }
        log.info("[cache] 캐시 저장소에 브랜드 별 총액 데이터가 존재하지 않습니다.");
        return Optional.empty();
    }

    private Optional<List<BrandLowestPriceInfo>> getTotalPriceBrandData(String brandId) {
        try {
            String data = (String) redisTemplate.opsForHash().get(BRAND_PRICE_INFO_KEY, brandId);
            return Optional.of((objectMapper.readValue(data, new TypeReference<>() {
            })));
        } catch (Exception e) {
            log.error("{}", e.getMessage());
            log.info("[cache] 캐시 저장소에 해당 브랜드의 최저가 데이터가 존재하지 않습니다. - brandId:{}", brandId);
            return Optional.empty();
        }
    }
}
