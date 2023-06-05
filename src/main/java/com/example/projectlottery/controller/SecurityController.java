package com.example.projectlottery.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping("/security")
@Controller
public class SecurityController {

    /**
     *  CustomAuthenticationEntryPoint 예외 발생 시 이 곳으로 전달된다.
     *  따라서, 인증 여부에 따른 예외 처리를 실질적으로 이 곳에서 담당한다.
     */
    @GetMapping
    public String secure() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof AnonymousAuthenticationToken) {
            //인증 정보가 없는 경우
            //사용자 인증을 위해 로그인 페이지로 redirect
            return "redirect:/oauth2/authorization/kakao";
        } else {
            //인증 정보가 있는 경우,
            //잘못된 요청(404)이므로 index view
            return "redirect:/";
        }
    }
}
