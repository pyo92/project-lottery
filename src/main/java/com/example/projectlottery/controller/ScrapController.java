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

    //TODO: 각 scrap 함수들 return 을 ResponseEntity 타입으로 바꾸기. (status) - msa 구조, rest api 통신으로 가기위함.

    //TODO: win 관련 scrap 함수 3개 하나로 합칠 수도 있을 거 같다?

    /**
     * 동행복권 로또 판매점 정보 scrap 호출 api
     * @param state 지역
     * @return scrap 결과 message
     */
    @GetMapping("/L645/shop")
    public String scrapShop(@RequestParam String state) {
        // TODO: 추후, scrap 프로젝트도 별도로 분리되면
        //  webDriver 가 null 인지 체크해서 사용중인지 확인하고 반환하도록 하자. (아마 rest api 통신 할 듯?)
        if (redisTemplateService.getScrapRunningInfo().url() != null)
            return "[scrapShop() - failed] Scraping is already running.";

        try {
            scrapLotteryShopService.scrapShopL645(state);

        } catch (Exception e) {
            return e.getMessage();
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
        // TODO: 추후, scrap 프로젝트도 별도로 분리되면
        //  webDriver 가 null 인지 체크해서 사용중인지 확인하고 반환하도록 하자. (아마 rest api 통신 할 듯?)
        if (redisTemplateService.getScrapRunningInfo().url() != null)
            return "[scrapLottoNumber() - failed] Scraping is already running.";

        if (Objects.isNull(end)) end = start;

        try {
            scrapLotteryWinService.scrapWinNumbersL645(start, end);

        } catch (Exception e) {
            return e.getMessage();
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
        // TODO: 추후, scrap 프로젝트도 별도로 분리되면
        //  webDriver 가 null 인지 체크해서 사용중인지 확인하고 반환하도록 하자. (아마 rest api 통신 할 듯?)
        if (redisTemplateService.getScrapRunningInfo().url() != null)
            return "[scrapLottoPrize() - failed] Scraping is already running.";

        if (Objects.isNull(end)) end = start;

        try {
            scrapLotteryWinService.scrapWinPrizesL645(start, end);
        } catch (Exception e) {
            return e.getMessage();
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
        // TODO: 추후, scrap 프로젝트도 별도로 분리되면
        //  webDriver 가 null 인지 체크해서 사용중인지 확인하고 반환하도록 하자. (아마 rest api 통신 할 듯?)
        if (redisTemplateService.getScrapRunningInfo().url() != null)
            return "[scrapLottoWinShop() - failed] Scraping is already running.";

        if (Objects.isNull(end)) end = start;

        try {
            scrapLotteryWinShopService.scrapWinShopL645(start, end);
        } catch (Exception e) {
            return e.getMessage();
        }

        return "[scrapLottoWinShop() - success] start = " + start + ", end = " + end;
    }
}
