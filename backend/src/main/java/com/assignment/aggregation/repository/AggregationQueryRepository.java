package com.assignment.aggregation.repository;

import com.assignment.aggregation.repository.dto.BrandCategoryDto;
import com.assignment.aggregation.repository.dto.CategoryPriceBrandDto;
import com.assignment.aggregation.repository.dto.QBrandCategoryDto;
import com.assignment.aggregation.repository.dto.QCategoryPriceBrandDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.assignment.brand.domain.QBrand.brand;
import static com.assignment.category.domain.QCategory.category;
import static com.assignment.item.domain.QItem.item;

@RequiredArgsConstructor
@Repository
public class AggregationQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<BrandCategoryDto> getAllBrandData() {
        return jpaQueryFactory.select(new QBrandCategoryDto(brand.id, brand.name, category.id, category.name, item.price.min()))
            .from(item)
            .join(brand).on(brand.id.eq(item.brandId))
            .join(category).on(category.id.eq(item.categoryId))
            .where(
                brandIsNotDeleted(),
                itemIsNotDeleted()
            )
            .groupBy(category.id, brand.id)
            .fetch();
    }

    public List<BrandCategoryDto> findByBrandLowestPriceByCategoryId(Long brandId) {
        return jpaQueryFactory.select(new QBrandCategoryDto(brand.id, brand.name, category.id, category.name, item.price.min()))
            .from(item)
            .join(brand).on(brand.id.eq(item.brandId))
            .join(category).on(category.id.eq(item.categoryId))
            .where(
                eqBrandId(brandId),
                brandIsNotDeleted(),
                itemIsNotDeleted()
            )
            .groupBy(category.id, brand.id)
            .fetch();
    }

    public List<CategoryPriceBrandDto> getCategoryLowestPriceBrandDtos() {
        return jpaQueryFactory
            .select(new QCategoryPriceBrandDto(category.id, category.name, brand.id, brand.name, item.price.min()))
            .from(item)
            .join(brand).on(item.brandId.eq(brand.id))
            .join(category).on(category.id.eq(item.categoryId))
            .where(
                brandIsNotDeleted(),
                itemIsNotDeleted()
            )
            .groupBy(item.categoryId, brand.id)
            .fetch();
    }

    public List<CategoryPriceBrandDto> getCategoryHighestPriceBrandDtos() {
        return jpaQueryFactory
            .select(new QCategoryPriceBrandDto(category.id, category.name, brand.id, brand.name, item.price.max()))
            .from(item)
            .join(brand).on(item.brandId.eq(brand.id))
            .join(category).on(category.id.eq(item.categoryId))
            .where(
                brandIsNotDeleted(),
                itemIsNotDeleted()
            )
            .groupBy(item.categoryId, brand.id)
            .fetch();
    }

    private BooleanExpression eqBrandId(Long brandId) {
        return brand.id.eq(brandId);
    }

    private BooleanExpression itemIsNotDeleted() {
        return item.deleted.isFalse();
    }

    private BooleanExpression brandIsNotDeleted() {
        return brand.deleted.isFalse();
    }
}
