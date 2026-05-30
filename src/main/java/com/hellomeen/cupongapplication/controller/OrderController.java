package com.hellomeen.cupongapplication.controller;

import com.hellomeen.cupongapplication.dto.request.OrderCreateRequest;
import com.hellomeen.cupongapplication.entity.Member;
import com.hellomeen.cupongapplication.service.MemberService;
import com.hellomeen.cupongapplication.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final MemberService memberService;

    @PostMapping
    public String createOrder(@AuthenticationPrincipal UserDetails userDetails,
                              @Valid OrderCreateRequest request,
                              RedirectAttributes redirectAttributes) {
        try {
            Member member = memberService.findByEmail(userDetails.getUsername());
            Long orderId = orderService.createFromCart(member.getId(), request);
            redirectAttributes.addFlashAttribute("successMessage", "주문이 완료되었습니다.");
            return "redirect:/orders/" + orderId;
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/cart";
        }
    }

    @GetMapping
    public String myOrders(@AuthenticationPrincipal UserDetails userDetails,
                           @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                           Model model) {
        Member member = memberService.findByEmail(userDetails.getUsername());
        model.addAttribute("orders", orderService.findMyOrders(member.getId(), pageable));
        return "orders/list";
    }

    @GetMapping("/{id}")
    public String orderDetail(@AuthenticationPrincipal UserDetails userDetails,
                              @PathVariable Long id, Model model) {
        Member member = memberService.findByEmail(userDetails.getUsername());
        model.addAttribute("order", orderService.findById(id));
        return "orders/detail";
    }

    @PostMapping("/{id}/cancel")
    public String cancelOrder(@AuthenticationPrincipal UserDetails userDetails,
                              @PathVariable Long id,
                              RedirectAttributes redirectAttributes) {
        try {
            Member member = memberService.findByEmail(userDetails.getUsername());
            orderService.cancel(member.getId(), id);
            redirectAttributes.addFlashAttribute("successMessage", "주문이 취소되었습니다.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/orders/" + id;
    }
}
