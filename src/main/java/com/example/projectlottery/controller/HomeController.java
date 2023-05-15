package com.example.projectlottery.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping("/")
@Controller
public class HomeController {

    /**
     * index view
     * @return index view file name
     */
    @GetMapping
    public String home() {
        return "index";
    }
}
