package com.kschoi.game.baseball_game.controller;



import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kschoi.game.baseball_game.dto.MemberDto;
import com.kschoi.game.baseball_game.service.MemberService;

@Controller
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute MemberDto memberDto,
            BindingResult bindingResult,
            Model model,
            HttpSession session) {

        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMessage.append(error.getDefaultMessage()).append("<br>");
            }
            model.addAttribute("errorMessage", errorMessage.toString());
            model.addAttribute("memberDto", memberDto);
            return "login";
        }
    	
        Long loggedInMemberId = memberService.loginMember(memberDto.getMemberId(), memberDto.getMemberPassword());

        if (loggedInMemberId != null) {
            session.setAttribute("loggedInMemberId", loggedInMemberId);
            return "redirect:/game-main";
        } else {
            model.addAttribute("errorMessage", "IDまたはパスワードが正しくありません。");
            return "login";
        }
    }
}