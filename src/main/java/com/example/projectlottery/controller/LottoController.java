package com.example.projectlottery.controller;

import com.example.projectlottery.dto.response.LottoResponse;
import com.example.projectlottery.service.LottoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;
import java.util.List;
import java.util.stream.LongStream;

@RequiredArgsConstructor
@RequestMapping("/L645")
@Controller
public class LottoController {

    private final LottoService lottoService;

    @GetMapping
    public String lotto(@RequestParam(required = false) Long drawNo, ModelMap map) {
        Long latestDrawNo = lottoService.getLatestDrawNo();
        if (drawNo == null || drawNo > latestDrawNo || drawNo < 1) {
            return "redirect:/L645?drawNo=" + latestDrawNo;
        }

        List<Long> drawNos = LongStream.range(1, latestDrawNo + 1)
                .boxed()
                .sorted(Comparator.reverseOrder()).toList();

        LottoResponse lottoResponse = lottoService.getLottoResponse(drawNo);

        map.addAttribute("latestDrawNo", latestDrawNo);
        map.addAttribute("drawNos", drawNos);
        map.addAttribute("lottoResponse", lottoResponse);

        return "lotto/drawResult";
    }
}
