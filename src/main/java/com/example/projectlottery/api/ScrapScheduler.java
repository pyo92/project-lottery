package com.example.projectlottery.api;

import com.example.projectlottery.api.service.ScrapLotteryShopService;
import com.example.projectlottery.api.service.ScrapLotteryWinService;
import com.example.projectlottery.api.service.ScrapLotteryWinShopService;
import com.example.projectlottery.service.LottoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Component
public class ScrapScheduler {

    private final LottoService lottoService;

    private final ScrapLotteryShopService scrapLotteryShopService;
    private final ScrapLotteryWinService scrapLotteryWinService;
    private final ScrapLotteryWinShopService scrapLotteryWinShopService;

    /**
     * 매주 토요일 3시에 실행 (로또 6/45 판매점)
     */
    @Scheduled(cron = "0 0 3 * * SAT", zone = "Asia/Seoul")
    public void scrapShopL645() {
        log.info("=== Started scrapShopL645() : {}", LocalDateTime.now());

        try {
            String scrapState = "ALL";
            scrapLotteryShopService.getShopL645(scrapState);
        } catch (Exception e) {
            log.error("=== Failed scrapShopL645() : {}", LocalDateTime.now());
        }

        log.info("=== Success scrapShopL645() : {}", LocalDateTime.now());
    }

    /**
     * 매주 토요일 20시 45분에 실행 (로또 6/45 추첨 결과 - 당첨 번호)
     */
    @Scheduled(cron = "0 45 20 * * SAT", zone = "Asia/Seoul")
    public void scrapWinNumberL645() {
        log.info("=== Started scrapWinNumberL645() : {}", LocalDateTime.now());

        try {
            long drawNo = lottoService.getLatestDrawNo() + 1;
            scrapLotteryWinService.getWinNumbersL645(drawNo, drawNo);
        } catch (Exception e) {
            log.error("=== Failed scrapWinNumberL645() : {}", LocalDateTime.now());
        }

        log.info("=== Success scrapWinNumberL645() : {}", LocalDateTime.now());
    }

    /**
     * 매주 토요일 21시에 실행 (로또 6/45 추첨 결과 - 등위별 상세 정보)
     */
    @Scheduled(cron = "0 0 21 * * SAT", zone = "Asia/Seoul")
    public void scrapWinPrizeL645() {
        log.info("=== Started scrapWinPrizeL645() : {}", LocalDateTime.now());

        try {
            Long drawNo = lottoService.getLatestDrawNo();
            scrapLotteryWinService.getWinPrizesL645(drawNo, drawNo);
        } catch (Exception e) {
            log.error("=== Failed scrapWinPrizeL645() : {}", LocalDateTime.now());
        }

        log.info("=== Success scrapWinPrizeL645() : {}", LocalDateTime.now());
    }

    /**
     * 매주 토요일 21시 10분에 실행 (로또 6/45 당첨 판매점 결과)
     */
    @Scheduled(cron = "0 10 21 * * SAT", zone = "Asia/Seoul")
    public void scrapWinL645Shop() {
        log.info("=== Started scrapWinL645Shop() : {}", LocalDateTime.now());

        try {
            Long drawNo = lottoService.getLatestDrawNo();
            scrapLotteryWinShopService.getWinShopL645(drawNo, drawNo);
        } catch (Exception e) {
            log.error("=== Failed scrapWinL645Shop() : {}", LocalDateTime.now());
        }

        log.info("=== Success scrapWinL645Shop() : {}", LocalDateTime.now());
    }
}
