package com.example.projectlottery.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping("/user")
@Controller
public class UserController {

    /**
     * TODO: 회원가입 view
     */
    @GetMapping("/join")
    public String userJoin() {
        return "user/userJoin";
    }

    /**
     * TODO: 로그인 view
     */
    @GetMapping("/login")
    public String userLogin() {
        return "user/userLogin";
    }
}
