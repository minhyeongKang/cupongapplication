package com.hellomeen.cupongapplication.service;

import com.hellomeen.cupongapplication.entity.Category;
import com.hellomeen.cupongapplication.exception.EntityNotFoundException;
import com.hellomeen.cupongapplication.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public Long createTopCategory(String name) {
        Category category = Category.builder()
                .name(name)
                .build();
        return categoryRepository.save(category).getId();
    }

    @Transactional
    public Long createSubCategory(String name, Long parentId) {
        Category parent = findById(parentId);
        Category category = Category.builder()
                .name(name)
                .parent(parent)
                .build();
        return categoryRepository.save(category).getId();
    }

    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다. id=" + id));
    }

    public List<Category> findTopCategories() {
        return categoryRepository.findByParentIsNull();
    }

    public List<Category> findSubCategories(Long parentId) {
        return categoryRepository.findByParentId(parentId);
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }
}
