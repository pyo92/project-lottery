package com.example.projectlottery.controller;

import com.example.projectlottery.api.service.ScrapLotteryShopService;
import com.example.projectlottery.api.service.ScrapLotteryWinService;
import com.example.projectlottery.api.service.ScrapLotteryWinShopService;
import com.example.projectlottery.service.RedisTemplateService;
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

    private final RedisTemplateService redisTemplateService;

    //TODO: 각 scrap 함수들 return 을 ResponseEntity 타입으로 바꾸기. (status)

    /**
     * 동행복권 로또 판매점 정보 scrap 호출 api
     * @param state 지역
     * @return scrap 결과 message
     */
    @GetMapping("/L645/shop")
    public String scrapShop(@RequestParam String state) {
        if (redisTemplateService.getScrapRunningInfo().get("url") != null)
            return "[scrapShop() - failed] Scraping is already running.";

        String url = "/scrap/L645/shop";
        redisTemplateService.saveScrapRunningInfo(url, state);

        try {
            scrapLotteryShopService.getShopL645(state);
        } catch (Exception e) {
            return e.getMessage();
        } finally {
            redisTemplateService.deleteScrapRunningInfo();
        }

        return "[scrapShop() - success] state = " + state;
    }

    /**
     * 동행복권 로또 추첨 결과 당첨 번호 scrap 호출 api
     * @param start 시작 회차 번호
     * @param end 종료 회차 번호
     * @return scrap 결과 message
     */
    @GetMapping("/L645/win/number")
    public String scrapLottoNumber(@RequestParam Long start, @RequestParam(required = false) Long end) {
        if (redisTemplateService.getScrapRunningInfo().get("url") != null)
            return "[scrapLottoNumber() - failed] Scraping is already running.";

        if (Objects.isNull(end)) end = start;

        String url = "/scrap/L645/win/number";
        redisTemplateService.saveScrapRunningInfo(url, start.toString(), end.toString());

        try {
            scrapLotteryWinService.getWinNumbersL645(start, end);
        } catch (Exception e) {
            return e.getMessage();
        } finally {
            redisTemplateService.deleteScrapRunningInfo();
        }

        return "[scrapLottoNumber() - success] start = " + start + ", end = " + end;
    }

    /**
     * 동행복권 로또 추첨 결과 당첨 등위별 상세 정보 scrap 호출 api
     * @param start 시작 회차 번호
     * @param end 종료 회차 번호
     * @return scrap 결과 message
     */
    @GetMapping("/L645/win/prize")
    public String scrapLottoPrize(@RequestParam Long start, @RequestParam(required = false) Long end) {
        if (redisTemplateService.getScrapRunningInfo().get("url") != null)
            return "[scrapLottoPrize() - failed] Scraping is already running.";

        if (Objects.isNull(end)) end = start;

        String url = "/scrap/L645/win/prize";
        redisTemplateService.saveScrapRunningInfo(url, start.toString(), end.toString());

        try {
            scrapLotteryWinService.getWinPrizesL645(start, end);
        } catch (Exception e) {
            return e.getMessage();
        } finally {
            redisTemplateService.deleteScrapRunningInfo();
        }

        return "[scrapLottoPrize() - success] start = " + start + ", end = " + end;
    }

    /**
     * 동행복권 로또 당첨 판매점 정보 scrap 호출 api
     * @param start 시작 회차 번호
     * @param end 종료 회차 번호
     * @return scrap 결과 message
     */
    @GetMapping("/L645/win/shop")
    public String scrapLottoWinShop(@RequestParam Long start, @RequestParam(required = false) Long end) {
        if (redisTemplateService.getScrapRunningInfo().get("url") != null)
            return "[scrapLottoWinShop() - failed] Scraping is already running.";

        if (Objects.isNull(end)) end = start;

        String url = "/scrap/L645/win/shop";
        redisTemplateService.saveScrapRunningInfo(url, start.toString(), end.toString());

        try {
            scrapLotteryWinShopService.getWinShopL645(start, end);
        } catch (Exception e) {
            return e.getMessage();
        } finally {
            redisTemplateService.deleteScrapRunningInfo();
        }

        return "[scrapLottoWinShop() - success] start = " + start + ", end = " + end;
    }
}
