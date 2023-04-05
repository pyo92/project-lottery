package com.example.projectlottery.dto.response;

import com.example.projectlottery.domain.Lotto;
import com.example.projectlottery.dto.response.querydsl.QShopSummary;

import java.util.List;

/**
 *  로또추첨결과 페이지에 포함되는 모든 정보를 담은 response (for redis cache too)
 */
public record LottoResponse(
        Long drawNo,
        String drawDt,
        int number1,
        int number2,
        int number3,
        int number4,
        int number5,
        int number6,
        int bonus,
        List<LottoPrizeResponse> lottoPrizes,
        List<QShopSummary> lotto1stWinShops,
        List<QShopSummary> lotto2ndWinShops
) {

    public static LottoResponse of(Long drawNo, String drawDt, int number1, int number2, int number3, int number4, int number5, int number6, int bonus, List<LottoPrizeResponse> lottoPrizes, List<QShopSummary> lottoWinShops1st, List<QShopSummary> lottoWinShops2nd) {
        return new LottoResponse(
                drawNo,
                drawDt,
                number1,
                number2,
                number3,
                number4,
                number5,
                number6,
                bonus,
                lottoPrizes,
                lottoWinShops1st,
                lottoWinShops2nd
        );
    }

    public static LottoResponse from(Lotto entity, List<LottoPrizeResponse> lottoPrizes, List<QShopSummary> lottoWinShops1st, List<QShopSummary> lottoWinShops2nd) {
        return LottoResponse.of(
                entity.getDrawNo(),
                entity.getDrawDt().toString(),
                entity.getLottoWinNumber().getNumber1(),
                entity.getLottoWinNumber().getNumber2(),
                entity.getLottoWinNumber().getNumber3(),
                entity.getLottoWinNumber().getNumber4(),
                entity.getLottoWinNumber().getNumber5(),
                entity.getLottoWinNumber().getNumber6(),
                entity.getLottoWinNumber().getNumberB(),
                lottoPrizes,
                lottoWinShops1st,
                lottoWinShops2nd
        );
    }
}
