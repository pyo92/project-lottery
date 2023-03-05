package com.example.projectlottery.dto.response.lotto;

import com.example.projectlottery.domain.Lotto;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

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
        Set<LottoPrizeResponse> lottoPrizes,
        Set<LottoWinShopResponse> lotto1stWinShops,
        Set<LottoWinShopResponse> lotto2ndWinShops
) {

    public static LottoResponse of(Long drawNo, String drawDt, int number1, int number2, int number3, int number4, int number5, int number6, int bonus, Set<LottoPrizeResponse> lottoPrizes, Set<LottoWinShopResponse> lottoWinShops1st, Set<LottoWinShopResponse> lottoWinShops2nd) {
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
                        .collect(Collectors.toCollection(() ->
                                new TreeSet<>(Comparator.comparing(LottoPrizeResponse::rank)))), //등위로 정렬
                entity.getLottoWinShops().stream()
                        .filter(lottoWinShop -> lottoWinShop.getRank() == 1)
                        .map(LottoWinShopResponse::from)
                        .collect(Collectors.toCollection(() ->
                                new TreeSet<>(Comparator.comparing(LottoWinShopResponse::no)))), //순번으로 정렬 (1등)
                entity.getLottoWinShops().stream()
                        .filter(lottoWinShop -> lottoWinShop.getRank() == 2)
                        .map(LottoWinShopResponse::from)
                        .collect(Collectors.toCollection(() ->
                                new TreeSet<>(Comparator.comparing(LottoWinShopResponse::no)))) //순번으로 정렬 (2등)
        );
    }
}
