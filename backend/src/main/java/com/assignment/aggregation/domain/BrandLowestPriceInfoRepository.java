package com.assignment.aggregation.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BrandLowestPriceInfoRepository extends JpaRepository<BrandLowestPriceInfo, BrandLowestPriceInfoPk> {

    List<BrandLowestPriceInfo> findAllById_BrandId(Long brandId);

    void deleteAllById_BrandId(Long brandId);

}
