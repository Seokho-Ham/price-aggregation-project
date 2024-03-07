package com.assignment.aggregation.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryHighestPriceBrandRepository extends JpaRepository<CategoryHighestPriceBrand, Long> {
    List<CategoryHighestPriceBrand> findAllByCategoryId(Long categoryId);

    void deleteAllByBrandId(Long brandId);

    void deleteAllByCategoryId(Long categoryId);
}
