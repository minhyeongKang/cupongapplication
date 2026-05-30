package com.hellomeen.cupongapplication.controller;

import com.hellomeen.cupongapplication.dto.request.MemberJoinRequest;
import com.hellomeen.cupongapplication.dto.request.MemberUpdateRequest;
import com.hellomeen.cupongapplication.entity.Member;
import com.hellomeen.cupongapplication.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/join")
    public String joinForm(Model model) {
        model.addAttribute("memberJoinRequest", new MemberJoinRequest());
        return "members/join";
    }

    @PostMapping("/join")
    public String join(@Valid MemberJoinRequest request, BindingResult bindingResult,
                       RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "members/join";
        }
        try {
            memberService.join(request);
            redirectAttributes.addFlashAttribute("successMessage", "회원가입이 완료되었습니다. 로그인해주세요.");
            return "redirect:/members/login";
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("email", "duplicate", e.getMessage());
            return "members/join";
        }
    }

    @GetMapping("/login")
    public String loginForm(@RequestParam(required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "이메일 또는 비밀번호가 올바르지 않습니다.");
        }
        return "members/login";
    }

    @GetMapping("/mypage")
    public String mypage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Member member = memberService.findByEmail(userDetails.getUsername());
        model.addAttribute("member", member);
        return "members/mypage";
    }

    @GetMapping("/edit")
    public String editForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Member member = memberService.findByEmail(userDetails.getUsername());
        model.addAttribute("member", member);
        model.addAttribute("memberUpdateRequest", new MemberUpdateRequest());
        return "members/edit";
    }

    @PostMapping("/edit")
    public String edit(@AuthenticationPrincipal UserDetails userDetails,
                       @Valid MemberUpdateRequest request, BindingResult bindingResult,
                       RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "members/edit";
        }
        Member member = memberService.findByEmail(userDetails.getUsername());
        memberService.updateProfile(member.getId(), request);
        redirectAttributes.addFlashAttribute("successMessage", "프로필이 수정되었습니다.");
        return "redirect:/members/mypage";
    }

    @GetMapping("/password")
    public String passwordForm() {
        return "members/password";
    }

    @PostMapping("/password")
    public String changePassword(@AuthenticationPrincipal UserDetails userDetails,
                                 @RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 RedirectAttributes redirectAttributes) {
        try {
            Member member = memberService.findByEmail(userDetails.getUsername());
            memberService.changePassword(member.getId(), currentPassword, newPassword);
            redirectAttributes.addFlashAttribute("successMessage", "비밀번호가 변경되었습니다.");
            return "redirect:/members/mypage";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/members/password";
        }
    }
}
