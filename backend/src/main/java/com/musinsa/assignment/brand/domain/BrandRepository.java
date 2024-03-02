package com.musinsa.assignment.brand.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Long> {

    Optional<Brand> findByName(String name);

    Optional<Brand> findByIdAndDeletedIsFalse(Long brandId);
}
