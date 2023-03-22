package com.example.projectlottery.api;

import com.example.projectlottery.api.service.ScrapLotteryShopService;
import com.example.projectlottery.api.service.ScrapLotteryWinService;
import com.example.projectlottery.api.service.ScrapLotteryWinShopService;
import com.example.projectlottery.service.LottoService;
import com.example.projectlottery.service.RedisTemplateService;
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

    private final RedisTemplateService redisTemplateService;

    private final ScrapLotteryShopService scrapLotteryShopService;
    private final ScrapLotteryWinService scrapLotteryWinService;
    private final ScrapLotteryWinShopService scrapLotteryWinShopService;

    @Scheduled(cron = "0 0 3 * * SAT", zone = "Asia/Seoul") //매주 토요일 3시에 실행 (로또 6/45 판매점)
    public void scrapShopL645() {
        log.info("=== Started scrapShopL645() : {}", LocalDateTime.now());

        try {
            String scrapState = "ALL";
            scrapLotteryShopService.getShopL645(scrapState);
        } catch (Exception e) {
            log.error("=== Failed scrapShopL645() : {}", LocalDateTime.now());
        } finally {
            redisTemplateService.deleteAllShopDetail(); //redis cache 삭제 -> 신규 또는 판매중지 판매점 변동되므로
        }

        log.info("=== Success scrapShopL645() : {}", LocalDateTime.now());
    }

    @Scheduled(cron = "0 0 22 * * SAT", zone = "Asia/Seoul") //매주 토요일 22시에 실행 (로또 6/45 추첨 결과)
    public void scrapWinL645() {
        log.info("=== Started scrapWinL645() : {}", LocalDateTime.now());

        try {
            Long drawNo = lottoService.getLatestDrawNo() + 1;
            scrapLotteryWinService.getResultsL645(drawNo, drawNo);
            scrapLotteryWinShopService.getWinShopL645(drawNo, drawNo);
        } catch (Exception e) {
            log.error("=== Failed scrapWinL645() : {}", LocalDateTime.now());
        } finally {
            redisTemplateService.deleteALlShopRanking(); //redis cache 삭제 -> 랭킹은 일주일마다 변동되므로
        }

        log.info("=== Success scrapWinL645() : {}", LocalDateTime.now());
    }
}
