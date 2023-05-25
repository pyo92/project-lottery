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

    /**
     * 로또추첨결과 view
     * @param drawNo 회차번호
     * @param map thymeleaf binding model map
     * @return 로또추첨결과 view file name
     */
    @GetMapping
    public String lotto(@RequestParam(required = false) Long drawNo, ModelMap map) {
        //회차 예외처리
        Long latestDrawNo = lottoService.getLatestDrawNo();
        if (drawNo == null || drawNo > latestDrawNo || drawNo < 1) {
            return "redirect:/L645?drawNo=" + latestDrawNo;
        }

        //회차 선택 selector value list
        List<Long> drawNos = LongStream.range(1, latestDrawNo + 1)
                .boxed()
                .sorted(Comparator.reverseOrder()).toList();

        //추첨결과 response dto
        LottoResponse lottoResponse = lottoService.getLottoResponse(drawNo);

        //model map binding
        map.addAttribute("latestDrawNo", latestDrawNo);
        map.addAttribute("drawNos", drawNos);
        map.addAttribute("lottoResponse", lottoResponse);

        return "lotto/drawResult";
    }

    /**
     * TODO: 로또도구 view
     */
    @GetMapping("/tool")
    public String lottoNumberGenerator() {
        return "lotto/lottoTool";
    }

    /**
     * TODO: 로또분석 view
     */
    @GetMapping("/analysis")
    public String lottoStatistics() {
        return "lotto/lottoAnalysis";
    }
}
