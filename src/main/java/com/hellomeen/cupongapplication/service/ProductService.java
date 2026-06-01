package com.hellomeen.cupongapplication.service;

import com.hellomeen.cupongapplication.dto.request.ProductSaveRequest;
import com.hellomeen.cupongapplication.entity.Category;
import com.hellomeen.cupongapplication.entity.Product;
import com.hellomeen.cupongapplication.exception.EntityNotFoundException;
import com.hellomeen.cupongapplication.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    @Transactional
    public Long create(ProductSaveRequest request) {
        Category category = categoryService.findById(request.getCategoryId());

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .imageUrl(request.getImageUrl())
                .category(category)
                .build();

        return productRepository.save(product).getId();
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다. id=" + id));
    }

    public Page<Product> findByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryId(categoryId, pageable);
    }

    public Page<Product> search(String keyword, Pageable pageable) {
        return productRepository.findByNameContainingOrDescriptionContaining(
                keyword, keyword, pageable);
    }

    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Transactional
    public void update(Long productId, ProductSaveRequest request) {
        Product product = findById(productId);
        Category category = categoryService.findById(request.getCategoryId());

        product.updateInfo(
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getStock(),
                request.getImageUrl()
        );
    }

    @Transactional
    public void delete(Long productId) {
        Product product = findById(productId);
        productRepository.delete(product);
    }
}
