package com.hellomeen.cupongapplication.controller;

import com.hellomeen.cupongapplication.dto.request.CartItemRequest;
import com.hellomeen.cupongapplication.entity.Member;
import com.hellomeen.cupongapplication.service.CartService;
import com.hellomeen.cupongapplication.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final MemberService memberService;

    @GetMapping
    public String cart(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Member member = memberService.findByEmail(userDetails.getUsername());
        var cart = cartService.getCart(member.getId());
        int totalPrice = cart.getCartItems().stream()
                .mapToInt(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
        model.addAttribute("cart", cart);
        model.addAttribute("totalPrice", totalPrice);
        return "cart/cart";
    }

    @PostMapping("/items")
    public String addItem(@AuthenticationPrincipal UserDetails userDetails,
                          @Valid CartItemRequest request,
                          RedirectAttributes redirectAttributes) {
        Member member = memberService.findByEmail(userDetails.getUsername());
        cartService.addItem(member.getId(), request);
        redirectAttributes.addFlashAttribute("successMessage", "장바구니에 담겼습니다.");
        return "redirect:/cart";
    }

    @PostMapping("/items/{id}/update")
    public String updateQuantity(@AuthenticationPrincipal UserDetails userDetails,
                                 @PathVariable Long id,
                                 @RequestParam int quantity) {
        Member member = memberService.findByEmail(userDetails.getUsername());
        cartService.updateItemQuantity(member.getId(), id, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/items/{id}/delete")
    public String removeItem(@AuthenticationPrincipal UserDetails userDetails,
                             @PathVariable Long id) {
        Member member = memberService.findByEmail(userDetails.getUsername());
        cartService.removeItem(member.getId(), id);
        return "redirect:/cart";
    }
}
