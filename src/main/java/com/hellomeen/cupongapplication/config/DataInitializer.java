package com.hellomeen.cupongapplication.config;

import com.hellomeen.cupongapplication.dto.naver.NaverShoppingResponse.NaverShoppingItem;
import com.hellomeen.cupongapplication.entity.Category;
import com.hellomeen.cupongapplication.entity.Member;
import com.hellomeen.cupongapplication.entity.Product;
import com.hellomeen.cupongapplication.entity.enums.Role;
import com.hellomeen.cupongapplication.repository.CategoryRepository;
import com.hellomeen.cupongapplication.repository.MemberRepository;
import com.hellomeen.cupongapplication.repository.ProductRepository;
import com.hellomeen.cupongapplication.service.NaverShoppingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final NaverShoppingService naverShoppingService;

    private static final Map<String, List<String>> CATEGORY_KEYWORDS = Map.of(
            "전자제품", List.of("노트북", "무선이어폰", "스마트워치"),
            "패션의류", List.of("티셔츠", "청바지", "후드집업"),
            "뷰티", List.of("선크림", "립글로스", "에센스"),
            "식품", List.of("프로틴바", "그래놀라", "견과류"),
            "스포츠", List.of("요가매트", "덤벨", "운동화")
    );

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        createAdminIfNotExists();
        initProducts();
    }

    private void createAdminIfNotExists() {
        if (!memberRepository.existsByEmail("admin@cupong.com")) {
            memberRepository.save(Member.builder()
                    .email("admin@cupong.com")
                    .password(passwordEncoder.encode("admin1234"))
                    .name("관리자")
                    .role(Role.ADMIN)
                    .build());
            log.info("관리자 계정 생성 완료 → email: admin@cupong.com / password: admin1234");
        }

        if (!memberRepository.existsByEmail("driver@cupong.com")) {
            memberRepository.save(Member.builder()
                    .email("driver@cupong.com")
                    .password(passwordEncoder.encode("driver1234"))
                    .name("배송기사")
                    .role(Role.DELIVERY)
                    .build());
            log.info("배송기사 계정 생성 완료 → email: driver@cupong.com / password: driver1234");
        }
    }

    private void initProducts() {
        if (productRepository.count() > 0) {
            log.info("상품 데이터가 이미 존재합니다. 초기화를 건너뜁니다.");
            return;
        }

        log.info("네이버 쇼핑 API로 상품 데이터를 초기화합니다...");

        CATEGORY_KEYWORDS.forEach((categoryName, keywords) -> {
            Category category = categoryRepository.findByParentIsNull().stream()
                    .filter(c -> c.getName().equals(categoryName))
                    .findFirst()
                    .orElseGet(() -> categoryRepository.save(
                            Category.builder().name(categoryName).build()));

            keywords.forEach(keyword -> {
                List<NaverShoppingItem> items = naverShoppingService.search(keyword, 5);
                items.forEach(item -> saveProduct(item, category));
            });
        });

        log.info("상품 데이터 초기화 완료. 총 {}개 상품 저장.", productRepository.count());
    }

    private void saveProduct(NaverShoppingItem item, Category category) {
        try {
            String title = item.cleanTitle();
            if (title.isBlank() || item.parsedPrice() <= 0) return;

            productRepository.save(Product.builder()
                    .name(title.length() > 100 ? title.substring(0, 100) : title)
                    .description(item.getMallName() != null
                            ? item.getMallName() + "에서 판매하는 상품입니다."
                            : "네이버 쇼핑 연동 상품입니다.")
                    .price(item.parsedPrice())
                    .stock(100)
                    .imageUrl(item.getImage())
                    .category(category)
                    .build());
        } catch (Exception e) {
            log.warn("상품 저장 실패: {}", e.getMessage());
        }
    }
}
