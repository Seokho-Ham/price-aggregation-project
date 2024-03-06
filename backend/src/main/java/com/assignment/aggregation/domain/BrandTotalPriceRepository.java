package com.assignment.aggregation.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BrandTotalPriceRepository extends JpaRepository<BrandTotalPrice, Long> {

}
