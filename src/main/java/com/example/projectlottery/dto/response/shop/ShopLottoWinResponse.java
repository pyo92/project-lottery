package com.example.projectlottery.dto.response.shop;

import com.example.projectlottery.domain.LottoWinShop;

public record ShopLottoWinResponse(
        Long drawNo,
        Integer no, //같은 회차 여러 개 당첨 복권 배출 시, set 에 표시하기 위한 용도
        String drawDt,
        int number1,
        int number2,
        int number3,
        int number4,
        int number5,
        int number6,
        int bonus,
        long winAmountPerGame,
        String lottoPurchaseType
) {

    public static ShopLottoWinResponse of(Long drawNo, Integer no, String drawDt, int number1, int number2, int number3, int number4, int number5, int number6, int bonus, long winAmountPerGame, String lottoPurchaseType) {
        return new ShopLottoWinResponse(drawNo, no, drawDt, number1, number2, number3, number4, number5, number6, bonus, winAmountPerGame, lottoPurchaseType);
    }

    public static ShopLottoWinResponse from(LottoWinShop entity) {
        return ShopLottoWinResponse.of(
                entity.getLotto().getDrawNo(),
                entity.getNo(),
                entity.getLotto().getDrawDt().toString(),
                entity.getLotto().getLottoWinNumber().getNumber1(),
                entity.getLotto().getLottoWinNumber().getNumber2(),
                entity.getLotto().getLottoWinNumber().getNumber3(),
                entity.getLotto().getLottoWinNumber().getNumber4(),
                entity.getLotto().getLottoWinNumber().getNumber5(),
                entity.getLotto().getLottoWinNumber().getNumber6(),
                entity.getLotto().getLottoWinNumber().getNumberB(),
                entity.getLotto().getLottoPrizes().stream()
                        .filter(lottoPrize -> lottoPrize.getRank() == 1)
                        .findFirst()
                        .get()
                        .getWinAmountPerGame(),
                entity.getLottoPurchaseType().getDescription()
        );
    }
}
