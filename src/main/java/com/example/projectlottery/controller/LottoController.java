package com.example.projectlottery.controller;

import com.example.projectlottery.dto.response.lotto.LottoResponse;
import com.example.projectlottery.service.LottoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

@RequiredArgsConstructor
@RequestMapping("/L645")
@Controller
public class LottoController {

    private final LottoService lottoService;

    @GetMapping
    public String lotto(@RequestParam(required = false) Long drawNo, ModelMap map) {
        Long latestDrawNo = lottoService.getLatestDrawNo();
        if (drawNo == null) {
            return "redirect:/L645?drawNo=" + latestDrawNo;
        }

        Set<Long> drawNos = new TreeSet<>(Comparator.reverseOrder());
        for (long i = 1; i <= latestDrawNo; i++) {
            drawNos.add(i);
        }

        LottoResponse lottoResponse = lottoService.getLottoResponse(drawNo);

        map.addAttribute("drawNos", drawNos);
        map.addAttribute("lottoResponse", lottoResponse);

        return "lotto/index";
    }
}
