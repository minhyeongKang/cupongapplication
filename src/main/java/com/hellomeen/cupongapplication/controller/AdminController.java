package com.hellomeen.cupongapplication.controller;

import com.hellomeen.cupongapplication.dto.request.ProductSaveRequest;
import com.hellomeen.cupongapplication.entity.enums.OrderStatus;
import com.hellomeen.cupongapplication.service.CategoryService;
import com.hellomeen.cupongapplication.service.OrderService;
import com.hellomeen.cupongapplication.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final OrderService orderService;

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("recentOrders", orderService.findAll(
                org.springframework.data.domain.PageRequest.of(0, 5)));
        return "admin/dashboard";
    }

    // 상품 관리

    @GetMapping("/products")
    public String productList(@PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                              Model model) {
        model.addAttribute("products", productService.findAll(pageable));
        return "admin/products/list";
    }

    @GetMapping("/products/new")
    public String productCreateForm(Model model) {
        model.addAttribute("productSaveRequest", new ProductSaveRequest());
        model.addAttribute("categories", categoryService.findAll());
        return "admin/products/form";
    }

    @PostMapping("/products/new")
    public String productCreate(@Valid ProductSaveRequest request, BindingResult bindingResult,
                                Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.findAll());
            return "admin/products/form";
        }
        productService.create(request);
        redirectAttributes.addFlashAttribute("successMessage", "상품이 등록되었습니다.");
        return "redirect:/admin/products";
    }

    @GetMapping("/products/{id}/edit")
    public String productEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.findById(id));
        model.addAttribute("productSaveRequest", new ProductSaveRequest());
        model.addAttribute("categories", categoryService.findAll());
        return "admin/products/form";
    }

    @PostMapping("/products/{id}/edit")
    public String productEdit(@PathVariable Long id,
                              @Valid ProductSaveRequest request, BindingResult bindingResult,
                              Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.findAll());
            return "admin/products/form";
        }
        productService.update(id, request);
        redirectAttributes.addFlashAttribute("successMessage", "상품이 수정되었습니다.");
        return "redirect:/admin/products";
    }

    @PostMapping("/products/{id}/delete")
    public String productDelete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        productService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "상품이 삭제되었습니다.");
        return "redirect:/admin/products";
    }

    // 카테고리 관리

    @GetMapping("/categories")
    public String categoryList(Model model) {
        model.addAttribute("topCategories", categoryService.findTopCategories());
        return "admin/categories/list";
    }

    @PostMapping("/categories")
    public String createTopCategory(@RequestParam String name, RedirectAttributes redirectAttributes) {
        categoryService.createTopCategory(name);
        redirectAttributes.addFlashAttribute("successMessage", "카테고리가 추가되었습니다.");
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/{id}/sub")
    public String createSubCategory(@PathVariable Long id, @RequestParam String name,
                                    RedirectAttributes redirectAttributes) {
        categoryService.createSubCategory(name, id);
        redirectAttributes.addFlashAttribute("successMessage", "하위 카테고리가 추가되었습니다.");
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/{id}/rename")
    public String renameCategory(@PathVariable Long id, @RequestParam String name,
                                 RedirectAttributes redirectAttributes) {
        categoryService.rename(id, name);
        redirectAttributes.addFlashAttribute("successMessage", "카테고리 이름을 수정했습니다.");
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/{id}/delete")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "카테고리를 삭제했습니다.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    // 주문 관리

    @GetMapping("/orders")
    public String orderList(@PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                            Model model) {
        model.addAttribute("orders", orderService.findAll(pageable));
        return "admin/orders/list";
    }

    @GetMapping("/orders/{id}")
    public String orderDetail(@PathVariable Long id, Model model) {
        model.addAttribute("order", orderService.findById(id));
        return "admin/orders/detail";
    }

    @PostMapping("/orders/{id}/approve")
    public String approveOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            orderService.approve(id);
            redirectAttributes.addFlashAttribute("successMessage", "주문을 승인했습니다. 배송을 준비해주세요.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/orders/" + id;
    }

    @PostMapping("/orders/{id}/ship")
    public String shipOrder(@PathVariable Long id,
                            @RequestParam String trackingNumber,
                            RedirectAttributes redirectAttributes) {
        try {
            orderService.startShipping(id, trackingNumber);
            redirectAttributes.addFlashAttribute("successMessage", "배송을 시작했습니다.");
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/orders/" + id;
    }

    @PostMapping("/orders/{id}/complete")
    public String completeOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            orderService.completeDelivery(id);
            redirectAttributes.addFlashAttribute("successMessage", "배송 완료 처리했습니다.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/orders/" + id;
    }

    @PostMapping("/orders/{id}/cancel")
    public String cancelOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            orderService.updateStatus(id, OrderStatus.CANCELLED);
            redirectAttributes.addFlashAttribute("successMessage", "주문을 취소했습니다.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/orders/" + id;
    }
}
