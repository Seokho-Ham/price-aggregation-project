package com.assignment.brand.domain;

import com.assignment.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// review: id 만 거는것도 아니고, 전체 체크하는것도 아니고 왜 id, name 으로 체크하는지 모르겠음
@EqualsAndHashCode(of = {"id", "name"}, callSuper = false)
@Entity
public class Brand extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // review: 보통 column 명 앞에 prefix 안붙임
    @Column(name = "brand_id")
    private Long id;

    @Column(name = "brand_name")
    private String name;

    @ColumnDefault("false")
    private Boolean deleted;

    private LocalDateTime deletedAt;

    public Brand(String name) {
        this.name = name;
        this.deleted = false;
    }

    public void updateName(String brandName) {
        this.name = brandName;
    }

    public void delete() {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
    }
}
