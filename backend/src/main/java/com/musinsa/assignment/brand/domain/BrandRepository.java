package com.musinsa.assignment.brand.domain;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BrandRepository {

    Brand save(Brand brandEntity);

    Optional<Brand> findByName(String name);

    void deleteAll();

}
