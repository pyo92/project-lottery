package com.example.projectlottery.api.service;

import com.example.projectlottery.dto.LottoDto;
import com.example.projectlottery.dto.LottoPrizeDto;
import com.example.projectlottery.service.LottoService;
import com.example.projectlottery.service.LottoPrizeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ScrapLotteryWinService {
    private static final String URL_RESULT_LOTTO = "https://www.dhlottery.co.kr/gameResult.do?method=byWin";

    private final ChromeDriverService chromeDriverService;
    private final LottoService lottoService;
    private final LottoPrizeService lottoPrizeService;

    //TODO: 신규 회차만 가져오는 메서드를 만들고 이를 잡에서 사용해 매주 새로운 추첨 회자 정보를 스크랩핑할 수 있도록 한다.

    /**
     * 로또 6/45 회차별 당첨 번호 + 등위별 당첨 금액 스크랩핑
     *
     * @param start 시작 회차
     * @param end   종료 회차
     */
    public void getResultsL645(long start, long end) {
        chromeDriverService.openWebDriver();
        chromeDriverService.openUrl(URL_RESULT_LOTTO, 200);

        for (long i = start; i <= end; i++) {
            getNumbersL645(i);
            getPrizesL645(i);
        }

        chromeDriverService.closeWebDriver();
    }

    private void getNumbersL645(long drawNo) {
        Select select = new Select(chromeDriverService.getElementById("dwrNoList"));
        select.selectByValue(String.valueOf(drawNo)); //회차 변경

        String js = "document.getElementById('searchBtn').click();";
        chromeDriverService.procJavaScript(js, 200); //회차 조회

        //추첨일
        String css = "#article > div:nth-child(2) > div > div.win_result > p";
        String strDrawDt = chromeDriverService.getElementByCssSelector(css).getText().replaceAll("[^0-9]", "");
        LocalDate drawDt = LocalDate.of(
                Integer.parseInt(strDrawDt.substring(0, 4)),
                Integer.parseInt(strDrawDt.substring(4, 6)),
                Integer.parseInt(strDrawDt.substring(6, 8))
        );

        //당첨번호
        css = "#article > div:nth-child(2) > div > div.win_result > div > div.num.win > p > span";
        Set<Integer> numbers = chromeDriverService.getElementsByCssSelector(css)
                .stream().map(WebElement::getText)
                .map(Integer::parseInt)
                .collect(Collectors.toUnmodifiableSet());

        //보너스 번호
        css = "#article > div:nth-child(2) > div > div.win_result > div > div.num.bonus > p > span";
        Integer bonus = Integer.parseInt(chromeDriverService.getElementByCssSelector(css).getText());

        LottoDto dto = LottoDto.of(drawNo, drawDt, numbers, bonus);
        lottoService.save(dto);
    }

    private void getPrizesL645(long drawNo) {
        for (int i = 1; i <= 5; i++) {
            //등위별 당첨자 수 + 당첨 금액
            String css = "#article > div:nth-child(2) > div > table > tbody > tr:nth-child(" + i + ") > td";
            List<WebElement> prizes = chromeDriverService.getElementsByCssSelector(css);

            LottoDto lottoDto = lottoService.getLotto(drawNo);
            Integer rank = i;
            long amount = Long.parseLong(prizes.get(1).getText().replaceAll("[^0-9]", ""));
            long winningCount = Long.parseLong(prizes.get(2).getText().replaceAll("[^0-9]", ""));
            long amountPerGame = Long.parseLong(prizes.get(3).getText().replaceAll("[^0-9]", ""));

            LottoPrizeDto lottoPrizeDto = LottoPrizeDto.of(lottoDto, rank, amount, winningCount, amountPerGame);
            lottoPrizeService.save(lottoPrizeDto);
        }
    }
}
