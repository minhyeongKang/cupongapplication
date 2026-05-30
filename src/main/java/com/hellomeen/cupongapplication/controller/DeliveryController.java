package com.hellomeen.cupongapplication.controller;

import com.hellomeen.cupongapplication.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/delivery")
@PreAuthorize("hasRole('DELIVERY')")
@RequiredArgsConstructor
public class DeliveryController {

    private final OrderService orderService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "shipping") String tab,
                       @PageableDefault(size = 12, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                       Model model) {
        if ("delivered".equals(tab)) {
            model.addAttribute("orders", orderService.findDeliveredOrders(pageable));
        } else {
            model.addAttribute("orders", orderService.findShippingOrders(pageable));
        }
        model.addAttribute("tab", tab);
        return "delivery/list";
    }

    @PostMapping("/{id}/complete")
    public String complete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            orderService.completeDelivery(id);
            redirectAttributes.addFlashAttribute("successMessage", "배송 완료 처리되었습니다.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/delivery";
    }
}
