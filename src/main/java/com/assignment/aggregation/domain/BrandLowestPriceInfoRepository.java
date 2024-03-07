package com.assignment.aggregation.domain;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BrandLowestPriceInfoRepository extends JpaRepository<BrandLowestPriceInfo, Long> {

    Optional<BrandLowestPriceInfo> findByBrandIdAndCategoryId(Long brandId, Long categoryId);

    List<BrandLowestPriceInfo> findAllByBrandIdOrderById(Long brandId);

    void deleteAllByBrandId(@Param("brandId") Long brandId);

}
