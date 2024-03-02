package com.musinsa.assignment.integration;

import com.musinsa.assignment.brand.controller.dto.BrandCreateRequest;
import com.musinsa.assignment.brand.domain.Brand;
import com.musinsa.assignment.brand.domain.BrandRepository;
import com.musinsa.assignment.brand.exception.BrandDuplicationException;
import com.musinsa.assignment.brand.service.BrandService;
import com.musinsa.assignment.common.exception.ApplicationErrorCode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("[통합테스트] 브랜드 생성, 수정 삭제 테스트")
public class BrandServiceTest extends IntegrationTest{

    @Autowired
    private BrandService brandService;

    @Autowired
    private BrandRepository brandRepository;

    @AfterEach
    void clearData() {
        brandRepository.deleteAll();
    }

    @DisplayName("기존에 존재하지 않는 브랜드일 경우 브랜드를 생성한다.")
    @Test
    void create_brand_success() {
        BrandCreateRequest request = new BrandCreateRequest("브랜드1");

        brandService.create(request);

        Brand brand = brandRepository.findByName(request.getBrandName()).get();

        assertThat(brand.getName()).isEqualTo(request.getBrandName());
    }

    @DisplayName("전달된 브랜드명과 동일한 브랜드가 이미 존재할 경우 예외를 반환한다.")
    @Test
    void create_brand_fail_by_duplicate() {
        BrandCreateRequest request = new BrandCreateRequest("브랜드1");
        brandRepository.save(new Brand(request.getBrandName()));

        assertThatThrownBy(() -> brandService.create(request))
            .isInstanceOf(BrandDuplicationException.class)
            .hasMessage(ApplicationErrorCode.BRAND_DUPLICATION.getMessage());
    }

}
