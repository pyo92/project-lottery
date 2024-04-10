package com.example.projectlottery.api.service;

import com.example.projectlottery.domain.type.LottoPurchaseType;
import com.example.projectlottery.dto.LottoDto;
import com.example.projectlottery.dto.LottoWinShopDto;
import com.example.projectlottery.dto.ShopDto;
import com.example.projectlottery.service.LottoService;
import com.example.projectlottery.service.LottoWinShopService;
import com.example.projectlottery.service.RedisTemplateService;
import com.example.projectlottery.service.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
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
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ScrapLotteryWinShopService {

    private static final String URL_SHOP_WINNING_LOTTO = "https://dhlottery.co.kr/store.do?method=topStore&pageGubun=L645";

    private final SeleniumScrapService seleniumScrapService;
    private final LottoService lottoService;
    private final ShopService shopService;
    private final LottoWinShopService lottoWinShopService;
    private final ScrapLotteryShopService scrapLotteryShopService;

    private final RedisTemplateService redisTemplateService;

    private int retryCount;

    private static final int MAX_RETRY_COUNT = 5;
    private static final long RETRY_DELAY = 120000; // 2분


    /**
     * 로또 6/45 당첨 판매점 정보 스크랩핑
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
    public void scrapWinShopL645(Long start, Long end) {
        log.info("=== Started scrapWinShopL645() : {}", LocalDateTime.now());

        try {
            //TODO: 추후, scrap 프로젝트도 별도로 분리되면
            // redis 가 아닌 다른 방법으로 사용중인지 여부를 관리하는 것으로 바꾸도록 하자. (아마 rest api 통신 할 듯?)
            String url = "/scrap/L645/win/shop";
            redisTemplateService.saveScrapRunningInfo(url, start.toString(), end.toString());

            seleniumScrapService.openWebDriver();
            seleniumScrapService.openUrl(URL_SHOP_WINNING_LOTTO);

            for (long i = start; i <= end; i++) {
                log.info("=== Processing scrapWinShopL645() 1st - {} : {}", i, LocalDateTime.now());
                getWinShopL645_1st(i);
                log.info("=== Processing scrapWinShopL645() 2nd - {} : {}", i, LocalDateTime.now());
                getWinShopL645_2nd(i);
            }

            //스크랩핑을 통해 최신 회차 정보가 반영되었기에 cache clear
            redisTemplateService.flushAllCache();

        } catch (NoSuchElementException e) {
            log.warn("=== Failed by NoSuchElementException - scrapWinShopL645(), retry 2 min later... ({}/{})", ++retryCount, MAX_RETRY_COUNT);

            throw e;

        } catch (JavascriptException e) {
            log.warn("=== Failed by JavascriptException - scrapWinShopL645(), retry 2 min later... ({}/{})", ++retryCount, MAX_RETRY_COUNT);

            throw e;

        } finally {
            redisTemplateService.deleteScrapRunningInfo();
            seleniumScrapService.closeWebDriver();
        }
    }

    /**
     * 로또 6/45 1등 당첨 판매점 정보 스크랩핑
     *
     * @param drawNo 회차
     */
    private void getWinShopL645_1st(long drawNo) throws NoSuchElementException, JavascriptException {
        setSelectDrawNo(drawNo);

        String js = "document.getElementById('searchBtn').click();";
        seleniumScrapService.procJavaScript(js);

        //1등 복권 판매점 목록 획득
        String css = "#article > div:nth-child(2) > div > div:nth-child(4) > table > tbody > tr";
        List<WebElement> shops = seleniumScrapService.getElementsByCssSelector(css);

        for (int i = 1; i <= shops.size(); i++) {
            //판매점 정보 획득
            css = "#article > div:nth-child(2) > div > div:nth-child(4) > table > tbody > tr:nth-child(" + i + ") > td";
            List<WebElement> shopInfos = seleniumScrapService.getElementsByCssSelector(css);

            //1등 당첨자 없는 회차 대응
            if (shopInfos.size() == 1 && shopInfos.get(0).getText().equals("조회 결과가 없습니다.")) {
                break;
            }

            saveWinShopL645(drawNo, 1, shopInfos);
        }
    }

    /**
     * 로또 6/45 2등 당첨 판매점 정보 스크랩핑
     *
     * @param drawNo 회차
     */
    private void getWinShopL645_2nd(long drawNo) throws NoSuchElementException, JavascriptException {
        String css = "#page_box > a";

        int pageCount = seleniumScrapService.getElementsByCssSelector(css).size();
        System.out.println("pageCount = " + pageCount);

        //20230406 - 2등 당첨내역 scrap 오류 수정
        if (seleniumScrapService.getElementsByCssSelector(css).size() > 10) {
            pageCount = Integer.parseInt(seleniumScrapService
                    .getElementsByCssSelector("#page_box > a.go.end").get(0)
                    .getAttribute("onclick")
                    .replaceAll("[^0-9]", ""));
        }

        for (int i = 1; i <= pageCount; i++) {
            setSelectDrawNo(drawNo);

            if (i > 1) { //2번째 페이지부터 자바스크립트를 실행해 페이지를 이동
                String js = "selfSubmit(" + i + ");";
                seleniumScrapService.procJavaScript(js);
            }

            css = "#article > div:nth-child(2) > div > div:nth-child(5) > table > tbody > tr";
            int shopCount = seleniumScrapService.getElementsByCssSelector(css).size();

            for (int j = 1; j <= shopCount; j++) {
                css = "#article > div:nth-child(2) > div > div:nth-child(5) > table > tbody > tr:nth-child(" + j + ") > td";
                List<WebElement> shops = seleniumScrapService.getElementsByCssSelector(css);

                //당첨 정보 바인딩
                saveWinShopL645(drawNo, 2, shops);
            }
        }
    }

    /**
     * 회차 select option 변경
     *
     * @param drawNo 회차
     */
    private void setSelectDrawNo(long drawNo) throws NoSuchElementException, JavascriptException {
        String css = "#drwNo";
        Select select = new Select(seleniumScrapService.getElementByCssSelector(css));

        //공식적으로 제공하는 가장 과거 회차 번호 힉득
        List<WebElement> options = select.getOptions();
        long pastDrawNo = Long.parseLong(options.get(options.size() - 1).getText());

        if (drawNo >= pastDrawNo) {
            //공식적으로 제공하는 회차는 select 옵션 변경해서 검색
            select.selectByValue(String.valueOf(drawNo));
        } else {
            //나머지 회차는 강제로 select value 변경해서 검색
            css = "#drwNo > option:nth-child(1)";
            WebElement option = seleniumScrapService.getElementByCssSelector(css);

            String js = "arguments[0].value=" + drawNo + ";";
            seleniumScrapService.procJavaScript(js, option);
        }
    }

    /**
     * 로또 6/45 당첨 판매점 정보 저장
     *
     * @param drawNo    회차
     * @param r         등위
     * @param shopInfos 당첨 판매점 정보 table (WebElement list)
     */
    private void saveWinShopL645(long drawNo, int r, List<WebElement> shopInfos) throws NoSuchElementException {
        //당첨 정보 바인딩
        LottoDto lottoDto = lottoService.getLotto(drawNo);
        Integer rank = r;
        Integer no = Integer.parseInt(shopInfos.get(0).getText());
        LottoPurchaseType lottoPurchaseType = (r == 1 ?
                LottoPurchaseType.getLottoType(shopInfos.get(2).getText()) : LottoPurchaseType.NONE);
        String displayAddress = shopInfos.get(r == 1 ? 3 : 2).getText();

        //판매점 id 획득
        Long shopId = Long.parseLong(shopInfos.get(r == 1 ? 4 : 3).findElement(By.cssSelector("a"))
                .getAttribute("onclick")
                .replaceAll("[^0-9]", "")
        );

        //해당 판매점이 shop 테이블에 존재하면 그대로 FK 로 지정하고,
        //그렇지 않으면 새로 shop 테이블에 저장하고, 해당 객체를 FK 로 지정한다. (orElseGet)
        ShopDto shopDto = shopService.getShopById(shopId).orElseGet(() -> {
            String address = shopInfos.get(r == 1 ? 3 : 2).getText();
            String name = shopInfos.get(1).getText();
            String tel = "";

            //해당 주소에 대한 위도, 경도 정보 획득
            double[] geoCoordinate = scrapLotteryShopService.getGeoCoordinateByAddress(
                    address,
                    shopInfos.get(r == 1 ? 4 : 3).findElement(By.cssSelector("a")).getAttribute("onclick")
            );
            double longitude = geoCoordinate[0];
            double latitude = geoCoordinate[1];

            //해당 위도, 경도에 대한 행정구역 정보 획득
            String[] states = scrapLotteryShopService.getStateByGeoCoordinate(longitude, latitude);
            String state1 = states[0];
            String state2 = states[1];
            String state3 = states[2];

            //해당 판매점은 더 이상 복권을 판매하지 않는 것으로 간주하고, 모든 복권 판매 여부를 false 로 세팅
            ShopDto dto = ShopDto.of(
                    shopId,
                    address,
                    name,
                    tel,
                    longitude,
                    latitude,
                    false,
                    false,
                    false,
                    state1,
                    state2,
                    state3,
                    LocalDate.now()
            );

            shopService.save(dto);

            return dto;
        });

        LottoWinShopDto lottoWinShopDto = LottoWinShopDto.of(
                lottoDto,
                rank,
                no,
                shopDto,
                lottoPurchaseType,
                displayAddress
        );

        lottoWinShopService.save(lottoWinShopDto);
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
