package com.example.projectlottery.api.service;

import com.example.projectlottery.api.dto.GeoCoordinateToRegionDocsDto;
import com.example.projectlottery.api.dto.response.KakaoGeoCoordinateToRegionResponse;
import com.example.projectlottery.api.dto.response.KakaoSearchAddressResponse;
import com.example.projectlottery.domain.type.ScrapStateType;
import com.example.projectlottery.dto.ShopDto;
import com.example.projectlottery.service.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class ScrapLotteryShopService {

    private static final String URL_SHOP_LOTTO = "https://www.dhlottery.co.kr/store.do?method=sellerInfo645";

    private final ChromeDriverService chromeDriverService;
    private final ShopService shopService;
    private final KakaoLocalApiService kakaoLocalApiService;

    /**
     * 로또 6/45 판매점 정보 스크랩핑
     *
     * @param state 시.도
     */
    public void getShopL645(String state) {
        chromeDriverService.openWebDriver();
        chromeDriverService.openUrl(URL_SHOP_LOTTO, 200);

        ScrapStateType shopScrapStateType = ScrapStateType.valueOf(state);

        if (shopScrapStateType == ScrapStateType.ALL) {
            Arrays.stream(ScrapStateType.values())
                    .filter(scrapStateType -> scrapStateType.ordinal() > 0)
                    .toList()
                    .forEach(this::scrapShopL645ByState);
        } else {
            scrapShopL645ByState(shopScrapStateType);
        }

        //스크랩핑 완료 후, 판매 중단 대상 판매점 처리 (스크랩핑 일자가 과거면서 l645YN = true)
        setShopWithdrawL645();

        chromeDriverService.closeWebDriver();
    }

    /**
     * 로또 6/45 판매점 정보 스크랩핑
     *
     * @param scrapStateType 시.도 Enum
     */
    private void scrapShopL645ByState(ScrapStateType scrapStateType) {
        String css;
        String js;

        //타 시.도로 이동해 구 단위 필터 해제
        if (scrapStateType == ScrapStateType.SEOUL) {
            css = "#mainMenuArea > a:nth-child(2)";
            js = chromeDriverService.getElementByCssSelector(css).getAttribute("onclick");
            chromeDriverService.procJavaScript(js, 200);
        }

        //대상 시.도로 이동
        css = "#mainMenuArea > a:nth-child(" + scrapStateType.ordinal() + ")";
        js = chromeDriverService.getElementByCssSelector(css).getAttribute("onclick");
        chromeDriverService.procJavaScript(js, 200);

        //항상 1 페이지부터 시작할 수 있도록 설정
        js = "$.selfSubmit(1);";
        chromeDriverService.procJavaScript(js, 200);

        //현재 pagination view 에 [끝 페이지] 링크가 존재하는지 확인
        css = "#pagingView > a";
        int firstPagingViewItemCount = chromeDriverService.getElementsByCssSelector(css).size();

        //마지막 페이지 번호 획득
        int lastPageNo = firstPagingViewItemCount;
        if (firstPagingViewItemCount > 10) {
            css = "#pagingView > a.go.end";
            lastPageNo = Integer.parseInt(chromeDriverService.getElementByCssSelector(css)
                    .getAttribute("href")
                    .replaceAll("[^0-9]", ""));
        }

        int curPageNo = 1;
        while (curPageNo <= lastPageNo) {
            //항상 1 페이지부터 시작할 수 있도록 세팅
            js = "$.selfSubmit(" + curPageNo + ");";
            chromeDriverService.procJavaScript(js, 200);

            css = "#resultTable > tbody > tr";
            List<WebElement> Shops = chromeDriverService.getElementsByCssSelector(css);

            for (WebElement shop : Shops) {
                //판매점 id 획득
                Long id = Long.parseLong(shop.findElement(By.cssSelector("td:nth-child(4) > a"))
                        .getAttribute("onclick")
                        .replaceAll("[^0-9]", "")
                );

                //상호명, 전화번호, 주소 정보 획득
                String name = shop.findElement(By.cssSelector("td:nth-child(1)")).getText();
                String tel = shop.findElement(By.cssSelector("td:nth-child(2)")).getText();
                String address = shop.findElement(By.cssSelector("td:nth-child(3)")).getText();

                //해당 주소에 대한 위도, 경도 정보 획득
                double[] geoCoordinate = getGeoCoordinateByAddress(
                        address,
                        shop.findElement(By.cssSelector("td:nth-child(4) > a")).getAttribute("onclick")
                );
                double longitude = geoCoordinate[0];
                double latitude = geoCoordinate[1];

                //해당 위도, 경도에 대한 행정구역 정보 획득
                String[] states = getStateByGeoCoordinate(longitude, latitude);
                String state1 = states[0];
                String state2 = states[1];
                String state3 = states[2];

                //판매점이 db에 없으면, 신규 레코드로 저장
                //판매점이 db에 있으면, 가게 상세정보 업데이트 (단, 연금복권과 스피또 판매여부는 여기서 설정하지 않는다.)
                ShopDto shopDto = shopService.getShopById(id).map(dto -> ShopDto.of(
                        dto.id(),
                        address,
                        name,
                        tel,
                        longitude,
                        latitude,
                        true,
                        dto.l720YN(),
                        dto.spYN(),
                        state1,
                        state2,
                        state3,
                        LocalDate.now()
                )).orElseGet(() -> ShopDto.of(
                        id,
                        address,
                        name,
                        tel,
                        longitude,
                        latitude,
                        true,
                        false,
                        false,
                        state1,
                        state2,
                        state3,
                        LocalDate.now()
                ));

                shopService.save(shopDto);
            }

            curPageNo++;
        }
    }

    /**
     * 로또 판매 중단 판매점 처리
     */
    private void setShopWithdrawL645() {
        //스크랩핑 일자가 과거면서 l645YN = true 인 목록은 판매 중단 처리
        Set<ShopDto> shopDtos = shopService.getShopByL645YNAndScrapedDt(true, LocalDate.now());

        for (ShopDto shopDto : shopDtos) {
            if (shopDto.id() == 51100000) continue; //동행복권 사이트는 scrap 목록에 포함되지 않기에 제외

            shopService.save(ShopDto.of(
                    shopDto.id(),
                    shopDto.address(),
                    shopDto.name(),
                    shopDto.tel(),
                    shopDto.longitude(),
                    shopDto.latitude(),
                    false,
                    shopDto.l720YN(),
                    shopDto.spYN(),
                    shopDto.state1(),
                    shopDto.state2(),
                    shopDto.state3(),
                    shopDto.scrapedDt()
            ));
        }
    }

    /**
     * 주소에 대한 위도, 경도 조회
     *
     * @param address 주소
     * @param jsOpenPopWindow 동행복권 위치보기 팝업 js
     * @return 위도, 경도
     */
    public double[] getGeoCoordinateByAddress(String address, String jsOpenPopWindow) {
        double longitude;
        double latitude;

        //최우선으로 카카오 주소 검색 API 결과의 위도, 경도를 획득한다.
        //정확히 1개의 결과가 반환되었을 때만 정확한 좌표로 판단하고 적용하기로 한다.
        KakaoSearchAddressResponse searchAddressResponse = kakaoLocalApiService.requestSearchAddress(address);
        if (searchAddressResponse.getMeta().getTotalCount() == 1) {
            longitude = searchAddressResponse.getDocs().get(0).getLongitude();
            latitude = searchAddressResponse.getDocs().get(0).getLatitude();
        } else {
            //차선책으로 동행복권에서 제공하는 위치보기 팝업창에서 위도, 경도를 획득한다.
            chromeDriverService.procJavaScript(jsOpenPopWindow, 200);

            chromeDriverService.switchMainWindow(false);
            longitude = Double.parseDouble(
                    chromeDriverService.getElementByCssSelector("body > form > input[type=hidden]:nth-child(2)").getAttribute("value"));
            latitude = Double.parseDouble(
                    chromeDriverService.getElementByCssSelector("body > form > input[type=hidden]:nth-child(1)").getAttribute("value"));
            chromeDriverService.switchMainWindow(true);
        }

        return new double[]{longitude, latitude};
    }

    /**
     * 위도, 경도에 대한 행정구역 정보 조회
     * @param longitude 위도
     * @param latitude 경도
     * @return 행정구역 정보 (시.도/시.군.구/읍.면.동)
     */
    public String[] getStateByGeoCoordinate(double longitude, double latitude) {
        String state1 = ""; //시.도
        String state2 = ""; //시.군.구
        String state3 = ""; //읍.면.동

        KakaoGeoCoordinateToRegionResponse geoCoordinateToRegionResponse = kakaoLocalApiService.requestGeoCoordinateToRegion(longitude, latitude);
        if (geoCoordinateToRegionResponse.getMeta().getTotalCount() > 0) {
            //행정동 행정구역 데이터가 있는지 체크
            Optional<GeoCoordinateToRegionDocsDto> hTypeRegion = geoCoordinateToRegionResponse.getDocs().stream()
                    .filter(doc -> doc.getRegionType().equals("H"))
                    .findFirst();

            Optional<GeoCoordinateToRegionDocsDto> bTypeRegion = geoCoordinateToRegionResponse.getDocs().stream()
                    .filter(doc -> doc.getRegionType().equals("B"))
                    .findFirst();

            //행정동 데이터가 없으면 법정동 데이터로 설정
            state1 = hTypeRegion.orElse(bTypeRegion.orElse(new GeoCoordinateToRegionDocsDto())).getRegionDepthName1();
            state2 = hTypeRegion.orElse(bTypeRegion.orElse(new GeoCoordinateToRegionDocsDto())).getRegionDepthName2();
            state3 = hTypeRegion.orElse(bTypeRegion.orElse(new GeoCoordinateToRegionDocsDto())).getRegionDepthName3();
        }

        return new String[]{state1, state2, state3};
    }
}
