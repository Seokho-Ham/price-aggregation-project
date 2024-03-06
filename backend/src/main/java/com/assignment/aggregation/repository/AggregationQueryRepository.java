package com.assignment.aggregation.repository;

import com.assignment.aggregation.repository.dto.BrandCategoryDto;
import com.assignment.aggregation.repository.dto.QBrandCategoryDto;
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
            .where(brandIsNotDeleted().and(itemIsNotDeleted()))
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
                brandIsNotDeleted().and(itemIsNotDeleted())
            )
            .groupBy(category.id, brand.id)
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
