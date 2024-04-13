package com.example.projectlottery.api.service;

import com.example.projectlottery.dto.LottoDto;
import com.example.projectlottery.dto.LottoPrizeDto;
import com.example.projectlottery.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ScrapLotteryWinService {
    private static final String URL_RESULT_LOTTO = "https://dhlottery.co.kr/gameResult.do?method=byWin";

    private final SeleniumScrapService seleniumScrapService;
    private final LottoService lottoService;
    private final LottoPrizeService lottoPrizeService;

    private final PurchaseResultService purchaseResultService;
    private final UserCombinationService userCombinationService;

    private final RedisTemplateService redisTemplateService;

    private int retryCount;

    private static final int MAX_RETRY_COUNT = 5;
    private static final long RETRY_DELAY = 120000; // 2분

    /**
     * 로또 6/45 회차별 당첨 번호 스크랩핑
     *
     * @param start 시작 회차
     * @param end   종료 회차
     */
    @Retryable(
            retryFor = { NoSuchElementException.class, JavascriptException.class },
            maxAttempts = MAX_RETRY_COUNT,
            backoff = @Backoff(delay = RETRY_DELAY),
            recover = "recoverScrap"
    )
    public void scrapWinNumbersL645(Long start, Long end) {
        log.info("=== Started scrapWinNumberL645() : {}", LocalDateTime.now());

        try {
            //TODO: 추후, scrap 프로젝트도 별도로 분리되면
            // redis 가 아닌 다른 방법으로 사용중인지 여부를 관리하는 것으로 바꾸도록 하자. (아마 rest api 통신 할 듯?)
            String url = "/scrap/L645/win/number";
            redisTemplateService.saveScrapRunningInfo(url, start.toString(), end.toString());

            seleniumScrapService.openWebDriver();
            seleniumScrapService.openUrl(URL_RESULT_LOTTO);

            for (long i = start; i <= end; i++) {
                log.info("=== Processing scrapWinNumberL645() - {} : {}", i, LocalDateTime.now());
                getNumbersL645(i);

                //당첨번호 scrap 후, 당첨번호를 가져온다. (for 구매내역과 조합내역에 대한 등위 업데이트 처리)
                LottoDto lotto = lottoService.getLotto(i);
                List<Integer> winNumber = new ArrayList<>();
                winNumber.add(lotto.number1());
                winNumber.add(lotto.number2());
                winNumber.add(lotto.number3());
                winNumber.add(lotto.number4());
                winNumber.add(lotto.number5());
                winNumber.add(lotto.number6());

                //구매내역과 조합내역에 대한 등위 업데이트 처리
                purchaseResultService.updatePurchasedWin(i, winNumber, lotto.numberB());
                userCombinationService.updateCombinationWin(i, winNumber, lotto.numberB());
            }

            //스크랩핑을 통해 최신 회차 정보가 변경되었기에 cache clear
            redisTemplateService.flushAllCache();

            log.info("=== Success scrapWinNumberL645() : {}", LocalDateTime.now());

        } catch (NoSuchElementException e) {
            log.warn("=== Failed by NoSuchElementException - scrapWinNumberL645(), retry 2 min later... ({}/{})", ++retryCount, MAX_RETRY_COUNT);

            throw e;

        } catch (JavascriptException e) {
            log.warn("=== Failed by JavascriptException - scrapWinNumberL645(), retry 2 min later... ({}/{})", ++retryCount, MAX_RETRY_COUNT);

            throw e;

        } finally {
            redisTemplateService.deleteScrapRunningInfo();
            seleniumScrapService.closeWebDriver();
        }
    }

    /**
     * 로또 6/45 회차별 등위별 당첨 금액 스크랩핑
     *
     * @param start 시작 회차
     * @param end   종료 회차
     */
    @Retryable(
            retryFor = { NoSuchElementException.class, JavascriptException.class },
            maxAttempts = MAX_RETRY_COUNT,
            backoff = @Backoff(delay = RETRY_DELAY),
            recover = "recoverScrap"
    )
    public void scrapWinPrizesL645(Long start, Long end) {
        log.info("=== Started scrapWinPrizesL645() : {}", LocalDateTime.now());

        try {
            //TODO: 추후, scrap 프로젝트도 별도로 분리되면
            // redis 가 아닌 다른 방법으로 사용중인지 여부를 관리하는 것으로 바꾸도록 하자. (아마 rest api 통신 할 듯?)
            String url = "/scrap/L645/win/prize";
            redisTemplateService.saveScrapRunningInfo(url, start.toString(), end.toString());

            seleniumScrapService.openWebDriver();
            seleniumScrapService.openUrl(URL_RESULT_LOTTO);

            for (long i = start; i <= end; i++) {
                log.info("=== Processing scrapWinPrizesL645() - {} : {}", i, LocalDateTime.now());
                getPrizesL645(i);
            }

            //스크랩핑을 통해 최신 회차 정보가 변경되었기에 cache clear
            redisTemplateService.flushAllCache();

            log.info("=== Success scrapWinPrizesL645() : {}", LocalDateTime.now());

        } catch (NoSuchElementException e) {
            log.warn("=== Failed by NoSuchElementException - scrapWinPrizesL645(), retry 2 min later... ({}/{})", ++retryCount, MAX_RETRY_COUNT);

            throw e;

        } catch (JavascriptException e) {
            log.warn("=== Failed by JavascriptException - scrapWinPrizesL645(), retry 2 min later... ({}/{})", ++retryCount, MAX_RETRY_COUNT);

            throw e;

        } finally {
            redisTemplateService.deleteScrapRunningInfo();
            seleniumScrapService.closeWebDriver();
        }
    }

    private void getNumbersL645(long drawNo) throws NoSuchElementException, JavascriptException {
        Select select = new Select(seleniumScrapService.getElementById("dwrNoList"));
        select.selectByValue(String.valueOf(drawNo)); //회차 변경

        String js = "document.getElementById('searchBtn').click();";
        seleniumScrapService.procJavaScript(js); //회차 조회

        //추첨일
        String css = "#article > div:nth-child(2) > div > div.win_result > p";
        String strDrawDt = seleniumScrapService.getElementByCssSelector(css).getText().replaceAll("[^0-9]", "");
        LocalDate drawDt = LocalDate.of(
                Integer.parseInt(strDrawDt.substring(0, 4)),
                Integer.parseInt(strDrawDt.substring(4, 6)),
                Integer.parseInt(strDrawDt.substring(6, 8))
        );

        //당첨번호
        css = "#article > div:nth-child(2) > div > div.win_result > div > div.num.win > p > span";
        Set<Integer> numbers = seleniumScrapService.getElementsByCssSelector(css)
                .stream().map(WebElement::getText)
                .map(Integer::parseInt)
                .collect(Collectors.toUnmodifiableSet());

        //보너스 번호
        css = "#article > div:nth-child(2) > div > div.win_result > div > div.num.bonus > p > span";
        Integer bonus = Integer.parseInt(seleniumScrapService.getElementByCssSelector(css).getText());

        LottoDto dto = LottoDto.of(drawNo, drawDt, numbers, bonus);
        lottoService.save(dto);
    }

    private void getPrizesL645(long drawNo) throws NoSuchElementException, JavascriptException {
        Select select = new Select(seleniumScrapService.getElementById("dwrNoList"));
        select.selectByValue(String.valueOf(drawNo)); //회차 변경

        String js = "document.getElementById('searchBtn').click();";
        seleniumScrapService.procJavaScript(js); //회차 조회

        for (int i = 1; i <= 5; i++) {
            //등위별 당첨자 수 + 당첨 금액
            String css = "#article > div:nth-child(2) > div > table > tbody > tr:nth-child(" + i + ") > td";
            List<WebElement> prizes = seleniumScrapService.getElementsByCssSelector(css);

            LottoDto lottoDto = lottoService.getLotto(drawNo);
            Integer rank = i;
            long amount = Long.parseLong(prizes.get(1).getText().replaceAll("[^0-9]", ""));
            long winningCount = Long.parseLong(prizes.get(2).getText().replaceAll("[^0-9]", ""));
            long amountPerGame = Long.parseLong(prizes.get(3).getText().replaceAll("[^0-9]", ""));

            LottoPrizeDto lottoPrizeDto = LottoPrizeDto.of(lottoDto, rank, amount, winningCount, amountPerGame);
            lottoPrizeService.save(lottoPrizeDto);
        }
    }

    @Recover
    private void recoverScrap(NoSuchElementException e, Long start, Long end) {
        retryCount = 0; //retry count 리셋

        throw e; //예외를 그대로 상위 객체로 던져준다.
    }

    @Recover
    private void recoverScrap(JavascriptException e, Long start, Long end) {
        retryCount = 0; //retry count 리셋

        throw e; //예외를 그대로 상위 객체로 던져준다.
    }
}
