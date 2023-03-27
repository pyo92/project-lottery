package com.example.projectlottery.controller;

import com.example.projectlottery.api.service.ScrapLotteryShopService;
import com.example.projectlottery.api.service.ScrapLotteryWinService;
import com.example.projectlottery.api.service.ScrapLotteryWinShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RequiredArgsConstructor
@RequestMapping("/scrap")
@RestController
public class ScrapController {

    private final ScrapLotteryShopService scrapLotteryShopService;
    private final ScrapLotteryWinService scrapLotteryWinService;
    private final ScrapLotteryWinShopService scrapLotteryWinShopService;

    @GetMapping("/shop")
    public String scrapShop(@RequestParam String state) {
        try {
            scrapLotteryShopService.getShopL645(state);
        } catch (Exception e) {
            return e.getMessage();
        }

        return "[scrapShop() - success] state = " + state;
    }

    @GetMapping("/L645/win")
    public String scrapLotto(@RequestParam Long start, @RequestParam(required = false) Long end) {
        if (Objects.isNull(end)) end = start;
        try {
            scrapLotteryWinService.getResultsL645(start, end);
        } catch (Exception e) {
            return e.getMessage();
        }

        return "[scrapLotto() - success] start = " + start + ", end = " + end;
    }

    @GetMapping("/L645/win/shop")
    public String scrapLottoWinShop(@RequestParam Long start, @RequestParam(required = false) Long end) {
        if (Objects.isNull(end)) end = start;
        try {
            scrapLotteryWinShopService.getWinShopL645(start, end);
        } catch (Exception e) {
            return e.getMessage();
        }

        return "[scrapLottoWinShop() - success] start = " + start + ", end = " + end;
    }
}
