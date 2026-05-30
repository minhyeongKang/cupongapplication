package com.hellomeen.cupongapplication.controller;

import com.hellomeen.cupongapplication.dto.request.ReviewRequest;
import com.hellomeen.cupongapplication.entity.Member;
import com.hellomeen.cupongapplication.service.MemberService;
import com.hellomeen.cupongapplication.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/products/{productId}/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final MemberService memberService;

    @PostMapping
    public String create(@AuthenticationPrincipal UserDetails userDetails,
                         @PathVariable Long productId,
                         @Valid ReviewRequest request,
                         RedirectAttributes redirectAttributes) {
        try {
            Member member = memberService.findByEmail(userDetails.getUsername());
            reviewService.create(member.getId(), productId, request);
            redirectAttributes.addFlashAttribute("successMessage", "리뷰가 등록되었습니다.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/products/" + productId;
    }

    @PostMapping("/{reviewId}/update")
    public String update(@AuthenticationPrincipal UserDetails userDetails,
                         @PathVariable Long productId,
                         @PathVariable Long reviewId,
                         @Valid ReviewRequest request,
                         RedirectAttributes redirectAttributes) {
        Member member = memberService.findByEmail(userDetails.getUsername());
        reviewService.update(member.getId(), reviewId, request);
        redirectAttributes.addFlashAttribute("successMessage", "리뷰가 수정되었습니다.");
        return "redirect:/products/" + productId;
    }

    @PostMapping("/{reviewId}/delete")
    public String delete(@AuthenticationPrincipal UserDetails userDetails,
                         @PathVariable Long productId,
                         @PathVariable Long reviewId) {
        Member member = memberService.findByEmail(userDetails.getUsername());
        reviewService.delete(member.getId(), reviewId);
        return "redirect:/products/" + productId;
    }
}
