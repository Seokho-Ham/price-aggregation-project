package com.musinsa.assignment;

import com.musinsa.assignment.brand.domain.Brand;
import com.musinsa.assignment.brand.domain.BrandRepository;
import com.musinsa.assignment.category.domain.Category;
import com.musinsa.assignment.category.domain.CategoryRepository;
import com.musinsa.assignment.item.domain.Item;
import com.musinsa.assignment.item.domain.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class DataInitCommandLineRunner implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ItemRepository itemRepository;

    @Override
    public void run(String... args) throws Exception {
        initCategoryData();
        initBrandData();
        initItemData();
    }

    private void initCategoryData() {
        List<Category> categories = new ArrayList<>();

        categories.add(new Category("상의"));
        categories.add(new Category("아우터"));
        categories.add(new Category("바지"));
        categories.add(new Category("스니커즈"));
        categories.add(new Category("가방"));
        categories.add(new Category("모자"));
        categories.add(new Category("양말"));
        categories.add(new Category("액세서리"));

        categoryRepository.saveAll(categories);
    }

    private void initBrandData() {
        List<Brand> brands = new ArrayList<>();

        brands.add(new Brand("A"));
        brands.add(new Brand("B"));
        brands.add(new Brand("C"));
        brands.add(new Brand("D"));
        brands.add(new Brand("E"));
        brands.add(new Brand("F"));
        brands.add(new Brand("G"));
        brands.add(new Brand("H"));
        brands.add(new Brand("I"));

        brandRepository.saveAll(brands);
    }

    private void initItemData() {
        List<Item> items = new ArrayList<>();

        items.add(new Item("상의1", 11200D, 1L, 1L));
        items.add(new Item("상의2", 10500D, 2L, 1L));
        items.add(new Item("상의3", 10000D, 3L, 1L));
        items.add(new Item("상의4", 10100D, 4L, 1L));
        items.add(new Item("상의5", 10700D, 5L, 1L));
        items.add(new Item("상의6", 11200D, 6L, 1L));
        items.add(new Item("상의7", 10500D, 7L, 1L));
        items.add(new Item("상의8", 10800D, 8L, 1L));
        items.add(new Item("상의9", 11400D, 9L, 1L));

        items.add(new Item("아우터1", 5500D, 1L, 2L));
        items.add(new Item("아우터2", 5900D, 2L, 2L));
        items.add(new Item("아우터3", 6200D, 3L, 2L));
        items.add(new Item("아우터4", 5100D, 4L, 2L));
        items.add(new Item("아우터5", 5000D, 5L, 2L));
        items.add(new Item("아우터6", 7200D, 6L, 2L));
        items.add(new Item("아우터7", 5800D, 7L, 2L));
        items.add(new Item("아우터8", 6300D, 8L, 2L));
        items.add(new Item("아우터9", 6700D, 9L, 2L));

        items.add(new Item("바지1", 4200D, 1L, 3L));
        items.add(new Item("바지2", 3800D, 2L, 3L));
        items.add(new Item("바지3", 3300D, 3L, 3L));
        items.add(new Item("바지4", 3000D, 4L, 3L));
        items.add(new Item("바지5", 3800D, 5L, 3L));
        items.add(new Item("바지6", 4000D, 6L, 3L));
        items.add(new Item("바지7", 3900D, 7L, 3L));
        items.add(new Item("바지8", 3100D, 8L, 3L));
        items.add(new Item("바지9", 3200D, 9L, 3L));

        items.add(new Item("스니커즈1", 9000D, 1L, 4L));
        items.add(new Item("스니커즈2", 9100D, 2L, 4L));
        items.add(new Item("스니커즈3", 9200D, 3L, 4L));
        items.add(new Item("스니커즈4", 9500D, 4L, 4L));
        items.add(new Item("스니커즈5", 9900D, 5L, 4L));
        items.add(new Item("스니커즈6", 9300D, 6L, 4L));
        items.add(new Item("스니커즈7", 9000D, 7L, 4L));
        items.add(new Item("스니커즈8", 9700D, 8L, 4L));
        items.add(new Item("스니커즈9", 9500D, 9L, 4L));

        items.add(new Item("가방1", 2000D, 1L, 5L));
        items.add(new Item("가방2", 2100D, 2L, 5L));
        items.add(new Item("가방3", 2200D, 3L, 5L));
        items.add(new Item("가방4", 2500D, 4L, 5L));
        items.add(new Item("가방5", 2300D, 5L, 5L));
        items.add(new Item("가방6", 2100D, 6L, 5L));
        items.add(new Item("가방7", 2200D, 7L, 5L));
        items.add(new Item("가방8", 2100D, 8L, 5L));
        items.add(new Item("가방9", 2400D, 9L, 5L));

        items.add(new Item("모자1", 1700D, 1L, 6L));
        items.add(new Item("모자2", 2000D, 2L, 6L));
        items.add(new Item("모자3", 1900D, 3L, 6L));
        items.add(new Item("모자4", 1500D, 4L, 6L));
        items.add(new Item("모자5", 1800D, 5L, 6L));
        items.add(new Item("모자6", 1600D, 6L, 6L));
        items.add(new Item("모자7", 1700D, 7L, 6L));
        items.add(new Item("모자8", 1600D, 8L, 6L));
        items.add(new Item("모자9", 1700D, 9L, 6L));

        items.add(new Item("양말1", 1800D, 1L, 7L));
        items.add(new Item("양말2", 2000D, 2L, 7L));
        items.add(new Item("양말3", 2200D, 3L, 7L));
        items.add(new Item("양말4", 2400D, 4L, 7L));
        items.add(new Item("양말5", 2100D, 5L, 7L));
        items.add(new Item("양말6", 2300D, 6L, 7L));
        items.add(new Item("양말7", 2100D, 7L, 7L));
        items.add(new Item("양말8", 2000D, 8L, 7L));
        items.add(new Item("양말9", 1700D, 9L, 7L));

        items.add(new Item("액세서리1", 2300D, 1L, 8L));
        items.add(new Item("액세서리2", 2200D, 2L, 8L));
        items.add(new Item("액세서리3", 2100D, 3L, 8L));
        items.add(new Item("액세서리4", 2000D, 4L, 8L));
        items.add(new Item("액세서리5", 2100D, 5L, 8L));
        items.add(new Item("액세서리6", 1900D, 6L, 8L));
        items.add(new Item("액세서리7", 2000D, 7L, 8L));
        items.add(new Item("액세서리8", 2000D, 8L, 8L));
        items.add(new Item("액세서리9", 2400D, 9L, 8L));

        itemRepository.saveAll(items);
    }

}
