package com.assignment.aggregation.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryLowestPriceBrandRepository extends JpaRepository<CategoryLowestPriceBrand, Long> {

    List<CategoryLowestPriceBrand> findAllByCategoryId(Long categoryId);

    void deleteAllByBrandId(Long brandId);

    void deleteAllByCategoryId(Long categoryId);

}
