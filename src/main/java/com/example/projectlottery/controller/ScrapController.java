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

    /**
     * TODO: 관리자 외에는 접근할 수 없도록 spring security 처리 (after 회원가입, 로그인 기능 개발)
     * 동행복권 로또 판매점 정보 scrap 호출 api
     * @param state 지역
     * @return scrap 결과 message
     */
    @GetMapping("/L645/shop")
    public String scrapShop(@RequestParam String state) {
        try {
            scrapLotteryShopService.getShopL645(state);
        } catch (Exception e) {
            return e.getMessage();
        }

        return "[scrapShop() - success] state = " + state;
    }

    /**
     * TODO: 관리자 외에는 접근할 수 없도록 spring security 처리 (after 회원가입, 로그인 기능 개발)
     * 동행복권 로또 추첨 결과 당첨 번호 scrap 호출 api
     * @param start 시작 회차 번호
     * @param end 종료 회차 번호
     * @return scrap 결과 message
     */
    @GetMapping("/L645/win/number")
    public String scrapLottoNumber(@RequestParam Long start, @RequestParam(required = false) Long end) {
        if (Objects.isNull(end)) end = start;
        try {
            scrapLotteryWinService.getWinNumbersL645(start, end);
        } catch (Exception e) {
            return e.getMessage();
        }

        return "[scrapLottoNumber() - success] start = " + start + ", end = " + end;
    }

    /**
     * TODO: 관리자 외에는 접근할 수 없도록 spring security 처리 (after 회원가입, 로그인 기능 개발)
     * 동행복권 로또 추첨 결과 당첨 등위별 상세 정보 scrap 호출 api
     * @param start 시작 회차 번호
     * @param end 종료 회차 번호
     * @return scrap 결과 message
     */
    @GetMapping("/L645/win/prize")
    public String scrapLottoPrize(@RequestParam Long start, @RequestParam(required = false) Long end) {
        if (Objects.isNull(end)) end = start;
        try {
            scrapLotteryWinService.getWinPrizesL645(start, end);
        } catch (Exception e) {
            return e.getMessage();
        }

        return "[scrapLottoPrize() - success] start = " + start + ", end = " + end;
    }

    /**
     * TODO: 관리자 외에는 접근할 수 없도록 spring security 처리 (after 회원가입, 로그인 기능 개발)
     * 동행복권 로또 당첨 판매점 정보 scrap 호출 api
     * @param start 시작 회차 번호
     * @param end 종료 회차 번호
     * @return scrap 결과 message
     */
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
