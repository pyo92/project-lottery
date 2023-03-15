package com.example.projectlottery.controller;

import com.example.projectlottery.service.LottoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping("/")
@Controller
public class HomeController {

    private final LottoService lottoService;

    @GetMapping
    public String home() {
        Long latestDrawNo = lottoService.getLatestDrawNo();

        return "redirect:/L645?drawNo=" + latestDrawNo;
    }
}
