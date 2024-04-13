package com.example.projectlottery.api;

import com.example.projectlottery.api.service.ScrapLotteryShopService;
import com.example.projectlottery.api.service.ScrapLotteryWinService;
import com.example.projectlottery.api.service.ScrapLotteryWinShopService;
import com.example.projectlottery.service.LottoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
        try {
            String scrapState = "ALL";
            scrapLotteryShopService.scrapShopL645(scrapState);

        } catch (Exception e) {
            //
        }
    }

    /**
     * 매주 토요일 20시 45분에 실행 (로또 6/45 추첨 결과 - 당첨 번호)
     */
    @Scheduled(cron = "0 45 20 * * SAT", zone = "Asia/Seoul")
    public void scrapWinNumberL645() {
        try {
            long drawNo = lottoService.getLatestDrawNo() + 1;
            scrapLotteryWinService.scrapWinNumbersL645(drawNo, drawNo);

        } catch (Exception e) {
            //
        }
    }

    /**
     * 매주 토요일 21시에 실행 (로또 6/45 추첨 결과 - 등위별 상세 정보)
     */
    @Scheduled(cron = "0 0 21 * * SAT", zone = "Asia/Seoul")
    public void scrapWinPrizeL645() {
        try {
            Long drawNo = lottoService.getLatestDrawNo();
            scrapLotteryWinService.scrapWinPrizesL645(drawNo, drawNo);

        } catch (Exception e) {
            //
        }
    }

    /**
     * 매주 토요일 21시 15분에 실행 (로또 6/45 당첨 판매점 결과)
     */
    @Scheduled(cron = "0 15 21 * * SAT", zone = "Asia/Seoul")
    public void scrapWinL645Shop() {
        try {
            Long drawNo = lottoService.getLatestDrawNo();
            scrapLotteryWinShopService.scrapWinShopL645(drawNo, drawNo);


        } catch (Exception e) {
            //
        }
    }
}
