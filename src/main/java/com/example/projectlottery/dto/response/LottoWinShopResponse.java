package com.example.projectlottery.dto.response;

import com.example.projectlottery.domain.LottoWinShop;

public record LottoWinShopResponse(
        Long shopId, //해당 판매점 상세 페이지 링크를 생성하기 위함
        Integer rank,
        Integer no,
        String shopName,
        String lottoPurchaseType,
        String displayAddress
) {

    public static LottoWinShopResponse of(Long shopId, Integer rank, Integer no, String shopName, String lottoPurchaseType, String displayAddress) {
        return new LottoWinShopResponse(shopId, rank, no, shopName, lottoPurchaseType, displayAddress);
    }

    public static LottoWinShopResponse from(LottoWinShop entity) {
        return LottoWinShopResponse.of(
                entity.getShop().getId(),
                entity.getRank(),
                entity.getNo(),
                entity.getShop().getName(),
                entity.getLottoPurchaseType().getDescription(),
                entity.getDisplayAddress()
        );
    }
}
