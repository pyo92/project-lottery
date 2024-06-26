package com.example.projectlottery.api.service;

import com.example.projectlottery.api.dto.GeoCoordinateToRegionDocsDto;
import com.example.projectlottery.api.dto.response.KakaoGeoCoordinateToRegionResponse;
import com.example.projectlottery.api.dto.response.KakaoSearchAddressResponse;
import com.example.projectlottery.domain.type.ScrapStateType;
import com.example.projectlottery.dto.ShopDto;
import com.example.projectlottery.service.RedisTemplateService;
import com.example.projectlottery.service.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class ScrapLotteryShopService {

    private static final String URL_SHOP_LOTTO = "https://dhlottery.co.kr/store.do?method=sellerInfo645";

    private final SeleniumScrapService seleniumScrapService;
    private final ShopService shopService;
    private final KakaoLocalApiService kakaoLocalApiService;

    private final RedisTemplateService redisTemplateService;

    private int retryCount;

    private static final int MAX_RETRY_COUNT = 5;
    private static final long RETRY_DELAY = 120000; // 2분

    /**
     * 로또 6/45 판매점 정보 스크랩핑
     *
     * @param state 시.도
     */
    @Retryable(
            retryFor = { NoSuchElementException.class, JavascriptException.class },
            maxAttempts = MAX_RETRY_COUNT,
            backoff = @Backoff(delay = RETRY_DELAY),
            recover = "recoverScrap"
    )
    public void scrapShopL645(String state) {
        log.info("=== Started scrapShopL645() : {}", LocalDateTime.now());

        try {
            //TODO: 추후, scrap 프로젝트도 별도로 분리되면
            // redis 가 아닌 다른 방법으로 사용중인지 여부를 관리하는 것으로 바꾸도록 하자. (아마 rest api 통신 할 듯?)
            String url = "/scrap/L645/shop";
            redisTemplateService.saveScrapRunningInfo(url, state);

            seleniumScrapService.openWebDriver();
            seleniumScrapService.openUrl(URL_SHOP_LOTTO);

            ScrapStateType shopScrapStateType = ScrapStateType.valueOf(state);

            log.info("=== Processing scrapShopL645() - {} : {}", state, LocalDateTime.now());

            if (shopScrapStateType == ScrapStateType.ALL) {
                Arrays.stream(ScrapStateType.values())
                        .filter(scrapStateType -> scrapStateType.ordinal() > 0)
                        .toList()
                        .forEach(this::scrapShopL645ByState);

                //스크랩핑 완료 후, 판매 중단 대상 판매점 처리
                //전국(ALL) 으로 스크랩핑 한 경우, 나머지 모든 ScrapStateType 대해 처리한다.
                Arrays.stream(ScrapStateType.values())
                        .filter(scrapStateType -> scrapStateType.ordinal() > 0)
                        .toList()
                        .forEach(this::setShopWithdrawL645);

            } else {
                scrapShopL645ByState(shopScrapStateType);
                setShopWithdrawL645(shopScrapStateType);
            }

            //스크랩핑을 통해 판매점에 대한 정보가 변경되었기에 cache clear
            redisTemplateService.flushAllCache();

            log.info("=== Success scrapShopL645() : {}", LocalDateTime.now());

        } catch (NoSuchElementException e) {
            log.warn("=== Failed by NoSuchElementException - scrapShopL645(), retry 2 min later... ({}/{})", ++retryCount, MAX_RETRY_COUNT);

            throw e;

        } catch (JavascriptException e) {
            log.warn("=== Failed by JavascriptException - scrapShopL645(), retry 2 min later... ({}/{})", ++retryCount, MAX_RETRY_COUNT);

            throw e;

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }finally {
            redisTemplateService.deleteScrapRunningInfo();
            seleniumScrapService.closeWebDriver();
        }
    }

    /**
     * 로또 6/45 판매점 정보 스크랩핑
     *
     * @param scrapStateType 시.도 Enum
     */
    private void scrapShopL645ByState(ScrapStateType scrapStateType) throws NoSuchElementException, JavascriptException {
        String css;
        String js;

        //타 시.도로 이동해 구 단위 필터 해제
        if (scrapStateType == ScrapStateType.SEOUL) {
            css = "#mainMenuArea > a:nth-child(2)";
            js = seleniumScrapService.getElementByCssSelector(css).getAttribute("onclick");
            seleniumScrapService.procJavaScript(js);
        }

        //대상 시.도로 이동
        js = "$.searchData('" + scrapStateType.getDescription() + "', '')";
        seleniumScrapService.procJavaScript(js);

        //항상 1 페이지부터 시작할 수 있도록 설정
        js = "$.selfSubmit(1);";
        seleniumScrapService.procJavaScript(js);

        //현재 pagination view 에 [끝 페이지] 링크가 존재하는지 확인
        css = "#pagingView > a";
        int firstPagingViewItemCount = seleniumScrapService.getElementsByCssSelector(css).size();

        //마지막 페이지 번호 획득
        int lastPageNo = firstPagingViewItemCount;
        if (firstPagingViewItemCount > 10) {
            css = "#pagingView > a.go.end";
            lastPageNo = Integer.parseInt(seleniumScrapService.getElementByCssSelector(css)
                    .getAttribute("href")
                    .replaceAll("[^0-9]", ""));
        }

        int curPageNo = 1;
        while (curPageNo <= lastPageNo) {
            //항상 1 페이지부터 시작할 수 있도록 세팅
            js = "$.selfSubmit(" + curPageNo + ");";
            seleniumScrapService.procJavaScript(js);

            css = "#resultTable > tbody > tr";
            List<WebElement> Shops = seleniumScrapService.getElementsByCssSelector(css);

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
    private void setShopWithdrawL645(ScrapStateType scrapStateType) {
        //스크랩핑 일자가 과거면서 l645YN = true 인 목록은 판매 중단 처리
        Set<ShopDto> shopDtos = shopService.getShopByL645YNAndScrapedDt(scrapStateType, true, LocalDate.now());

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
    public double[] getGeoCoordinateByAddress(String address, String jsOpenPopWindow) throws NoSuchElementException, JavascriptException {
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
            seleniumScrapService.procJavaScript(jsOpenPopWindow);

            seleniumScrapService.switchMainWindow(false);
            longitude = Double.parseDouble(
                    seleniumScrapService.getElementByCssSelector("body > form > input[type=hidden]:nth-child(2)").getAttribute("value"));
            latitude = Double.parseDouble(
                    seleniumScrapService.getElementByCssSelector("body > form > input[type=hidden]:nth-child(1)").getAttribute("value"));
            seleniumScrapService.switchMainWindow(true);
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

    @Recover
    private void recoverScrap(NoSuchElementException e, String state) {
        retryCount = 0; //retry count 리셋

        throw e; //예외를 그대로 상위 객체로 던져준다.
    }

    @Recover
    private void recoverScrap(JavascriptException e, String state) {
        retryCount = 0; //retry count 리셋

        throw e; //예외를 그대로 상위 객체로 던져준다.
    }
}
