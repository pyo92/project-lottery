package com.example.projectlottery.dto.response.lotto;

import com.example.projectlottery.domain.Lotto;

import java.util.Comparator;
import java.util.List;

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
        List<LottoWinShopResponse> lotto1stWinShops,
        List<LottoWinShopResponse> lotto2ndWinShops
) {

    public static LottoResponse of(Long drawNo, String drawDt, int number1, int number2, int number3, int number4, int number5, int number6, int bonus, List<LottoPrizeResponse> lottoPrizes, List<LottoWinShopResponse> lottoWinShops1st, List<LottoWinShopResponse> lottoWinShops2nd) {
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

    public static LottoResponse from(Lotto entity) {
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
                entity.getLottoPrizes().stream()
                        .map(LottoPrizeResponse::from)
                        .sorted(Comparator.comparing(LottoPrizeResponse::rank))
                        .toList(), //등위로 정렬
                entity.getLottoWinShops().stream()
                        .filter(lottoWinShop -> lottoWinShop.getRank() == 1)
                        .map(LottoWinShopResponse::from)
                        .sorted(Comparator.comparing(LottoWinShopResponse::no))
                        .toList(), //순번으로 정렬 (1등)
                entity.getLottoWinShops().stream()
                        .filter(lottoWinShop -> lottoWinShop.getRank() == 2)
                        .map(LottoWinShopResponse::from)
                        .sorted(Comparator.comparing(LottoWinShopResponse::no))
                        .toList() //순번으로 정렬 (2등)
        );
    }
}
