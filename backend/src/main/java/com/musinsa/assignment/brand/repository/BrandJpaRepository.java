package com.musinsa.assignment.brand.repository;

import com.musinsa.assignment.brand.domain.Brand;
import com.musinsa.assignment.brand.domain.BrandRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BrandJpaRepository extends BrandRepository, JpaRepository<Brand, Long> {

    Optional<Brand> findByName(String name);

}
