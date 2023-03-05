package com.example.projectlottery.dto.response.lotto;

import com.example.projectlottery.domain.LottoWinShop;

import java.util.stream.Collectors;

public record LottoWinShopResponse(
        Long shopId, //해당 판매점 상세 페이지 링크를 생성하기 위함
        Integer no,
        String shopName,
        String lottoPurchaseType,
        String displayAddress,
        long count1stWin,
        long count2ndWin
) {

    public static LottoWinShopResponse of(Long shopId, Integer no, String shopName, String lottoPurchaseType, String displayAddress, long count1stWin, long count2ndWin) {
        return new LottoWinShopResponse(shopId, no, shopName, lottoPurchaseType, displayAddress, count1stWin, count2ndWin);
    }

    public static LottoWinShopResponse from(LottoWinShop entity) {
        long count1stWin = entity.getShop().getLottoWinShops().stream()
                .filter(lottoWinShop -> lottoWinShop.getRank() == 1)
                .collect(Collectors.toUnmodifiableSet()).size();
        long count2ndWin = entity.getShop().getLottoWinShops().stream()
                .filter(lottoWinShop -> lottoWinShop.getRank() == 2)
                .collect(Collectors.toUnmodifiableSet()).size();

        return LottoWinShopResponse.of(
                entity.getShop().getId(),
                entity.getNo(),
                entity.getShop().getName(),
                entity.getLottoPurchaseType().getDescription(),
                entity.getDisplayAddress(),
                count1stWin,
                count2ndWin
        );
    }
}
