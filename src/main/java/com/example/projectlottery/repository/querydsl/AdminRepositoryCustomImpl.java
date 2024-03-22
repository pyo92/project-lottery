package com.example.projectlottery.repository.querydsl;

import com.example.projectlottery.domain.*;
import com.example.projectlottery.dto.response.APIResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class AdminRepositoryCustomImpl extends QuerydslRepositorySupport implements AdminRepositoryCustom {

    private final QShop shop;
    private final QLotto lotto;
    private final QLottoPrize lottoPrize;
    private final QLottoWinShop lottoWinShop;
    private final QRegion region;

    public AdminRepositoryCustomImpl() {
        super(Object.class);

        shop = QShop.shop;
        lotto = QLotto.lotto;
        lottoPrize = QLottoPrize.lottoPrize;
        lottoWinShop = QLottoWinShop.lottoWinShop;
        region = QRegion.region;
    }

    /**
     * Scrap api 목록과 영향도 있는 테이블의 최종 업데이트 일시 조회 - 관리자용
     * @return Scrap api 목록 (+ 최종 업데이트 일시)
     */
    @Override
    public List<APIResponse> getAllAPIModifiedAt() {
        List<APIResponse> result = new ArrayList<>();

        //scrap API url 은 실제 db 에서 관리하는 정보는 아니다.
        //다만, 영향을 미치는 테이블의 최종 업데이트 일시와 묶어서 조회하기 위해 각 쿼리 결과를 합친다.

        result.addAll(
                from(shop)
                        .select(Projections.constructor(APIResponse.class,
                                Expressions.asString("/scrap/L645/shop"),
                                Expressions.asString("shop"),
                                shop.modifiedAt.max()))
                        .fetch()
        );

        result.addAll(
                from(lotto)
                        .select(Projections.constructor(APIResponse.class,
                                Expressions.asString("/scrap/L645/win/number"),
                                Expressions.asString("lotto"),
                                lotto.modifiedAt.max()))
                        .fetch()
        );

        result.addAll(
                from(lottoPrize)
                        .select(Projections.constructor(APIResponse.class,
                                Expressions.asString("/scrap/L645/win/prize"),
                                Expressions.asString("lotto_prize"),
                                lottoPrize.modifiedAt.max()))
                        .fetch()
        );

        result.addAll(
                from(lottoWinShop)
                        .select(Projections.constructor(APIResponse.class,
                                Expressions.asString("/scrap/L645/win/shop"),
                                Expressions.asString("lotto_win_shop"),
                                lottoWinShop.modifiedAt.max()))
                        .fetch()
        );

        return result;
    }


    /**
     * Selenium chrome driver 목록 조회 - 관리자용
     * @return Selenium chrome driver 목록
     */
    @Override
    public List<String> getAllSeleniumChromeDriver() {
        List<String> result = new ArrayList<>();

        //selenium chrome driver 는 실제 db 에서 관리하는 정보는 아니다.
        //다만, 편의상 일관성을 위해 단순 string text 목록을 select 하도록 했다.
        //따라서, 가장 영향도 없는 region 테이블에서 1건만 조회하도록 limit 처리하여 사용했다.
        result.addAll(
                from(region)
                        .select(Expressions.asString("Selenium-for-scrap"))
                        .limit(1L)
                        .fetch()
        );

        result.addAll(
                from(region)
                        .select(Expressions.asString("Selenium-for-purchase"))
                        .limit(1L)
                        .fetch()
        );

        return result;
    }
}
