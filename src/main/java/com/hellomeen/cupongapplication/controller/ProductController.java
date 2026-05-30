package com.hellomeen.cupongapplication.controller;

import com.hellomeen.cupongapplication.service.CategoryService;
import com.hellomeen.cupongapplication.service.ProductService;
import com.hellomeen.cupongapplication.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final ReviewService reviewService;

    @GetMapping
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) Long categoryId,
                       @PageableDefault(size = 12, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                       Model model) {
        if (keyword != null && !keyword.isBlank()) {
            model.addAttribute("products", productService.search(keyword, pageable));
            model.addAttribute("keyword", keyword);
        } else if (categoryId != null) {
            model.addAttribute("products", productService.findByCategory(categoryId, pageable));
            model.addAttribute("selectedCategoryId", categoryId);
        } else {
            model.addAttribute("products", productService.findAll(pageable));
        }
        model.addAttribute("categories", categoryService.findTopCategories());
        return "products/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model,
                         @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        model.addAttribute("product", productService.findById(id));
        model.addAttribute("reviews", reviewService.findByProduct(id, pageable));
        model.addAttribute("averageRating", reviewService.getAverageRating(id));
        return "products/detail";
    }
}
