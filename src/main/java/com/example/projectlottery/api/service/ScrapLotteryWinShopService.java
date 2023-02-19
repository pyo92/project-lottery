package com.example.projectlottery.api.service;

import com.example.projectlottery.domain.type.LottoPurchaseType;
import com.example.projectlottery.dto.LottoDto;
import com.example.projectlottery.dto.LottoWinShopDto;
import com.example.projectlottery.dto.ShopDto;
import com.example.projectlottery.service.LottoService;
import com.example.projectlottery.service.LottoWinShopService;
import com.example.projectlottery.service.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ScrapLotteryWinShopService {

    private static final String URL_SHOP_WINNING_LOTTO = "https://www.dhlottery.co.kr/store.do?method=topStore&pageGubun=L645";

    private final ChromeDriverService chromeDriverService;
    private final LottoService lottoService;
    private final ShopService shopService;
    private final LottoWinShopService lottoWinShopService;
    private final ScrapLotteryShopService scrapLotteryShopService;


    /**
     * 로또 6/45 당첨 판매점 정보 스크랩핑
     *
     * @param start 시작 회차
     * @param end   종료 회차
     */
    public void getWinShopL645(long start, long end) {
        chromeDriverService.openWebDriver();
        chromeDriverService.openUrl(URL_SHOP_WINNING_LOTTO, 200);

        for (long i = start; i <= end; i++) {
            getWinShopL645_1st(i);
            getWinShopL645_2nd(i);
        }

        chromeDriverService.closeWebDriver();
    }

    /**
     * 로또 6/45 1등 당첨 판매점 정보 스크랩핑
     *
     * @param drawNo 회차
     */
    private void getWinShopL645_1st(long drawNo) {
        setSelectDrawNo(drawNo);

        String js = "document.getElementById('searchBtn').click();";
        chromeDriverService.procJavaScript(js, 200);

        //1등 복권 판매점 목록 획득
        String css = "#article > div:nth-child(2) > div > div:nth-child(4) > table > tbody > tr";
        List<WebElement> shops = chromeDriverService.getElementsByCssSelector(css);

        for (int i = 1; i <= shops.size(); i++) {
            //판매점 정보 획득
            css = "#article > div:nth-child(2) > div > div:nth-child(4) > table > tbody > tr:nth-child(" + i + ") > td";
            List<WebElement> shopInfos = chromeDriverService.getElementsByCssSelector(css);

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
    private void getWinShopL645_2nd(long drawNo) {
        String css = "#page_box > a";
        int pageCount = chromeDriverService.getElementsByCssSelector(css).size();

        for (int i = 1; i <= pageCount; i++) {
            setSelectDrawNo(drawNo);

            if (i > 1) { //2번째 페이지부터 자바스크립트를 실행해 페이지를 이동
                String js = "selfSubmit(" + i + ");";
                chromeDriverService.procJavaScript(js, 200);
            }

            css = "#article > div:nth-child(2) > div > div:nth-child(5) > table > tbody > tr";
            int shopCount = chromeDriverService.getElementsByCssSelector(css).size();

            for (int j = 1; j <= shopCount; j++) {
                css = "#article > div:nth-child(2) > div > div:nth-child(5) > table > tbody > tr:nth-child(" + j + ") > td";
                List<WebElement> shops = chromeDriverService.getElementsByCssSelector(css);

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
    private void setSelectDrawNo(long drawNo) {
        String css = "#drwNo";
        Select select = new Select(chromeDriverService.getElementByCssSelector(css));

        //공식적으로 제공하는 가장 과거 회차 번호 힉득
        List<WebElement> options = select.getOptions();
        long pastDrawNo = Long.parseLong(options.get(options.size() - 1).getText());

        if (drawNo >= pastDrawNo) {
            //공식적으로 제공하는 회차는 select 옵션 변경해서 검색
            select.selectByValue(String.valueOf(drawNo));
        } else {
            //나머지 회차는 강제로 select value 변경해서 검색
            css = "#drwNo > option:nth-child(1)";
            WebElement option = chromeDriverService.getElementByCssSelector(css);

            String js = "arguments[0].value=" + drawNo + ";";
            chromeDriverService.procJavaScript(js, option, 0);
        }
    }

    /**
     * 로또 6/45 당첨 판매점 정보 저장
     *
     * @param drawNo    회차
     * @param r         등위
     * @param shopInfos 당첨 판매점 정보 table (WebElement list)
     */
    private void saveWinShopL645(long drawNo, int r, List<WebElement> shopInfos) {
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
}
