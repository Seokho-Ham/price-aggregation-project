package com.musinsa.assignment.integration;

import com.musinsa.assignment.brand.domain.Brand;
import com.musinsa.assignment.brand.domain.BrandRepository;
import com.musinsa.assignment.brand.exception.BrandDuplicationException;
import com.musinsa.assignment.brand.exception.BrandNotFoundException;
import com.musinsa.assignment.brand.service.BrandService;
import com.musinsa.assignment.brand.service.dto.BrandCreateDto;
import com.musinsa.assignment.brand.service.dto.BrandUpdateDto;
import com.musinsa.assignment.common.exception.ApplicationErrorCode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("[통합테스트] 브랜드 생성, 수정 삭제 테스트")
public class BrandServiceTest extends IntegrationTest {

    @Autowired
    private BrandService brandService;

    @Autowired
    private BrandRepository brandRepository;

    @AfterEach
    void clearData() {
        brandRepository.deleteAll();
    }

    @DisplayName("[create] 기존에 존재하지 않는 브랜드일 경우 브랜드를 생성한다.")
    @Test
    void create_brand_success() {
        String brandName = "브랜드1";

        brandService.create(new BrandCreateDto(brandName));

        Brand brand = brandRepository.findByName(brandName).get();

        assertThat(brand.getName()).isEqualTo(brandName);
    }

    @DisplayName("[create] 전달된 브랜드명과 동일한 브랜드가 이미 존재할 경우 예외를 반환한다.")
    @Test
    void create_brand_fail_by_duplicate() {
        String brandName = "브랜드1";
        brandRepository.save(new Brand(brandName));

        assertThatThrownBy(() -> brandService.create(new BrandCreateDto(brandName)))
            .isInstanceOf(BrandDuplicationException.class)
            .hasMessage(ApplicationErrorCode.BRAND_DUPLICATION.getMessage());
    }

    @DisplayName("[update] 전달된 브랜드명과 중복되는 브랜드가 존재하지 않을 경우 해당 브랜드명으로 업데이트한다.")
    @Test
    void update_brand_success() {
        String oldBrandName = "브랜드1";
        String newBrandName = "브랜드2";

        Brand existingBrand = brandRepository.save(new Brand(oldBrandName));

        brandService.update(new BrandUpdateDto(existingBrand.getId(), newBrandName));

        Brand findBrand = brandRepository.findByName(newBrandName).get();

        assertAll(() -> {
            assertThat(findBrand).isNotNull();
            assertThat(findBrand.getName()).isEqualTo(newBrandName);
        });
    }

    @DisplayName("[update] 전달된 브랜드명과 중복되는 브랜드가 존재할 경우 예외를 반환한다.")
    @Test
    void update_brand_fail_by_duplicate() {
        String brandName1 = "브랜드1";
        String brandName2 = "브랜드2";
        Brand brand1 = brandRepository.save(new Brand(brandName1));
        brandRepository.save(new Brand(brandName2));

        assertThatThrownBy(() -> brandService.update(new BrandUpdateDto(brand1.getId(), brandName2)))
            .isInstanceOf(BrandDuplicationException.class)
            .hasMessage(ApplicationErrorCode.BRAND_DUPLICATION.getMessage());
    }

    @DisplayName("[delete] 전달된 브랜드 Id에 해당하는 데이터를 논리적으로 삭제처리한다.")
    @Test
    void delete_brand_success() {
        String brandName = "브랜드1";
        Brand existingBrand = brandRepository.save(new Brand(brandName));

        brandService.delete(existingBrand.getId());

        Brand targetBrand = brandRepository.findByName(brandName).get();

        assertAll(() -> {
            assertThat(targetBrand.getDeleted()).isTrue();
            assertThat(targetBrand.getDeletedAt()).isNotNull();
        });
    }

    @DisplayName("[delete] 이미 삭제된 데이터일 경우 예외를 반환한다.")
    @Test
    void delete_brand_fail_by_already_deleted() {
        String brandName = "브랜드1";
        Brand existingBrand = brandRepository.save(new Brand(brandName));
        brandService.delete(existingBrand.getId());

        assertThatThrownBy(() -> brandService.delete(existingBrand.getId()))
            .isInstanceOf(BrandNotFoundException.class);
    }

}
