package com.assignment.unit;

import com.assignment.brand.domain.Brand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("[단위테스트] Brand Entity 테스트")
class BrandTest {

    @DisplayName("updated 메서드 호출 시 엔티티의 name 필드가 전달된 값으로 변경된다.")
    @Test
    void update() {
        String originalName = "브랜드1";
        Brand brand = new Brand(originalName);

        String newName = "브랜드2";
        brand.updateName(newName);

        assertThat(brand.getName()).isEqualTo(newName);
    }

    @DisplayName("delete 메서드 호출 시 엔티티의 deleted 필드가 true로, deletedAt 필드가 현재시간으로 변경된다.")
    @Test
    void delete() {
        String brandName = "브랜드1";
        Brand brand = new Brand(brandName);

        brand.delete();

        assertAll(() -> {
            assertThat(brand.getDeleted()).isTrue();
            assertThat(brand.getDeletedAt()).isNotNull();
        });
    }

}
